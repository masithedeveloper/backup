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
package com.barclays.absa.banking.expressCashSend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryResult
import com.barclays.absa.banking.cashSend.ui.CashSendResultActivity
import com.barclays.absa.banking.databinding.CashsendOverviewFragmentBinding
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.AnalyticsUtil.tagCashSend
import com.barclays.absa.utils.SureCheckUtils.isResponseSuccessSureCheck
import com.barclays.absa.utils.TextFormatUtils.formatCashSendAmount
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedCellphoneNumber

class CashSendOverviewFragment : CashSendBaseFragment(R.layout.cashsend_overview_fragment) {

    private val binding by viewBinding(CashsendOverviewFragmentBinding::bind)
    private var isCashSendToSelf = false
    private var shouldUseResultStub = false

    private lateinit var cashSendBeneficiaryResult: CashSendBeneficiaryResult
    private lateinit var cashSendAmount: String

    //TODO: Needs to be moved once we implement services
    private val cashSendResponseListener: ExtendedResponseListener<CashSendBeneficiaryResult> = object : ExtendedResponseListener<CashSendBeneficiaryResult>() {
        override fun onSuccess(successResponse: CashSendBeneficiaryResult) {
            cashSendBeneficiaryResult = successResponse
            cashSendSureCheckDelegate.processSureCheck(this@CashSendOverviewFragment, successResponse) { launchResultScreen() }
        }
    }

