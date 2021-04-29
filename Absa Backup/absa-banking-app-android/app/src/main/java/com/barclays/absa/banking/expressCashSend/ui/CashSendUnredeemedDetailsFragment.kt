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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.CashSendUnredeemedDetailsFragmentBinding
import com.barclays.absa.banking.express.cashSend.cancelCashSendPayment.CancelCashSendPaymentViewModel
import com.barclays.absa.banking.express.cashSend.cancelCashSendPayment.dto.CancelCashSendPaymentRequest
import com.barclays.absa.banking.express.cashSend.cashSendResendWithdrawalPin.CashSendResendWithdrawalPinViewModel
import com.barclays.absa.banking.express.cashSend.cashSendResendWithdrawalPin.dto.ResendPinRequest
import com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin.CashSendUpdateATMPinViewModel
import com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin.dto.CashSendUpdateAtmPinRequest
import com.barclays.absa.banking.express.cashSend.dto.CashSendType
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto.CashSendPaymentTransaction
import com.barclays.absa.banking.express.shared.dto.CashSendInstructionType
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity.Companion.IS_CASH_SEND_PLUS
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.AnalyticsUtil.tagCashSend
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.extensions.viewBinding
import com.google.android.material.snackbar.Snackbar
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toRandAmount
import styleguide.utils.extensions.toSentenceCase
import java.util.*

class CashSendUnredeemedDetailsFragment : BaseFragment(R.layout.cash_send_unredeemed_details_fragment), CashSendUnredeemedPinChangeDialogFragment.PinChangeInterface {

    private val binding by viewBinding(CashSendUnredeemedDetailsFragmentBinding::bind)

    private val cashSendUpdateATMPinViewModel by activityViewModels<CashSendUpdateATMPinViewModel>()
    private val cancelCashSendPaymentViewModel by activityViewModels<CancelCashSendPaymentViewModel>()
    private val cashSendResendWithdrawalPinViewModel by activityViewModels<CashSendResendWithdrawalPinViewModel>()

    private lateinit var transactionUnredeemedObject: CashSendPaymentTransaction
    private lateinit var fromAccountText: String
    private lateinit var fromAccountNo: String
    private lateinit var modifiedAtmPin: String
    private lateinit var accountName: String
    private lateinit var accountNumber: String

