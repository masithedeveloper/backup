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
package com.barclays.absa.banking.express.payments.validatePayment

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentRequest
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentResponse
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM
import za.co.absa.networking.hmac.service.BaseRequest

class ValidatePaymentRepository : Repository() {

    private lateinit var validatePaymentRequest: ValidatePaymentRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryPaymentFacade"
    override val operation = "ValidatePayment"

    override var mockResponseFile = "express/payments/payments_validate_payment_response.json"

    suspend fun validatePayment(validatePaymentRequest: ValidatePaymentRequest): ValidatePaymentResponse? {
        this.validatePaymentRequest = validatePaymentRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("beneficiaryNumber", validatePaymentRequest.beneficiaryNumber)
            .addParameter("beneficiaryName", validatePaymentRequest.beneficiaryName)
            .addParameter("targetAccountNumber", validatePaymentRequest.targetAccountNumber)
            .addParameter("beneficiaryAccountType", validatePaymentRequest.beneficiaryAccountType)
            .addParameter("clearingCodeOrInstitutionCode", validatePaymentRequest.clearingCodeOrInstitutionCode)
            .addParameter("bankName", validatePaymentRequest.bankName)
            .addParameter("sourceAccountReference", validatePaymentRequest.sourceAccountReference)
            .addParameter("targetAccountReference", validatePaymentRequest.targetAccountReference)
            .addParameter("cifKey", validatePaymentRequest.cifKey)
            .addParameter("tieBreaker", validatePaymentRequest.tieBreaker)
            .addParameter("uniqueEFTNumber", validatePaymentRequest.uniqueEFTNumber)
            .addParameter("beneficiaryStatus", validatePaymentRequest.beneficiaryStatus)
            .addParameter("beneficiaryNotification", validatePaymentRequest.beneficiaryNotification)
            .addParameter("updateBeneficiaryNotificationDetails", validatePaymentRequest.updateBeneficiaryNotificationDetails)
            .addParameter("typeOfBeneficiary", validatePaymentRequest.typeOfBeneficiary.value)
            .addParameter("verifiedPaymentIndicator", validatePaymentRequest.verifiedPaymentIndicator)
            .addParameter("instructionType", validatePaymentRequest.instructionType)
            .addParameter("ownNotificationVO", validatePaymentRequest.ownNotification)
            .addParameter("paymentAmount", validatePaymentRequest.paymentAmount)
            .addParameter("notes", validatePaymentRequest.notes)
            .addParameter("paymentSourceAccountNumber", validatePaymentRequest.paymentSourceAccountNumber)
            .addParameter("useTime", validatePaymentRequest.useTime)
            .addParameter("immediatePayment", validatePaymentRequest.immediatePayment)
            .addParameter("paymentTransactionDateAndTime", DateTimeHelper.formatDate(validatePaymentRequest.paymentTransactionDateAndTime, DASHED_PATTERN_YYYY_MM_DD_HH_MM))
            .addParameter("retryIIP", validatePaymentRequest.retryIIP)
            .addParameter("realTimeProcessedPayment", validatePaymentRequest.realTimeProcessedPayment)
            .addParameter("realTimeProcessedPaymentReferenceNumber", validatePaymentRequest.realTimeProcessedPaymentReferenceNumber)
            .addParameter("paymentNumber", validatePaymentRequest.paymentNumber)
            .build()
}