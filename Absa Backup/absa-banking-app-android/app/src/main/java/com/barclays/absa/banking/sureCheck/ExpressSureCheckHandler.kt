/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.sureCheck

import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.RequestSecurityNotificationViewModel
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.dto.SecurityNotificationRequest
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.dto.SecurityNotificationResponse
import com.barclays.absa.banking.express.sureCheck.resendSecurityNotification.ResendSecurityNotificationViewModel
import com.barclays.absa.banking.express.sureCheck.validateSecurityNotification.ValidateSecurityNotificationViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckHandlerView
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler.cancelSureCheck
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler.navigateToTransactionRejectedActivity
import com.barclays.absa.utils.viewModel

object ExpressSureCheckHandler {
    private lateinit var requestSecurityNotificationViewModel: RequestSecurityNotificationViewModel
    private lateinit var validateSecurityNotificationViewModel: ValidateSecurityNotificationViewModel
    private lateinit var resendSecurityNotificationViewModel: ResendSecurityNotificationViewModel

    lateinit var processSureCheck: ProcessSureCheck
    lateinit var securityNotificationRequest: SecurityNotificationRequest
    lateinit var securityNotificationResponse: SecurityNotificationResponse

    private var countDownTimer: CountDownTimer? = null

    private const val POLL_EVERY_SECONDS = 3
    private const val COUNT_DOWN_INTERVAL = 1000
    private const val MILLIS_DURATION = 60000

    private val topMostActivity: BaseActivity
        get() = BMBApplication.getInstance().topMostActivity as BaseActivity

    var isActive = false

    fun start(sureCheckView: SureCheckHandlerView? = null) {
        if (isActive) {
            return
        }

        requestSecurityNotificationViewModel = topMostActivity.viewModel()
        validateSecurityNotificationViewModel = topMostActivity.viewModel()

        requestSecurityNotificationViewModel.requestSecurityNotificationLiveData = MutableLiveData()
        requestSecurityNotificationViewModel.requestSecurityNotification(securityNotificationRequest)

        countDownTimer = object : CountDownTimer(MILLIS_DURATION.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / COUNT_DOWN_INTERVAL).toInt()
                sureCheckView?.timerTick(secondsLeft)
                if (secondsLeft % POLL_EVERY_SECONDS == 0) {
                    with(validateSecurityNotificationViewModel) {
                        validateSecurityNotification(securityNotificationRequest)
                        validateSecurityNotificationLiveData.observe(topMostActivity) {
                            when (it.sureCheckResult) {
                                ExpressSureCheckStatus.SURE_CHECK_PROCESSED.sureCheckStatus -> {
                                    stopTimer()
                                    sureCheckView?.close()
                                    processSureCheck.onSureCheckProcessed()
                                }
                                ExpressSureCheckStatus.SURE_CHECK_REJECTED.sureCheckStatus -> {
                                    stopTimer()
                                    sureCheckView?.showSureCheckRejected()
                                    processSureCheck.onSureCheckRejected()
                                }
                                ExpressSureCheckStatus.SURE_CHECK_FAILED.sureCheckStatus -> {
                                    sureCheckView?.sureCheckFailed()
                                    processSureCheck.onSureCheckFailed()
                                }
                            }
                        }
                        failureLiveData.observe(topMostActivity) {}
                    }
                }

                if (BuildConfigHelper.STUB && secondsLeft < 55) {
                    stopTimer()
                    processSureCheck.onSureCheckProcessed()
                }
            }

