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
package com.barclays.absa.banking.express.payments.validateOnceOffPayment

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.payments.validateOnceOffPayment.dto.ValidateOnceOffPaymentRequest
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentResponse
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM
import za.co.absa.networking.hmac.service.BaseRequest

class ValidateOnceOffPaymentRepository : Repository() {

    private lateinit var validateOnceOffPaymentRequest: ValidateOnceOffPaymentRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryPaymentFacade"
    override val operation = "ValidateOnceOffPayment"

    override var mockResponseFile = "express/payments/payments_validate_payment_response.json"

    suspend fun validatePayment(validateOnceOffPaymentRequest: ValidateOnceOffPaymentRequest): ValidatePaymentResponse? {
        this.validateOnceOffPaymentRequest = validateOnceOffPaymentRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("beneficiaryName", validateOnceOffPaymentRequest.beneficiaryName)
            .addParameter("targetAccountNumber", validateOnceOffPaymentRequest.targetAccountNumber)
            .addParameter("beneficiaryAccountType", validateOnceOffPaymentRequest.beneficiaryAccountType)
            .addParameter("clearingCodeOrInstitutionCode", validateOnceOffPaymentRequest.clearingCodeOrInstitutionCode)
            .addParameter("bankName", validateOnceOffPaymentRequest.bankName)
            .addParameter("sourceAccountReference", validateOnceOffPaymentRequest.sourceAccountReference)
            .addParameter("targetAccountReference", validateOnceOffPaymentRequest.targetAccountReference)
            .addParameter("beneficiaryNotification", validateOnceOffPaymentRequest.beneficiaryNotification)
            .addParameter("typeOfBeneficiary", validateOnceOffPaymentRequest.typeOfBeneficiary.value)
            .addParameter("verifiedPaymentIndicator", validateOnceOffPaymentRequest.verifiedPaymentIndicator)
            .addParameter("instructionType", validateOnceOffPaymentRequest.instructionType)
            .addParameter("ownNotificationVO", validateOnceOffPaymentRequest.ownNotification)
            .addParameter("paymentAmount", validateOnceOffPaymentRequest.paymentAmount)
            .addParameter("notes", validateOnceOffPaymentRequest.notes)
            .addParameter("paymentSourceAccountNumber", validateOnceOffPaymentRequest.paymentSourceAccountNumber)
            .addParameter("useTime", validateOnceOffPaymentRequest.useTime)
            .addParameter("immediatePayment", validateOnceOffPaymentRequest.immediatePayment)
            .addParameter("paymentTransactionDateAndTime", DateTimeHelper.formatDate(validateOnceOffPaymentRequest.paymentTransactionDateAndTime, DASHED_PATTERN_YYYY_MM_DD_HH_MM))
            .addParameter("retryIIP", validateOnceOffPaymentRequest.retryIIP)
            .addParameter("realTimeProcessedPayment", validateOnceOffPaymentRequest.realTimeProcessedPayment)
            .addParameter("realTimeProcessedPaymentReferenceNumber", validateOnceOffPaymentRequest.realTimeProcessedPaymentReferenceNumber)
            .addParameter("paymentNumber", validateOnceOffPaymentRequest.paymentNumber)
            .build()
}