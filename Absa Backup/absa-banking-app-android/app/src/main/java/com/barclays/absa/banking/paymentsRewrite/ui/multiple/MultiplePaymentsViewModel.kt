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
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PaymentResponse
import com.barclays.absa.banking.express.payments.validateMultiplePayment.dto.ValidateMultiplePayment
import com.barclays.absa.banking.express.payments.validateMultiplePayment.dto.ValidateMultiplePaymentRequest
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.dto.ValidationAlert
import java.math.BigDecimal
import java.util.*

class MultiplePaymentsViewModel : ViewModel() {

    val selectedBeneficiaryList by lazy { mutableListOf<RegularBeneficiary>() }
    val multiplePaymentBeneficiaryWrapperList: MutableMap<Int, MultiplePaymentBeneficiaryWrapper> = LinkedHashMap()
    val validateMultiplePaymentsRequestList: MutableList<ValidateMultiplePaymentRequest> = mutableListOf()

    var totalImmediatePaymentAmount: BigDecimal = BigDecimal.ZERO
    var totalNormalPaymentAmount: BigDecimal = BigDecimal.ZERO
    var totalFutureDatedPaymentAmount: BigDecimal = BigDecimal.ZERO

    var validationAlertList: MutableList<ValidationAlert> = mutableListOf()

    val totalPaymentAmount: BigDecimal
        get() {
            var totalAmount = BigDecimal.ZERO
            multiplePaymentBeneficiaryWrapperList.values.forEach {
                val validationAlertForBeneficiary = getValidationAlertForBeneficiary(it)
                if (validationAlertForBeneficiary == null || !validationAlertForBeneficiary.isRemoved) {
                    totalAmount += it.paymentAmount
                }
            }
            return totalAmount
        }

    companion object {
        const val MAX_ALLOWED_RETAIL_BENEFICIARIES = 5
        const val MAX_ALLOWED_BUSINESS_BENEFICIARIES = 10
    }

    fun createValidateMultiplePaymentRequest(selectedBeneficiary: RegularBeneficiary): ValidateMultiplePaymentRequest = ValidateMultiplePaymentRequest(selectedBeneficiary)

    fun getFuturePaymentsList(): List<MultiplePaymentBeneficiaryWrapper> = mutableListOf<MultiplePaymentBeneficiaryWrapper>().apply {
        addAll(multiplePaymentBeneficiaryWrapperList.values.filter { it.useTime })
    }

    fun getNormalPaymentsList(): List<MultiplePaymentBeneficiaryWrapper> = mutableListOf<MultiplePaymentBeneficiaryWrapper>().apply {
        addAll(multiplePaymentBeneficiaryWrapperList.values.filter { !it.useTime && !it.isImmediate })
    }

    fun getImmediatePaymentsList(): List<MultiplePaymentBeneficiaryWrapper> = mutableListOf<MultiplePaymentBeneficiaryWrapper>().apply {
        addAll(multiplePaymentBeneficiaryWrapperList.values.filter { it.isImmediate })
    }

    fun filterNormalPaymentsResponseList(paymentResponseList: List<PaymentResponse>): List<PaymentResponse> = mutableListOf<PaymentResponse>().apply {
        addAll(paymentResponseList.filter { paymentResponse -> getPaymentRequestForPaymentResponse(paymentResponse)?.let { (!it.useTime && !it.immediatePayment) } ?: false })
    }

    fun filterFuturePaymentsResponseList(paymentResponseList: List<PaymentResponse>): List<PaymentResponse> = mutableListOf<PaymentResponse>().apply {
        addAll(paymentResponseList.filter { paymentResponse -> getPaymentRequestForPaymentResponse(paymentResponse)?.useTime ?: false })
    }

    fun filterImmediatePaymentsResponseList(paymentResponseList: List<PaymentResponse>): List<PaymentResponse> = mutableListOf<PaymentResponse>().apply {
        addAll(paymentResponseList.filter { paymentResponse -> getPaymentRequestForPaymentResponse(paymentResponse)?.immediatePayment ?: false })
    }

