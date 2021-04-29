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
 *
 */

package com.barclays.absa.banking.expressCashSend.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.databinding.CashSendOnceOffConfirmFragmentBinding
import com.barclays.absa.banking.express.cashSend.performCashSend.CashSendPaymentViewModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationDataModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.dto.CashSendValidationResponse
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler
import com.barclays.absa.banking.sureCheck.ProcessSureCheck
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.TextFormatUtils.formatCashSendAmount
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedCellphoneNumber
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.ExpressBaseRepository

class CashSendOnceOffConfirmFragment : CashSendBaseFragment(R.layout.cash_send_once_off_confirm_fragment) {
    private lateinit var cashSendValidationResponse: CashSendValidationResponse
    private lateinit var cashSendValidationDataModel: CashSendValidationDataModel
    private lateinit var beneficiaryName: String
    private lateinit var cashSendAmount: String
    private lateinit var atmPin: String

    private val binding by viewBinding(CashSendOnceOffConfirmFragmentBinding::bind)
    private val handler = Handler(Looper.getMainLooper())
    private val performCashSendViewModel by activityViewModels<CashSendPaymentViewModel>()

    private lateinit var cashSendSureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashSendSureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                handler.postDelayed({ requestCashSendOnceOffResult() }, 250)
            }

            override fun onSureCheckFailed() {
                launchSureCheckFailedResultScreen("")
            }

            override fun onSureCheckFailed(errorMessage: String) {
                launchSureCheckFailedResultScreen(errorMessage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_OVERVIEW_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
        arguments?.let {
            val navArgs =CashSendOnceOffConfirmFragmentArgs.fromBundle(it)
            atmPin = navArgs.atmpin
        }

        setToolBar(R.string.confirm_cash_send)
        onPopulateView()
        setupTalkBack()
    }

    fun setupTalkBack() {
        setContentDescriptions(binding.cashSendBeneficiaryPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_recipient, binding.cashSendBeneficiaryPrimaryContentAndLabelView.contentTextViewValue))
        setContentDescriptions(binding.cashSendMobileNumberPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_mobile_Number, binding.cashSendMobileNumberPrimaryContentAndLabelView.contentTextViewValue))
        setContentDescriptions(binding.cashSendAccountNumberSecondaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_account, binding.cashSendAccountNumberSecondaryContentAndLabelView.contentTextViewValue))
        setContentDescriptions(binding.cashSendReferenceSecondaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_my_reference, binding.cashSendReferenceSecondaryContentAndLabelView.contentTextViewValue))
        setContentDescriptions(binding.cashSendAmountPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_amount_sending, AccessibilityUtils.getTalkBackRandValueFromString(binding.cashSendAmountPrimaryContentAndLabelView.contentTextViewValue)))
        setContentDescriptions(binding.cashSendPinPrimaryContentAndLabelView, getString(R.string.talkback_cashsend_overview_my_atm_access_pin, AccessibilityUtils.getTalkbackPinNumberFromString(binding.cashSendPinPrimaryContentAndLabelView.contentTextViewValue)))
        binding.cashSendConfirmButton.contentDescription = getString(R.string.talkback_cashsend_overview_confirm_cashsend)
    }

    private fun setContentDescriptions(contentAndLabelView: BaseContentAndLabelView, contentDescription: String) {
        contentAndLabelView.contentDescription = contentDescription
        contentAndLabelView.labelTextView.contentDescription = contentDescription
        contentAndLabelView.contentTextView.contentDescription = contentDescription
    }

    private fun onPopulateView() {
        cashSendValidationResponse = cashSendViewModel.cashSendValidationResponse
        cashSendValidationDataModel = cashSendViewModel.cashSendValidationDataModel
        val accountType = cashSendViewModel.sourceAccount.accountType
        val accountNumber = cashSendValidationResponse.sourceAccount.toFormattedAccountNumber()
        cashSendAmount = cashSendValidationDataModel.paymentAmount
        binding.cashSendAccountNumberSecondaryContentAndLabelView.setContentText("$accountType ($accountNumber)")
        beneficiaryName = "${cashSendValidationDataModel.beneficiaryName} ${cashSendValidationDataModel.beneficiarySurname}"
        binding.cashSendBeneficiaryPrimaryContentAndLabelView.setContentText(beneficiaryName)
        binding.cashSendMobileNumberPrimaryContentAndLabelView.setContentText(cashSendValidationResponse.recipientCellphoneNumber.toFormattedCellphoneNumber())
        val amount = Amount(cashSendAmount)
        binding.cashSendAmountPrimaryContentAndLabelView.setContentText(formatCashSendAmount(amount))
        binding.cashSendReferenceSecondaryContentAndLabelView.setContentText(cashSendValidationDataModel.statementReference)
        binding.cashSendPinPrimaryContentAndLabelView.setContentText(atmPin)
        binding.cashSendConfirmButton.setOnClickListener { v ->
            preventDoubleClick(v)
            if (appCacheService.isInNoPrimaryDeviceState()) {
                appCacheService.setReturnToScreen(CashSendOnceOffConfirmFragment::class.java) //TODO: more to come in this regards
                showNoPrimaryDeviceScreen()
            } else {
                requestCashSendOnceOffResult()
            }
        }
    }

    private fun requestCashSendOnceOffResult() {
        performCashSendViewModel.performCashSend(cashSendValidationResponse, cashSendValidationDataModel)
        performCashSendViewModel.cashSendPaymentResponseLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })
        ExpressSureCheckHandler.processSureCheck = object : ProcessSureCheck {
            override fun onSureCheckProcessed() {
                dismissProgressDialog()
                performCashSendViewModel.performCashSend(cashSendValidationResponse, cashSendValidationDataModel)
                performCashSendViewModel.cashSendPaymentResponseLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    launchTransactionResultScreen(it)
                })
            }
        }
    }

    private fun launchTransactionResultScreen(baseResponse: BaseResponse) {
        // TODO: 2021/01/11 confirm that this latest surecheck implementation
        var resultMessages = ""
        baseResponse.header.resultMessages.forEach {
            resultMessages += it
        }
        /*if (BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION.equals(onceOffCashSendResultMessage, ignoreCase = true)) {
            tagCashSend("OnceOffAuthorisationPending_ScreenDisplayed")
            Intent(baseActivity, DualAuthPaymentPendingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(BMBConstants.TRANSACTION_TYPE, BMBConstants.TRANSACTION_TYPE_CASH_SEND)
                startActivity(this)
            }
            val airtimeIntent = Intent(baseActivity, DualAuthPaymentPendingActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(BMBConstants.TRANSACTION_TYPE, BMBConstants.TRANSACTION_TYPE_CASH_SEND)
            startActivity(airtimeIntent)
        } else {*/
        /*val cashSendResultIntent = Intent(baseActivity, CashSendResultActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(BMBConstants.CASHSEND_AMOUNT, cashSendAmount)
                .putExtra(BMBConstants.ATM_ACCESS_PIN_KEY, atmPin)
                .putExtra(IS_CASH_SEND_PLUS, cashSendViewModel.isCashSendPlus.value)
                .putExtra(AppConstants.RESULT, cashSendOnceOffResult)
        startActivity(cashSendResultIntent)*/
        Toast.makeText(baseActivity, "Success -> $resultMessages", Toast.LENGTH_LONG).show()
        navigate(CashSendOnceOffConfirmFragmentDirections.actionCashSendOnceOffConfirmFragmentToCashSendResultFragment())
        //}
    }

    private fun buildResultIntent(message: String): Intent {
        // TODO: 2021/01/11 use/navigate generic result fragment
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            Intent(baseActivity, CashSendActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND)
                startActivity(this)
            }
        }
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { loadAccountsAndGoHome() }

        return Intent(baseActivity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            putExtra(GenericResultActivity.IS_FAILURE, true)
            if (message.isNotBlank()) {
                putExtra(GenericResultActivity.SUB_MESSAGE_STRING, message)
            }
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.btn_make_another_cash_send)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
        }
    }

    private fun launchSureCheckFailedResultScreen(responseMessage: String) {
        val message: String = if (responseMessage.contains("Payment limit exceeded")) {
            getString(R.string.cashsend_more_than_available_limit)
        } else {
            responseMessage
        }
        val intent = buildResultIntent(message).putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed)
        startActivity(intent)
    }
}