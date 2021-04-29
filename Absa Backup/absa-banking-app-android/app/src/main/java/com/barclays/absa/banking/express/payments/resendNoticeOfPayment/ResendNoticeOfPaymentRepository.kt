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
package com.barclays.absa.banking.express.payments.resendNoticeOfPayment

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.payments.resendNoticeOfPayment.dto.ResendNoticeOfPaymentRequest
import com.barclays.absa.utils.DateTimeHelper
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class ResendNoticeOfPaymentRepository : Repository() {

    private lateinit var resendNoticeOfPaymentRequest: ResendNoticeOfPaymentRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryPaymentFacade"
    override val operation = "CreateAndSendPaymentNotification"

    override var mockResponseFile = "express/payments/payments_resend_notice_of_payment_response.json"

    suspend fun resendNoticeOfPayment(resendNoticeOfPaymentRequest: ResendNoticeOfPaymentRequest): BaseResponse? {
        this.resendNoticeOfPaymentRequest = resendNoticeOfPaymentRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("beneficiaryName", resendNoticeOfPaymentRequest.beneficiaryName)
            .addParameter("targetAccountNumber", resendNoticeOfPaymentRequest.targetAccountNumber)
            .addParameter("clearingCodeOrInstitutionCode", resendNoticeOfPaymentRequest.clearingCodeOrInstitutionCode)
            .addParameter("bankName", resendNoticeOfPaymentRequest.bankName)
            .addParameter("targetAccountReference", resendNoticeOfPaymentRequest.targetAccountReference)
            .addParameter("realTimeProcessedPayment", resendNoticeOfPaymentRequest.realTimeProcessedPayment)
            .addParameter("paymentTransactionDateAndTime", DateTimeHelper.formatDate(resendNoticeOfPaymentRequest.paymentTransactionDateAndTime, "yyyy-MM-dd HH:mm"))
            .addParameter("paymentAmount", resendNoticeOfPaymentRequest.paymentAmount)
            .addParameter("processedPaymentNumber", resendNoticeOfPaymentRequest.processedPaymentNumber)
            .addParameter("beneficiaryNotification", resendNoticeOfPaymentRequest.beneficiaryNotification)
            .addParameter("ownNotificationVO", resendNoticeOfPaymentRequest.ownNotification)
            .addParameter("uniqueEFTNumber", resendNoticeOfPaymentRequest.uniqueEFTNumber)
            .addParameter("beneficiaryNumber", resendNoticeOfPaymentRequest.beneficiaryNumber)
            .addParameter("tieBreaker", resendNoticeOfPaymentRequest.tieBreaker)
            .addParameter("cifKey", resendNoticeOfPaymentRequest.cifKey)
            .addParameter("beneficiaryStatus", resendNoticeOfPaymentRequest.beneficiaryStatus)
            .addParameter("instructionType", resendNoticeOfPaymentRequest.instructionType)
            .build()
}