            override fun onFinish() {
                stopTimer()
                sureCheckView?.displayResendOption()
            }
        }

        requestSecurityNotificationViewModel.requestSecurityNotificationLiveData.observe(topMostActivity) {
            topMostActivity.dismissProgressDialog()
            securityNotificationResponse = it
            if (it.resendAttemptsRemaining > 0 && it.sureCheckResult == ExpressSureCheckStatus.SURE_CHECK_PROCESSING.sureCheckStatus) {
                countDownTimer?.start()
            } else if (it.sureCheckResult == ExpressSureCheckStatus.SURE_CHECK_PROCESSED.sureCheckStatus) {
                stopTimer()
                sureCheckView?.close()
                processSureCheck.onSureCheckProcessed()
            }
        }

        isActive = true
    }

    fun processSureCheck(securityNotificationRequest: SecurityNotificationRequest) {
        this.securityNotificationRequest = securityNotificationRequest
        val secondFactorState: SecondFactorState = SecondFactorState.fromValue(CustomerProfileObject.instance.secondFactorState)
        securityNotificationRequest.securityNotificationType = secondFactorState.name
        when (secondFactorState) {
            SecondFactorState.SURECHECKV1 -> initiateV1CountDownScreen()
            SecondFactorState.SURECHECKV2 -> initiateV2CountDownScreen()
            SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE -> topMostActivity.showNoPrimaryDeviceScreen()
            SecondFactorState.SURECHECKV2_SECURITYCODE -> navigateToSureCheckFallback()
            else -> cancelSureCheck()
        }
    }

    private fun initiateV1CountDownScreen() {
        if (!isActive && !topMostActivity.isFinishing) {
            val fragmentTransaction = topMostActivity.supportFragmentManager.beginTransaction()
            val sureCheckCountDownDialogFragment = ExpressSureCheckV1CountDownFragment.newInstance()
            fragmentTransaction.add(sureCheckCountDownDialogFragment, ExpressSureCheckV1CountDownFragment::class.java.simpleName)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    private fun initiateV2CountDownScreen() {
        if (!isActive && !topMostActivity.isFinishing) {
            val fragmentTransaction = topMostActivity.supportFragmentManager.beginTransaction()
            val sureCheckCountDownDialogFragment = ExpressSureCheckCountdownFragment.newInstance()
            fragmentTransaction.add(sureCheckCountDownDialogFragment, ExpressSureCheckCountdownFragment::class.java.simpleName)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    private fun navigateToSureCheckFallback() {
        topMostActivity.startActivity(Intent(topMostActivity, ExpressOfflineOtpActivity::class.java))
    }

    fun navigateToTransactionRejectedActivity(isFraud: Boolean) {
        val intent: Intent
        if (topMostActivity.appCacheService.getUserLoggedInStatus()) {
            intent = IntentFactory.getRejectedResultScreen(topMostActivity, isFraud)
            topMostActivity.startActivity(intent)
        } else {
            intent = IntentFactory.getRejectedPreLoginResultScreen(topMostActivity, isFraud)
            topMostActivity.startActivity(intent)
            topMostActivity.finish()
        }
    }

    fun stopTimer() {
        isActive = false
        countDownTimer?.cancel()
        topMostActivity.dismissProgressDialog()
    }

    fun resendSureCheck() {
        resendSecurityNotificationViewModel = topMostActivity.viewModel()
        with(resendSecurityNotificationViewModel) {
            resendSecurityNotificationLiveData = MutableLiveData()
            validateSecurityNotification(securityNotificationRequest)
            resendSecurityNotificationLiveData.observe(topMostActivity) {
                topMostActivity.dismissProgressDialog()
                securityNotificationResponse = it
                securityNotificationRequest.securityNotificationType = it.securityNotificationType
                when (it.securityNotificationType) {
                    SureCheckNotificationType.SURECHECKV2.name -> countDownTimer?.start()
                    SureCheckNotificationType.SURECHECKV2_FALLBACK.name -> navigateToSureCheckFallback()
                }
            }
        }
    }

    fun cancelSureCheck(activity: BaseActivity? = topMostActivity) {
        stopTimer()
        if (activity != null && !activity.isFinishing) {
            activity.finish()
            activity.dismissProgressDialog()
        }
    }
}

interface ProcessSureCheck {
    fun onSureCheckProcessed()
    fun onSureCheckFailed() = cancelSureCheck()
    fun onSureCheckRejected() = navigateToTransactionRejectedActivity(SureCheckAuth2faActivity.isFraud)
}