    private fun getPaymentRequestForPaymentResponse(paymentResponse: PaymentResponse): ValidateMultiplePaymentRequest? = validateMultiplePaymentsRequestList.find { it.paymentSequenceNumber == paymentResponse.paymentSequenceNumber }

    fun calculateTotals() {
        totalNormalPaymentAmount = BigDecimal.ZERO
        totalImmediatePaymentAmount = BigDecimal.ZERO
        totalFutureDatedPaymentAmount = BigDecimal.ZERO
        getNormalPaymentsList().forEach { totalNormalPaymentAmount += it.paymentAmount }
        getImmediatePaymentsList().forEach { totalImmediatePaymentAmount += it.paymentAmount }
        getFuturePaymentsList().forEach { totalFutureDatedPaymentAmount += it.paymentAmount }
    }

    fun hasImmediatePayment(): Boolean = multiplePaymentBeneficiaryWrapperList.values.any { it.isImmediate }

    fun getBeneficiaryWrapper(selectedBeneficiary: RegularBeneficiary) = multiplePaymentBeneficiaryWrapperList[selectedBeneficiary.beneficiaryNumber]

    fun hasZeroPaymentAmountBeneficiary(): Boolean = multiplePaymentBeneficiaryWrapperList.values.any { it.paymentAmount == BigDecimal.ZERO }

    fun getZeroPaymentAmountBeneficiary(): MultiplePaymentBeneficiaryWrapper? = multiplePaymentBeneficiaryWrapperList.values.find { it.paymentAmount == BigDecimal.ZERO }

    fun createValidationAlertList(validateMultiplePaymentResponseList: List<ValidateMultiplePayment>?): MutableList<ValidationAlert> {
        validationAlertList = mutableListOf()
        validateMultiplePaymentResponseList?.forEach { validateMultiplePayment ->
            validateMultiplePaymentsRequestList.find { validateMultiplePayment.warningMessageList.isNotEmpty() && it.paymentSequenceNumber == validateMultiplePayment.paymentSequenceNumber }?.let {
                validationAlertList.add(ValidationAlert().apply {
                    beneficiaryNumber = it.beneficiaryNumber
                    paymentSequenceNumber = it.paymentSequenceNumber
                    beneficiaryName = it.beneficiaryName
                    amount = it.paymentAmount
                })
            }
        }
        return validationAlertList
    }

    fun getValidationAlertForBeneficiary(beneficiary: MultiplePaymentBeneficiaryWrapper): ValidationAlert? = validationAlertList.find { it.beneficiaryNumber == beneficiary.regularBeneficiary.beneficiaryDetails.beneficiaryNumber }

    fun getValidationAlertForSequenceNumber(sequenceNumber: Int): ValidationAlert? = validationAlertList.find { it.paymentSequenceNumber == sequenceNumber }

    fun isValidPaymentList(paymentList: List<MultiplePaymentBeneficiaryWrapper>) = paymentList.any {
        val validationAlertForBeneficiary = getValidationAlertForBeneficiary(it)
        validationAlertForBeneficiary == null || validationAlertForBeneficiary.isResolved
    }

    fun removeBeneficiaryFromList(regularBeneficiary: RegularBeneficiary?) {
        selectedBeneficiaryList.remove(regularBeneficiary)
        multiplePaymentBeneficiaryWrapperList.remove(regularBeneficiary?.beneficiaryDetails?.beneficiaryNumber)
        validateMultiplePaymentsRequestList.removeIf { it.beneficiaryNumber == regularBeneficiary?.beneficiaryDetails?.beneficiaryNumber }
        calculateTotals()
    }

    fun validationAlertRemoved(validationAlert: ValidationAlert?) {
        validationAlert?.apply {
            isRemoved = true
            isResolved = false
        }
    }

    fun validationAlertResolved(validationAlert: ValidationAlert?) {
        validationAlert?.apply {
            isResolved = true
            isRemoved = false
        }
    }
}