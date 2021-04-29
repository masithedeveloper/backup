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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.sureCheck.validateSecurityNotification.ValidateSecurityNotificationViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.generateTokens.OfflineOtpGenerator
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AccessibilityUtils
import kotlinx.android.synthetic.main.activity_2fa_offline_otp.*
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule

class ExpressOfflineOtpActivity : BaseActivity(R.layout.activity_2fa_offline_otp) {
    private val validateSecurityNotificationViewModel by viewModels<ValidateSecurityNotificationViewModel>()

    companion object {
        private const val MAX_NUMBER_OF_RETRIES = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uniqueNumberNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.please_enter_a_token_number))

        setToolBarWithNoBackButton("")

        if (appCacheService.isPrimarySecondFactorDevice()) {
            val offlineOtp = OfflineOtpGenerator.generateOfflineToken()
            uniqueNumberNormalInputView.selectedValue = offlineOtp
        }

        continueButton.setOnClickListener {
            if (uniqueNumberNormalInputView.validate()) {
                ExpressSureCheckHandler.securityNotificationRequest.requestToken = uniqueNumberNormalInputView.selectedValueUnmasked
                showProgressDialog()
                validateSecurityNotificationViewModel.validateSecurityNotificationLiveData = MutableLiveData()
                validateSecurityNotificationViewModel.validateSecurityNotification(ExpressSureCheckHandler.securityNotificationRequest)
                validateSecurityNotificationViewModel.validateSecurityNotificationLiveData.observe(this) {
                    dismissProgressDialog()
                    when (it.sureCheckResult) {
                        ExpressSureCheckStatus.SURE_CHECK_REJECTED.sureCheckStatus -> {
                            if (it.oneTimePinRetriesRemaining > 0) {
                                val retriesSoFar: Int = MAX_NUMBER_OF_RETRIES - it.oneTimePinRetriesRemaining
                                val errorText = getString(R.string.incorrect_token_number, retriesSoFar, MAX_NUMBER_OF_RETRIES)
                                uniqueNumberNormalInputView.setError(errorText)
                            } else {
                                navigateToErrorResultActivity(R.string.token_number_invalid, getString(R.string.you_need_to_make_a_new_transaction))
                            }
                        }
                        ExpressSureCheckStatus.SURE_CHECK_PROCESSED.sureCheckStatus -> {
                            ExpressSureCheckHandler.processSureCheck.onSureCheckProcessed()
                            finish()
                        }
                        ExpressSureCheckStatus.SURE_CHECK_FAILED.sureCheckStatus -> {
                            navigateToErrorResultActivity(R.string.token_number_invalid, getString(R.string.you_need_to_make_a_new_transaction))
                        }
                    }

                }
            }
        }

        setupTalkBack()
    }

    fun setupTalkBack() {
        AccessibilityUtils.announceErrorFromTextWidget(instructionTertiaryContentAndLabelView.contentTextView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cancel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sureCheckDelegate = appCacheService.getSureCheckDelegate()
        sureCheckDelegate?.onSureCheckCancelled()
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToErrorResultActivity(headerId: Int, message: String) {
        val intent = IntentFactory.getFailureResultScreenBuilder(this@ExpressOfflineOtpActivity, headerId, message).build()
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            if (BMBApplication.getInstance().userLoggedInStatus) {
                loadAccountsAndGoHome()
            } else {
                ExpressSureCheckHandler.cancelSureCheck()
            }
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // disable back press
    }
}