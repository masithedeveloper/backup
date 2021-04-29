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
package com.barclays.absa.banking.express.onceOffPaymentHistoryDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.onceOffPaymentHistoryDetails.dto.OnceOffPaymentHistoryDetailsRequest
import com.barclays.absa.banking.express.onceOffPaymentHistoryDetails.dto.OnceOffPaymentHistoryDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class OnceOffPaymentHistoryRepository : Repository() {
    private lateinit var onceOffPaymentHistoryDetailsRequest: OnceOffPaymentHistoryDetailsRequest
    override val apiService = createXTMSService()
    override val service = "PaymentHistoryFacade"
    override val operation = "GetOnceOffPaymentHistoryDetail"
    override var mockResponseFile = "express/payments/once_off_payment_history_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.enablePagination()
                .addParameter("payType", onceOffPaymentHistoryDetailsRequest.payType)
                .addParameter("fromAccountNo", onceOffPaymentHistoryDetailsRequest.fromAccountNo)
                .addParameter("fromDate", onceOffPaymentHistoryDetailsRequest.fromDate)
                .addParameter("toDate", onceOffPaymentHistoryDetailsRequest.toDate)
                .addParameter("sortColumn", onceOffPaymentHistoryDetailsRequest.sortColumn)
                .addParameter("sortDirection", onceOffPaymentHistoryDetailsRequest.sortDirection)
                .build()
    }

    suspend fun fetchOnceOffPaymentDetails(request: OnceOffPaymentHistoryDetailsRequest): OnceOffPaymentHistoryDetailsResponse? {
        onceOffPaymentHistoryDetailsRequest = request
        return submitRequest()
    }
}