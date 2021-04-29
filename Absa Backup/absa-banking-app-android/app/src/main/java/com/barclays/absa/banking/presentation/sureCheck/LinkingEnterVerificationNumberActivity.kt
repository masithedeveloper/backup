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

package com.barclays.absa.banking.presentation.sureCheck

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LinkingEnterVerificationNumberActivityBinding
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import styleguide.utils.extensions.toMaskedCellphoneNumber
import styleguide.utils.extensions.toMaskedEmailAddress

class LinkingEnterVerificationNumberActivity : ConnectivityMonitorActivity(), EnterVerificationNumberView {
    private val binding by viewBinding(LinkingEnterVerificationNumberActivityBinding::inflate)

    private val enteredVerificationNumber: String? = null
    private lateinit var presenter: EnterVerificationNumberPresenter
    private var sureCheckDelegate: SureCheckDelegate? = null
    private val isUserLoggedIn = appCacheService.getSecureHomePageObject() != null
    private var cancelClickAnalyticsTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.linking_enter_verification_number_activity)

        if (enteredVerificationNumber != null) {
            binding.enterCodeNormalInputView.selectedValue = enteredVerificationNumber
        }

        binding.submitButton.setOnClickListener {
            presenter.submitButtonInvoked(binding.enterCodeNormalInputView.selectedValue)
        }

        binding.resendButton.setOnClickListener {
            presenter.resendVerficationNumber()
        }

        AnalyticsUtils.getInstance().trackCustomScreenView(TVN_FALLBACK_CONST, CONFIRM_CONST, TRUE_CONST)
        presenter = EnterVerificationNumberPresenter(this)

        val enterCodeNormalInputViewTitleText = getString(R.string.sure_check_enter_code_rvn)
        var enterCodeNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_rvn)
        var resendButtonTitle = getString(R.string.sure_check_resend_button_rvn)
        var toolbarTitle = getString(R.string.sure_check_toolbar_title_rvn)

        if ("S".equals(appCacheService.getSureCheckNotificationMethod(), ignoreCase = true)) {
            cancelClickAnalyticsTag = "ID&VLinkDevice_SMSPleaseEnterTVNScreen_CancelButtonClicked"
            val sureCheckCellphoneNumber = appCacheService.getSureCheckCellphoneNumber()
            val maskedCellphoneNumber = sureCheckCellphoneNumber.toMaskedCellphoneNumber()
            binding.tvnTitleTextView.text = getString(R.string.linking_tvn_sms_message, maskedCellphoneNumber)
            if (appCacheService.isLinkingFlow()) {
                enterCodeNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_rvn)
                toolbarTitle = getString(R.string.sure_check_toolbar_title_rvn)
                resendButtonTitle = getString(R.string.sure_check_resend_button_rvn)
            }
        } else {
            cancelClickAnalyticsTag = "ID&VLinkDevice_EmailPleaseEnterTVNScreen_CancelButtonClicked"
            val sureCheckEmail = appCacheService.getSureCheckEmail()
            val maskedEmailAddress = sureCheckEmail.toMaskedEmailAddress()
            binding.tvnTitleTextView.text = getString(R.string.linking_tvn_email_message, maskedEmailAddress)
            if (appCacheService.isLinkingFlow()) {
                enterCodeNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_rvn)
                toolbarTitle = getString(R.string.sure_check_toolbar_title_rvn)
                resendButtonTitle = getString(R.string.sure_check_resend_button_rvn)
            }
        }
        binding.enterCodeNormalInputView.setTitleText(enterCodeNormalInputViewTitleText)
        binding.enterCodeNormalInputView.setHintText(enterCodeNormalInputViewHintText)
        binding.resendButton.text = resendButtonTitle

        RebuildUtils.setupToolBar(this, toolbarTitle, R.drawable.ic_arrow_back_white, false, null)
        sureCheckDelegate = appCacheService.getSureCheckDelegate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.cancel_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_menu_item) {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, cancelClickAnalyticsTag)
            if (isUserLoggedIn) {
                onBackPressed()
            } else {
                CommonUtils.callWelcomeActivity(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showIncorrectRVNCodeMessage(retriesRemaining: Int) {
        val errorMessage = getString(R.string.verification_error_message_2fa, retriesRemaining)
        binding.enterCodeNormalInputView.setError(errorMessage)
    }

    override fun showSuccessOutcome() {
        sureCheckDelegate?.onSureCheckProcessed()
        finish()
    }

    override fun showFailureOutcome() {
        sureCheckDelegate?.onSureCheckFailed()
        finish()
    }

    override fun showValidationError(pleaseEnterValid: Int) {
        val textToUse = if (appCacheService.isLinkingFlow()) getString(R.string.sure_check_rvn) else getString(R.string.sure_check_tvn)
        val errorMessage = String.format(getString(pleaseEnterValid), textToUse.replace(":".toRegex(), ""))
        binding.enterCodeNormalInputView.setError(errorMessage)
    }

    override fun showOtpEntryScreen() {
        sureCheckDelegate?.initiateTransactionVerificationEntryScreen()
    }

    override fun showSureCheckScreen() {
        sureCheckDelegate?.initiateV1CountDownScreen()
    }

    override fun incorrectVerificationNumber(errorMessage: String?) {
        binding.enterCodeNormalInputView.setError(errorMessage)
    }

    override fun showRetriesExceeded() {
        showMessageError(getString(R.string.resend_RVN_exceeded)) { _: DialogInterface?, _: Int -> finish() }
    }
}