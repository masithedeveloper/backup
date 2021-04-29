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
package com.barclays.absa.banking.express.onceOffPaymentHistoryPaymentNotification

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.onceOffPaymentHistoryPaymentNotification.dto.OnceOffPaymentHistoryPaymentNotificationRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest


class OnceOffPaymentHistoryPaymentNotificationRepository : Repository() {

    private lateinit var onceOffPaymentHistoryPaymentNotificationRequest: OnceOffPaymentHistoryPaymentNotificationRequest

    override val apiService = createXTMSService()

    override val service = "PaymentHistoryFacade"
    override val operation = "SendOnceOffPaymentHistoryPaymentNotification"

    override var mockResponseFile = "express/payment/once_off_payment_history_payment_notification.json"

    override var allowEmptyParameters: Boolean = true

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.addParameter("fromAccount", onceOffPaymentHistoryPaymentNotificationRequest.fromAccount)
                .addParameter("fromDate", onceOffPaymentHistoryPaymentNotificationRequest.fromDate)
                .addParameter("toDate", onceOffPaymentHistoryPaymentNotificationRequest.toDate)
                .addParameter("powerOfAttorney", onceOffPaymentHistoryPaymentNotificationRequest.powerOfAttorney)
                .addParameter("cifKey", onceOffPaymentHistoryPaymentNotificationRequest.cifKey)
                .addParameter("beneficiaryNotification", onceOffPaymentHistoryPaymentNotificationRequest.beneficiaryNotification)
                .addParameter("ownNotification", onceOffPaymentHistoryPaymentNotificationRequest.ownNotification)
                .addParameter("paymentHistoryLine", onceOffPaymentHistoryPaymentNotificationRequest.paymentHistoryLine)
                .build()
    }

    suspend fun fetchOnceOffPaymentHistoryPaymentNotification(request: OnceOffPaymentHistoryPaymentNotificationRequest): BaseResponse? {
        onceOffPaymentHistoryPaymentNotificationRequest = request
        return submitRequest()
    }
}