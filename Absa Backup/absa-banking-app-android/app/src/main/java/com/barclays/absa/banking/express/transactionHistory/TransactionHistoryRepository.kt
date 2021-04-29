/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.transactionHistory

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest
import com.barclays.absa.banking.express.transactionHistory.dto.TransactionHistoryResponse
import za.co.absa.networking.hmac.service.BaseRequest

class TransactionHistoryRepository : Repository() {

    private lateinit var historyRequest: HistoryRequest

    override val apiService = createXTMSService()

    override var headers = mapOf(Pair("X-Absa-C2C", "TXH"))

    override var service: String = "TransactionHistoryFacade"
    override var operation: String = "GetTransactionHistory"

    override var mockResponseFile: String = "express/transaction_history.json"

    suspend fun fetchTransactionHistory(historyRequest: HistoryRequest): TransactionHistoryResponse? {
        this.historyRequest = historyRequest
        return submitRequest()
    }

    suspend fun fetchAvafTransactionHistory(historyRequest: HistoryRequest): TransactionHistoryResponse? {
        this.historyRequest = historyRequest

        service = "AVAFManagementFacade"
        operation = "GetAVAFTransactionHistory"
        apiPollingMaxRetries = 20
        apiRetryDelayMillis = 1000L

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.enablePagination()
                .addParameter("fromAccountNr", historyRequest.fromAccountNumber)
                .addParameter("fromDate", historyRequest.fromDate)
                .addParameter("toDate", historyRequest.toDate)
                .addParameter("sortColumn", "transactionDate")
                .addParameter("unclearedTransactions", historyRequest.includeUnclearedTransactions.toString())
                .addParameter("allTransactions", historyRequest.includeAllTransactions.toString())
                .addParameter("sortDirection", "DESC")
                .build()
    }
}