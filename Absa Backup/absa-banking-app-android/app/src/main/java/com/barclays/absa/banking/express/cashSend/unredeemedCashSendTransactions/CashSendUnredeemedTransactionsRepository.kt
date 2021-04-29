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

package com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto.CashSendUnredeemedTransactionsResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendUnredeemedTransactionsRepository : Repository() {
    lateinit var sourceAccount: String

    override val apiService: ApiService = createXTMSService()

    override val service: String = "CashSendPaymentFacade"
    override val operation: String = "GetUnredeemedCashSendTransactions"

    override var mockResponseFile: String = "express/cashSend/unredeemed_cash_send_transactions_response.json"

    suspend fun fetchUnredeemedTransactions(sourceAccount: String): CashSendUnredeemedTransactionsResponse? {
        this.sourceAccount = sourceAccount
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest
            .enablePagination()
            .addParameter("sourceAccount", sourceAccount)
            .build()
}