    private var isCashSendPlus = false
    private val sendSMS = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.transaction_details)

        BaseActivity.mScreenName = CASHSEND_UNREDEEMED_TRANSACTION_DETAILS_CONST
        BaseActivity.mSiteSection = CASHSEND_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(CASHSEND_UNREDEEMED_TRANSACTION_DETAILS_CONST, CASHSEND_CONST, TRUE_CONST)

        arguments?.let { extras ->
            if (extras.get(UNREDEEMED_TRANSACTION) != null) {
                with(extras) {
                    transactionUnredeemedObject = getParcelable(UNREDEEMED_TRANSACTION)!!
                    fromAccountText = getString(ACCOUNT_NUMBER_TO_DISPLAY) ?: ""
                    fromAccountNo = getString(ACCOUNT_NUMBER) ?: ""
                    isCashSendPlus = getBoolean(IS_CASH_SEND_PLUS, false)
                }
            }
        }

        initViews()
        attachEventHandlers()
        onPopulateView()
        setupTalkBack()
    }

    private fun initViews() {
        if (isCashSendPlus) {
            binding.changeAtmPinButton.visibility = View.GONE
            binding.resendAtmPinButton.visibility = View.GONE
        }
    }

    private fun attachEventHandlers() {
        binding.cancelCashSendButton.setOnClickListener {
            tagCashSend("UnredeemedTransactionDetails_CancelClicked")
            displayCancelCashSendAlert()
        }

        binding.resendAtmPinButton.setOnClickListener {
            tagCashSend("UnredeemedTransactionDetails_ResendSmsClicked")
            displayResendWithdrawalAlert()
        }

        binding.changeAtmPinButton.setOnClickListener {
            tagCashSend("UnredeemedTransactionDetails_ChangePinClicked")
            displayPinChangeDialog()
        }
    }

    private fun onPopulateView() {
        with(transactionUnredeemedObject) {
            binding.beneficiaryInfoPrimaryContentAndLabelView.setContentText("$beneficiaryShortName $beneficiarySurname")
            binding.mobileNumberSecondaryContentAndLabelView.setContentText(recipientCellphoneNumber.toFormattedCellphoneNumber())
            binding.accountNumberSecondaryContentAndLabelView.setContentText("$fromAccountText (${fromAccountNo.toFormattedAccountNumber()})")
            binding.cashSendAmountPrimaryContentAndLabelView.setContentText(paymentAmount.toRandAmount())
            binding.referenceSecondaryContentAndLabelView.setContentText(statementReference)
            binding.dateSecondaryContentAndLabelView.setContentText(DateTimeHelper.formatDate(transactionDateTime))
            binding.pinPrimaryContentAndLabelView.setContentText(getString(R.string.unreedemed_cashsend_empty_pin))
        }
    }

    private fun setupTalkBack() {
        val beneficiaryName = binding.beneficiaryInfoPrimaryContentAndLabelView.contentTextViewValue
        val mobileNumber = binding.mobileNumberSecondaryContentAndLabelView.contentTextViewValue
        val cashSendAmount = AccessibilityUtils.getTalkBackRandValueFromString(binding.cashSendAmountPrimaryContentAndLabelView.contentTextViewValue)
        val accountInfo = AccessibilityUtils.splitAccountNumberFromName(binding.accountNumberSecondaryContentAndLabelView.contentTextViewValue)
        if (accountInfo.size > 2) {
            accountName = accountInfo.first()
            accountNumber = accountInfo[1]
        }
        val cashSendReference: String = binding.referenceSecondaryContentAndLabelView.contentTextViewValue
        val cashSendDate: String = binding.dateSecondaryContentAndLabelView.contentTextViewValue

        binding.beneficiaryInfoPrimaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_beneficiary_name, beneficiaryName)
        binding.beneficiaryInfoPrimaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_beneficiary_name, beneficiaryName)
        binding.mobileNumberSecondaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_mobile_number, mobileNumber)
        binding.mobileNumberSecondaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_mobile_number, mobileNumber)
        binding.cashSendAmountPrimaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_amount_sent, cashSendAmount)
        binding.cashSendAmountPrimaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_amount_sent, cashSendAmount)
        //binding.accountNumberSecondaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_account_number, accountName, accountNumber)
        //binding.accountNumberSecondaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_account_number, accountName, accountNumber)
        binding.referenceSecondaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_reference_number, cashSendReference)
        binding.referenceSecondaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_reference_number, cashSendReference)
        binding.dateSecondaryContentAndLabelView.labelTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_date, cashSendDate)
        binding.dateSecondaryContentAndLabelView.contentTextView.contentDescription = getString(R.string.talkback_unredeemed_cashsends_transaction_date, cashSendDate)
        binding.changeAtmPinButton.contentDescription = getString(R.string.talkback_unredeemed_cashsend_change_atm_pin)
        binding.cancelCashSendButton.contentDescription = getString(R.string.talkback_unredeemed_cashsend_cancel)
        binding.resendAtmPinButton.contentDescription = getString(R.string.talkback_unredeemed_cashsend_resend_withdraw_sms)
    }

    private fun displayCancelCashSendAlert() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .message(getString(R.string.cancel_cashsend))
                .positiveDismissListener { _, _ ->
                    requestCancelCashSendPayment()
                })
    }

    private fun requestCancelCashSendPayment() {
        cancelCashSendPaymentViewModel.cancelCashSendPayment(CancelCashSendPaymentRequest().apply {
            uniqueEFT = transactionUnredeemedObject.uniqueEFT
            paymentNumber = transactionUnredeemedObject.paymentNumber
            sourceAccount = transactionUnredeemedObject.sourceAccount
            paymentAmount = transactionUnredeemedObject.paymentAmount
            recipientCellphoneNumber = transactionUnredeemedObject.recipientCellphoneNumber
        })
        cancelCashSendPaymentViewModel.cancelCashSendPaymentResponse.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
            dismissProgressDialog()
        }
        cancelCashSendPaymentViewModel.failureLiveData.observe(viewLifecycleOwner) {
            dismissProgressDialog()
        }
    }

    private fun displayResendWithdrawalAlert() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.resend_cashsend_withrawal_title))
                .message(getString(R.string.resend_cashsend_withdrawal_msg))
                .positiveDismissListener { _, _ ->
                    requestWithdrawalSms()
                })
    }

    private fun displayPinChangeDialog() {
        navigate(CashSendUnredeemedDetailsFragmentDirections.actionCashSendUnredeemedDetailsFragmentToCashSendUnredeemedPinChangeDialogFragment(sendSMS))
    }

    private fun requestWithdrawalSms() {
        cashSendResendWithdrawalPinViewModel.resendCashSendWithdrawalPin(ResendPinRequest().apply {
            uniqueEFT = transactionUnredeemedObject.uniqueEFT
            paymentNumber = transactionUnredeemedObject.paymentNumber.toString()
            sourceAccount = transactionUnredeemedObject.sourceAccount
        })
        cashSendResendWithdrawalPinViewModel.resendPinResponse.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            val smsResendMessage = it.toString()
            val smsResendStatus = getString(R.string.status_success).toSentenceCase()
            showSMSDialog(smsResendMessage, smsResendStatus)
        }
    }

    private fun showSMSDialog(resendSMSMessage: String, resendSMSStatus: String) {
        tagCashSend("UnredeemedResendSmsSuccess_NoticeDisplayed")
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(resendSMSStatus)
                .message(resendSMSMessage)
                .build())
    }

    override fun pinChanged(newPin: String) {
        showProgressDialog()
        cashSendUpdateATMPinViewModel.updateAtmPin(CashSendUpdateAtmPinRequest().apply {
            beneficiaryNumber = transactionUnredeemedObject.beneficiaryNumber.toString()
            recipientCellphoneNumber = transactionUnredeemedObject.recipientCellphoneNumber
            instructionType = CashSendInstructionType.CASHSEND_BENEFICIARY.value
            beneficiaryName = transactionUnredeemedObject.beneficiaryName
            beneficiarySurname = transactionUnredeemedObject.beneficiarySurname
            beneficiaryShortName = transactionUnredeemedObject.beneficiaryShortName
            statementReference = transactionUnredeemedObject.statementReference
            transactionDateAndTime = DateTimeHelper.formatDate(Date(), DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM)
            paymentAmount = transactionUnredeemedObject.paymentAmount
            sourceAccount = transactionUnredeemedObject.sourceAccount
            cifKey = transactionUnredeemedObject.cifKey
            tieBreaker = transactionUnredeemedObject.tieBreaker
            beneficiaryNumber = transactionUnredeemedObject.beneficiaryNumber.toString()
            cashSendType = CashSendType.SINGLE.value
            pin = newPin
            uniqueEFT = transactionUnredeemedObject.uniqueEFT
            paymentNumber = transactionUnredeemedObject.paymentNumber
        })

        cashSendUpdateATMPinViewModel.updateAtmPinResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            view?.let {
                val resultSnackbar = Snackbar.make(it, R.string.atm_updated_successfully, Snackbar.LENGTH_INDEFINITE)
                resultSnackbar.setAction(R.string.ok) { resultSnackbar.dismiss() }.show()
                binding.pinPrimaryContentAndLabelView.setContentText(newPin)
                modifiedAtmPin = it.toString()
                if (sendSMS) {
                    requestWithdrawalSms()
                }
            }
        })
    }
}