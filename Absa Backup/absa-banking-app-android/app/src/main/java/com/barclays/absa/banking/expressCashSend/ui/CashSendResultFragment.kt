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
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.CashSendResultFragmentBinding
import com.barclays.absa.banking.express.shared.dto.CashSendInstructionType
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.PASS_CASHSEND
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations.Companion.generalFailure
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations.Companion.paymentSuccess
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil.tagCashSend
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toRandAmount
import java.util.regex.Pattern

class CashSendResultFragment : BaseFragment(R.layout.cash_send_result_fragment) {
    private val binding by viewBinding(CashSendResultFragmentBinding::bind)

    private lateinit var cashSendAmount: String
    private lateinit var atmAccessPin: String
    private lateinit var phoneNumber: String
    private lateinit var customerName: String
    private lateinit var beneficiaryName: String
    private lateinit var messageToSend: String
    private val cashSendViewModel by activityViewModels<CashSendViewModel>()

    private var isCashSendToSelf = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachEventHandlers()
        onPopulateView()
        hideToolBar()
    }

    private fun attachEventHandlers() {
        binding.goBackHomeButton.setOnClickListener {
            loadAccountsAndGoHome()
        }

        binding.shareCashSendButton.setOnClickListener {
            requestForSharePIN(atmAccessPin)
        }
    }

    private fun onPopulateView() {
        with(cashSendViewModel.cashSendValidationDataModel) {
            isCashSendToSelf = cashSendViewModel.isCashSendToSelf
            phoneNumber = recipientCellphoneNumber.toFormattedCellphoneNumber()
            cashSendAmount = paymentAmount.toRandAmount()
            atmAccessPin = cashSendViewModel.accessPin
            this@CashSendResultFragment.beneficiaryName = if (isCashSendToSelf) {
                getString(R.string.yourself)
            } else {
                "$beneficiaryName $beneficiarySurname"
            }
        }
        var resultMessages = ""
        cashSendViewModel.cashSendPaymentResponse.header.resultMessages.forEach {
            resultMessages += "$it\n"
        }

        if (cashSendViewModel.cashSendValidationDataModel.instructionType == CashSendInstructionType.CASHSEND_ONCE_OFF.value && cashSendViewModel.cashSendPaymentResponse.paymentNumber > 0) {
            tagCashSend("OnceOffSuccess_ScreenDisplayed")
            binding.shareCashSendButton.visibility = View.VISIBLE
            showResultAnimation(true)
            binding.resultSuccessHeaderTextView.text = getString(R.string.cash_send_success)
            binding.resultSuccessContentTextView.text = getString(R.string.cash_send_success_text, beneficiaryName, cashSendAmount, phoneNumber)
            binding.resultSuccessPinTextView.text = getString(R.string.cash_send_atm_pin_text, atmAccessPin)
            binding.resultSuccessPinTextView.visibility = View.VISIBLE

            binding.resultSuccessContentTextView.visibility = View.VISIBLE
            binding.resultSuccessPinTextView.visibility = View.VISIBLE

            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND)
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
        } else if (cashSendViewModel.cashSendPaymentResponse.paymentNumber == 0) {
            tagCashSend("OnceOffFailure_ScreenDisplayed")
            binding.shareCashSendButton.visibility = View.GONE
            binding.resultSuccessHeaderTextView.text = getString(R.string.cash_send_fail)
            binding.resultSuccessPinTextView.visibility = View.GONE
            showResultAnimation(true)
            binding.resultSuccessContentTextView.text = resultMessages.trim()
        }

        /* TODO: this needs to be moved to where an add ben is done */
        /*is AddBeneficiaryCashSendConfirmationObject -> {
            addBeneficiarySuccessObject = responseObject

            if (CONST_SUCCESS.equals(addBeneficiarySuccessObject.status, ignoreCase = true)) {
                BaseActivity.mScreenName = CASHSEND_SUCCESSFUL_CONST
                BaseActivity.mSiteSection = MANAGE_CASHSEND_BENEFICIARIES_CONST
                AnalyticsUtils.getInstance().trackCustomScreenView(CASHSEND_SUCCESSFUL_CONST, MANAGE_CASHSEND_BENEFICIARIES_CONST, TRUE_CONST)

                showResultAnimation(true)
                binding.shareCashSendButton.visibility = View.GONE
                binding.resultSuccessHeaderTextView.text = getString(R.string.add_new_prepaid_success_msg)
                binding.resultSuccessContentTextView.text = addBeneficiarySuccessObject.msg
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND)
                AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
                tagCashSend("CashSend_BeneficiaryAddSuccess_ScreenDisplayed")
            } else {
                BaseActivity.mScreenName = ADD_BENEFICIARY_UNSUCCESSFUL_CONST
                BaseActivity.mSiteSection = MANAGE_CASHSEND_BENEFICIARIES_CONST
                AnalyticsUtils.getInstance().trackCustomScreenView(CASHSEND_UNSUCCESSFUL_CONST, MANAGE_CASHSEND_BENEFICIARIES_CONST, TRUE_CONST)

                showResultAnimation(false)
                binding.shareCashSendButton.visibility = View.GONE
                binding.resultSuccessHeaderTextView.text = getString(R.string.unable_to_save_prepaid_ben)
                binding.resultSuccessContentTextView.text = addBeneficiarySuccessObject.msg
                tagCashSend("CashSend_BeneficiaryAddFailure_ScreenDisplayed")
            }
        }*/
        else if (cashSendViewModel.cashSendValidationDataModel.instructionType == CashSendInstructionType.CASHSEND_BENEFICIARY.value && cashSendViewModel.cashSendPaymentResponse.paymentNumber > 0) {
            if (isCashSendToSelf) {
                tagCashSend("SendCashToMyselfSuccess_ScreenDisplayed")
                binding.shareCashSendButton.visibility = View.GONE
            } else {
                tagCashSend("SendCashToBeneficiarySuccess_ScreenDisplayed")
            }

            binding.resultSuccessHeaderTextView.text = getString(R.string.cash_send_success)
            binding.resultSuccessContentTextView.text = getString(R.string.cash_send_success_text, beneficiaryName, cashSendAmount, phoneNumber)
            binding.resultSuccessPinTextView.text = getString(R.string.cash_send_atm_pin_text, atmAccessPin)
            binding.resultSuccessPinTextView.visibility = View.VISIBLE
            showResultAnimation(true)
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_CASHSEND)
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false)

        } else if (cashSendViewModel.cashSendPaymentResponse.paymentNumber == 0) {
            if (isCashSendToSelf) tagCashSend("SendCashToMyselfFailure_ScreenDisplayed") else tagCashSend("SendCashToBeneficiaryFailure_ScreenDisplayed")
            binding.shareCashSendButton.visibility = View.GONE
            binding.resultSuccessHeaderTextView.text = getString(R.string.cash_send_fail)
            binding.resultSuccessPinTextView.visibility = View.GONE
            showResultAnimation(false)

            //TODO: this regex stuff was probably very specific to AOL; probably not needed here
            val regexPattern = "(\\d+)-([a-zA-Z. ]+)(\\([A-Z]\\d+\\))"
            val pattern = Pattern.compile(regexPattern)

            val matcher = pattern.matcher(resultMessages)
            if (pattern.matcher(resultMessages).matches() && matcher.find()) {
                binding.resultSuccessContentTextView.text = matcher.group(2)
            } else {
                binding.resultSuccessContentTextView.text = resultMessages
            }
        }
    }

    private fun requestForSharePIN(atmPin: String) {
        customerName = CustomerProfileObject.instance.customerName.toString()
        messageToSend = String.format(getString(R.string.share_atm_pin_message), beneficiaryName, cashSendAmount, atmPin, customerName)

        with(Intent(ACTION_SEND)) {
            putExtra(EXTRA_TEXT, messageToSend)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            type = "text/plain"
            startActivity(this)
        }
    }

    private fun showResultAnimation(result: Boolean) {
        val animation = if (result) paymentSuccess else generalFailure
        binding.cashSendResultLottieAnimationView.setAnimation(animation)
    }
}