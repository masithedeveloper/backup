/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.presentation.sureCheckV2

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationState
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.viewModel

object SureCheckHandler {

    private var countDownTimer: CountDownTimer? = null
    private lateinit var sureCheckViewModel: SureCheckViewModel

    private const val POLL_EVERY_SECONDS = 3
    private const val COUNT_DOWN_INTERVAL = 1000
    private const val MILLIS_DURATION = 60000

    private val appCacheService: IAppCacheService = getServiceInterface()
    var isActive = false

    fun start() {
        start(null)
    }

    fun start(sureCheckView: SureCheckHandlerView?) {
        if (isActive) {
            return
        }

        val topMostActivity = BMBApplication.getInstance().topMostActivity as BaseActivity
        sureCheckViewModel = topMostActivity.viewModel()

        val sureCheckDelegate: SureCheckDelegate? = appCacheService.getSureCheckDelegate()

        sureCheckViewModel.transactionVerificationStateLiveData = MutableLiveData()
        sureCheckViewModel.resetPolling()

        countDownTimer = object : CountDownTimer(MILLIS_DURATION.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / COUNT_DOWN_INTERVAL).toInt()
                sureCheckView?.timerTick(secondsLeft)
                if (secondsLeft % POLL_EVERY_SECONDS == 0) {
                    sureCheckViewModel.poll()
                }

                if (BuildConfigHelper.STUB && secondsLeft < 55) {
                    stopTimer()
                    sureCheckDelegate?.onSureCheckProcessed()
                }
            }

            override fun onFinish() {
                stopTimer()
                sureCheckView?.displayResendOption()
            }
        }

        sureCheckViewModel.transactionVerificationStateLiveData.observe(topMostActivity, Observer {
            when (it) {
                TransactionVerificationState.PROCESSED -> {
                    sureCheckDelegate?.onSureCheckProcessed()
                    sureCheckView?.sureCheckProcessed()
                }
                TransactionVerificationState.REJECTED -> {
                    sureCheckDelegate?.onSureCheckRejected()
                    sureCheckView?.showSureCheckRejected()
                }
                TransactionVerificationState.FAILED -> {
                    sureCheckDelegate?.onSureCheckFailed()
                    sureCheckView?.sureCheckFailed()
                }
                TransactionVerificationState.RESENDREQUIRED -> {
                    sureCheckView?.displayResendOption()
                }
                TransactionVerificationState.SEND_VERIFICATION -> {
                    sureCheckView?.close()
                }
                else -> return@Observer
            }
        })

        countDownTimer?.start()
        isActive = true
    }

    fun stopTimer() {
        isActive = false
        countDownTimer?.cancel()
        (BMBApplication.getInstance().topMostActivity as BaseActivity).dismissProgressDialog()
    }

    fun resendSureCheck() {
        sureCheckViewModel.resendSureCheck()
    }
}

interface SureCheckHandlerView {
    fun displayResendOption()
    fun timerTick(secondsLeft: Int)
    fun sureCheckProcessed()
    fun sureCheckFailed()
    fun showSureCheckRejected()
    fun close()
}
