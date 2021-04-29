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
package com.barclays.absa.banking.paymentsRewrite.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.PaymentConfirmationFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.payments.payBeneficiary.dto.PayBeneficiaryRequest
import com.barclays.absa.banking.express.payments.payBeneficiary.dto.PayBeneficiaryResponse
import com.barclays.absa.banking.express.payments.validateOnceOffPayment.dto.ValidateOnceOffPaymentRequest
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentRequest
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler
import com.barclays.absa.banking.sureCheck.ProcessSureCheck
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.DateTimeHelper.toFormattedString
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount
import za.co.absa.networking.dto.ResponseHeader
import za.co.absa.networking.hmac.service.ExpressBaseRepository.Companion.PENDING_RESPONSE_CODE
import za.co.absa.networking.hmac.service.ExpressBaseRepository.Companion.RETRY_RESPONSE_CODE
import java.util.*

class PaymentConfirmationFragment : PaymentsBaseFragment(R.layout.payment_confirmation_fragment) {

    private val binding by viewBinding(PaymentConfirmationFragmentBinding::bind)
    private var iipReferenceNumber: String = ""

    private lateinit var beneficiaryType: BeneficiaryType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayPaymentConfirmationDetails()

        binding.payButton.setOnClickListener {
            validatePayment()

            ExpressSureCheckHandler.processSureCheck = object : ProcessSureCheck {
                override fun onSureCheckProcessed() {
                    makePayment()
                }

                override fun onSureCheckFailed() {
                    startActivity(IntentFactory.getPaymentTransactionFailedResultScreen(baseActivity))
                }
            }
        }
    }

    private fun validatePayment() {
        paymentsViewModel.validatePaymentRequest.apply {
            sourceAccountReference = paymentsViewModel.selectedBeneficiary.beneficiaryDetails.sourceAccountReference
            targetAccountReference = paymentsViewModel.selectedBeneficiary.beneficiaryDetails.targetAccountReference
            paymentAmount = paymentsViewModel.paymentAmount
            notes = "" // TODO Where do these NOTES get set?
            paymentSourceAccountNumber = paymentsViewModel.selectedSourceAccount.accountNumber

            if (!useTime) {
                paymentTransactionDateAndTime = Date()
            }
        }

        if (paymentsViewModel.isOnceOffPayment) {
            validateOnceOffPaymentViewModel.validatePayment(paymentsViewModel.validatePaymentRequest as ValidateOnceOffPaymentRequest)

            validateOnceOffPaymentViewModel.validateOnceOffPaymentResponse.observe(viewLifecycleOwner, {

                // TODO : Handle Warnings

                makePayment()
            })
        } else {
            validateBeneficiaryPaymentViewModel.validatePayment(paymentsViewModel.validatePaymentRequest as ValidatePaymentRequest)

            validateBeneficiaryPaymentViewModel.validatePaymentResponse.observe(viewLifecycleOwner, {

                // TODO : Handle Warnings

                makePayment()
            })
        }
    }

    private fun makePayment() {
        val onFailureObserver: (responseHeader: ResponseHeader) -> Unit = {
            dismissProgressDialog()
            launchFailureResultScreen(it.resultMessages.firstOrNull()?.responseMessage ?: "")
        }

        if (paymentsViewModel.isOnceOffPayment) {
            with(payOnceOffBeneficiaryViewModel) {
                failureLiveData.observe(viewLifecycleOwner, onFailureObserver)
                payBeneficiary(createPaymentRequest(validateOnceOffPaymentViewModel.validateOnceOffPaymentResponse.value))
                payOnceOffBeneficiaryResponse.observe(viewLifecycleOwner, { handleSuccessResponse(it) })
            }
        } else {
            with(payBeneficiaryViewModel) {
                failureLiveData.observe(viewLifecycleOwner, onFailureObserver)
                payBeneficiary(createPaymentRequest(validateBeneficiaryPaymentViewModel.validatePaymentResponse.value))
                payBeneficiaryResponse.observe(viewLifecycleOwner, { handleSuccessResponse(it) })
            }
        }
    }

    private fun handleSuccessResponse(payBeneficiaryResponse: PayBeneficiaryResponse) {
        dismissProgressDialog()

        when (payBeneficiaryResponse.header.statuscode) {
            PENDING_RESPONSE_CODE -> {
                // For Delayed IIP's even though the return code is zero, it does not mean that the
                //payment has been successful. A return code of 480 will be returned in this case
                // TODO: Show Pending Screen
            }
            RETRY_RESPONSE_CODE -> {
                //A status code = 475 means that there hasn't been a response from BankServ yet for the IIP and you need to retry or re-call or poll the same payment.

                makePayment() // TODO : Confirm Works
            }
            else -> launchResultScreen(payBeneficiaryResponse)
        }
    }

    private fun createPaymentRequest(validatePaymentResponse: ValidatePaymentResponse?) = PayBeneficiaryRequest().apply {
        transactionAmount = paymentsViewModel.validatePaymentRequest.paymentAmount
        validatePaymentResponse?.let {
            targetAccountNumber = it.targetAccountNumber
            paymentToken = it.paymentToken
            iipReferenceNumber = it.realTimePaymentReferenceNumber
        }
    }

    private fun displayPaymentConfirmationDetails() {
        val selectedBeneficiary = paymentsViewModel.selectedBeneficiary
        beneficiaryType = selectedBeneficiary.beneficiaryDetails.typeOfBeneficiary
        val baseValidatePaymentRequest = paymentsViewModel.validatePaymentRequest
        with(binding) {
            beneficiaryNameView.title = selectedBeneficiary.beneficiaryDetails.beneficiaryName
            accountNumberView.setLineItemViewContent(selectedBeneficiary.beneficiaryDetails.targetAccountNumber)
            amountView.title = paymentsViewModel.paymentAmount.toRandAmount()
            if (paymentsViewModel.isBillPayment) {
                bankView.visibility = View.GONE
                branchView.visibility = View.GONE
                accountTypeView.visibility = View.GONE
                theirNotificationView.visibility = View.GONE
                paymentDateView.visibility = View.GONE
            } else {
                bankView.setLineItemViewContent(selectedBeneficiary.beneficiaryDetails.bankOrInstitutionName)
                branchView.setLineItemViewContent(selectedBeneficiary.beneficiaryDetails.clearingCodeOrInstitutionCode)
                if (selectedBeneficiary.beneficiaryDetails.accountType == BeneficiaryAccountType.NONE) {
                    accountTypeView.visibility = View.GONE
                } else {
                    accountTypeView.setLineItemViewContent(getAccountTypeString(selectedBeneficiary.beneficiaryDetails.accountType))
                }
                theirNotificationView.contentTextView.text = getNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.beneficiaryNotification)
                paymentDateView.contentTextView.text = if (baseValidatePaymentRequest.immediatePayment) getString(R.string.today) else baseValidatePaymentRequest.paymentTransactionDateAndTime.toFormattedString()
            }
            fromAccountView.contentTextView.text = paymentsViewModel.selectedSourceAccount.accountNumber
            theirReferenceView.contentTextView.text = paymentsViewModel.selectedBeneficiary.beneficiaryDetails.targetAccountReference
            myReferenceView.contentTextView.text = paymentsViewModel.selectedBeneficiary.beneficiaryDetails.sourceAccountReference
            myNotificationView.contentTextView.text = getNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.ownNotification)
            paymentTypeView.contentTextView.text = when {
                baseValidatePaymentRequest.useTime -> getString(R.string.future_dated_payment)
                baseValidatePaymentRequest.immediatePayment -> getString(R.string.iip)
                else -> getString(R.string.normal_24_48)
            }
        }
    }

    //TODO: change to fragments in future
    private fun launchResultScreen(payBeneficiaryResponse: PayBeneficiaryResponse) {
        val baseValidatePaymentRequest = paymentsViewModel.validatePaymentRequest

        // TODO: Check if result works for this, We need a service change here to give a response code for this.
        if (payBeneficiaryResponse.result.contains(BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION)) {
            val subMessage = getString(R.string.auth_title)
            startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_pending, subMessage, R.string.done, false))
        } else {
            BaseActivity.mSiteSection = BMBConstants.CONFIRM_CONST
            val transactionAmount = baseValidatePaymentRequest.paymentAmount.toRandAmount()
            if (beneficiaryType == BeneficiaryType.INSTITUTIONAL) {
                val successMessage = getString(R.string.payment_success_message, transactionAmount, baseValidatePaymentRequest.beneficiaryName, baseValidatePaymentRequest.targetAccountNumber)
                if (baseValidatePaymentRequest.useTime) {
                    BaseActivity.mScreenName = BMBConstants.PAYMENT_SCHEDULE_SUCCESS_CONST
                    baseActivity.trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
                    startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_sched_success, successMessage, R.string.done, false))
                } else {
                    startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_success_title, successMessage, R.string.done, true))
                }
            } else if (baseValidatePaymentRequest.useTime) {
                BaseActivity.mScreenName = BMBConstants.PAYMENT_SCHEDULE_SUCCESS_CONST
                baseActivity.trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
                val successMessage = getString(R.string.payment_success_message, transactionAmount, baseValidatePaymentRequest.beneficiaryName, baseValidatePaymentRequest.targetAccountNumber)
                startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_sched_success, successMessage, R.string.done, false))
            } else if (baseValidatePaymentRequest.immediatePayment) {
                BaseActivity.mScreenName = BMBConstants.PAYMENT_IIP_DELAYED_SUCCESS_CONST
                baseActivity.trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
                startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_sched_success, getString(R.string.iip_reference_number, iipReferenceNumber), R.string.done, true))
            } else {
                BaseActivity.mScreenName = if (baseValidatePaymentRequest.immediatePayment) BMBConstants.PAYMENT_IIP_SUCCESS_CONST else BMBConstants.PAYMENT_NORMAL_CONST
                baseActivity.trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
                val successMessage = getString(R.string.payment_success_message, transactionAmount, baseValidatePaymentRequest.beneficiaryName, baseValidatePaymentRequest.targetAccountNumber.toFormattedAccountNumber())
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, BMBConstants.PASS_PAYMENT)
                startActivity(IntentFactory.getSuccessfulResultScreenPaymentWithSubMessageAndHomeButton(baseActivity, R.string.payment_success_title, successMessage, R.string.done, true))
            }
        }
    }

    //TODO: change to fragments in future
    private fun launchFailureResultScreen(errorMessage: String) {
        baseActivity.trackCustomScreenView(BMBConstants.PAYMENT_UNSUCCESSFUL_CONST, BMBConstants.CONFIRM_CONST, BMBConstants.TRUE_CONST)
        val intent = IntentFactoryGenericResult.getFailureResultBuilder(baseActivity)
                .setGenericResultHeaderMessage(R.string.payment_error_title)
                .setGenericResultSubMessage(errorMessage)
                .setGenericResultDoneButton(baseActivity) { loadAccountsAndGoHome() }
                .build()
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}