    private val cashSendSureCheckDelegate: SureCheckDelegate = object : SureCheckDelegate(activity) {
        override fun onSureCheckProcessed() {
            shouldUseResultStub = true
            requestSendBenCashSendResult()
        }

        override fun onSureCheckRejected() {
            setupFailureScreen(false)
        }

        override fun onSureCheckFailed() {
            setupFailureScreen(true)
        }

        override fun onSureCheckCancelled() {
            super.onSureCheckCancelled(baseActivity)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(resources.getString(R.string.cash_send_confirm))
        getDeviceProfilingInteractor().notifyTransaction()

        cashSendResponseListener.setView(this)

        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_OVERVIEW_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
        onPopulateView()

        binding.cashSendConfirmButton.setOnClickListener { v: View ->
            preventDoubleClick(v)
            if (appCacheService.isInNoPrimaryDeviceState()) {
                appCacheService.setReturnToScreen(CashSendOverviewFragment::class.java)
                showNoPrimaryDeviceScreen()
            } else {
                requestSendBenCashSendResult()
            }
        }
        setupTalkBack()
    }

    fun setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            setContentDescriptions(binding.cashSendBeneficiaryPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_recipient, binding.cashSendBeneficiaryPrimaryContentAndLabelView.contentTextViewValue))
            setContentDescriptions(binding.cashSendMobileNumberPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_mobile_Number, binding.cashSendMobileNumberPrimaryContentAndLabelView.contentTextViewValue))
            setContentDescriptions(binding.cashSendAccountNumberSecondaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_account, binding.cashSendAccountNumberSecondaryContentAndLabelView.contentTextViewValue))
            setContentDescriptions(binding.cashSendReferenceSecondaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_my_reference, binding.cashSendReferenceSecondaryContentAndLabelView.contentTextViewValue))
            setContentDescriptions(binding.cashSendAmountPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_amount_sending, AccessibilityUtils.getTalkBackRandValueFromString(binding.cashSendAmountPrimaryContentAndLabelView.contentTextViewValue)))
            setContentDescriptions(binding.cashSendPinPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_my_atm_access_pin, AccessibilityUtils.getTalkbackPinNumberFromString(binding.cashSendPinPrimaryContentAndLabelView.contentTextViewValue)))
        }

        binding.cashSendConfirmButton.contentDescription = getString(R.string.talkback_cashsend_overview_confirm_cashsend)
    }

    private fun setContentDescriptions(contentAndLabelView: BaseContentAndLabelView, contentDescription: String) {
        with(contentAndLabelView) {
            this.contentDescription = contentDescription
            labelTextView.contentDescription = contentDescription
            contentTextView.contentDescription = contentDescription
        }
    }

    private fun onPopulateView() {
        // TODO: Redo all getIntent extras
        // isCashSendToSelf = this.getIntent().getBooleanExtra(BMBConstants.IS_SELF, false)

        /*if (intent.extras != null && intent.extras.containsKey(BMBConstants.BENEFICIARY_IMG_DATA)) {
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PREPAID_PURCHASE_OVERVIEW_CONST, BMBConstants.PREPAID_CONST, BMBConstants.TRUE_CONST)
        }*/
        // if (!this.intent.getStringExtra(ATM_ACCESS_PIN_KEY).equals("", ignoreCase = true)) binding.cashSendPin.setContentText(this.intent.getStringExtra(ATM_ACCESS_PIN_KEY))

        val accountType = cashSendBeneficiaryConfirmation.accountType
        val accountNumber = cashSendBeneficiaryConfirmation.fromAccountNumber.toFormattedAccountNumber()
        binding.cashSendAccountNumberSecondaryContentAndLabelView.setContentText("$accountType ($accountNumber)")
        if (isCashSendToSelf) {
            binding.cashSendBeneficiaryPrimaryContentAndLabelView.setContentText(instance.customerName)
        } else {
            binding.cashSendBeneficiaryPrimaryContentAndLabelView.setContentText(cashSendBeneficiaryConfirmation.beneficiaryName)
        }
        cashSendAmount = cashSendBeneficiaryConfirmation.amount.toString()
        binding.cashSendMobileNumberPrimaryContentAndLabelView.setContentText(cashSendBeneficiaryConfirmation.cellNumber.toFormattedCellphoneNumber())
        binding.cashSendAmountPrimaryContentAndLabelView.setContentText(formatCashSendAmount(cashSendBeneficiaryConfirmation.amount))
        binding.cashSendReferenceSecondaryContentAndLabelView.setContentText(cashSendBeneficiaryConfirmation.myReference)
    }

    private fun requestSendBenCashSendResult() {
        // TODO : Call express perform CashSend
        //cashSendInteractor.performCashSendToBeneficiary(isCashSendPlus, cashSendBeneficiaryConfirmation.txnReference, shouldUseResultStub, cashSendResponseListener)
    }

    private fun launchResultScreen() {
        context?.let { context ->
            if (isResponseSuccessSureCheck(cashSendBeneficiaryResult, context)) {
                val cashSendBeneficiaryResultMessage = cashSendBeneficiaryResult.message
                if (cashSendBeneficiaryResultMessage != null && cashSendBeneficiaryResultMessage.contains("Payment limit exceeded")) {
                    cashSendBeneficiaryResult.message = getString(R.string.cashsend_more_than_available_limit)
                }
                if (BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION.equals(cashSendBeneficiaryResultMessage, ignoreCase = true)) {
                    if (isCashSendToSelf) {
                        tagCashSend("SendCashToMyselfAuthorisationPending_ScreenDisplayed")
                    } else {
                        tagCashSend("SendCashToBeneficiaryAuthorisationPending_ScreenDisplayed")
                    }
                    startActivity(Intent(context, DualAuthPaymentPendingActivity::class.java).apply {
                        putExtra(BMBConstants.TRANSACTION_TYPE, BMBConstants.TRANSACTION_TYPE_CASH_SEND)
                    })
                } else {
                    // TODO : This will need to change to Result Fragment
                    startActivity(Intent(context, CashSendResultActivity::class.java).apply {
                        putExtra(BMBConstants.CASHSEND_AMOUNT, cashSendAmount)
                        // TODO: GET ACCESS PIN
                        // cashSendResultIntent.putExtra(BMBConstants.ATM_ACCESS_PIN_KEY, context.getStringExtra(BMBConstants.ATM_ACCESS_PIN_KEY))
                        putExtra(BMBConstants.IS_SELF, isCashSendToSelf)
                        putExtra(CashSendActivity.IS_CASH_SEND_PLUS, isCashSendPlus)
                        putExtra(AppConstants.RESULT, cashSendBeneficiaryResult)
                    })
                }
            }
        }
    }

    //TODO: change to fragment possibly
    private fun setupFailureScreen(isSureCheckFailure: Boolean) {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { loadAccountsAndGoHome() }

        startActivity(Intent(context, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
            if (isSureCheckFailure) {
                putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed)
            } else {
                putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected)
            }
        })
    }
}