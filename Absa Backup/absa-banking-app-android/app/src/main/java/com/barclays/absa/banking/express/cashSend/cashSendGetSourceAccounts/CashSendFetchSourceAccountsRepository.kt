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

package com.barclays.absa.banking.express.cashSend.cashSendGetSourceAccounts

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.dto.SourceAccountsResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendFetchSourceAccountsRepository : Repository() {
    private lateinit var searchString: String

    override val apiService: ApiService = createXTMSService()

    override val service: String = "CashSendPaymentFacade"
    override val operation: String = "GetSourceAccounts"

    override var mockResponseFile: String = "express/cashSend/cash_send_get_source_accounts.json"

    suspend fun fetchSourceAccounts(searchString: String): SourceAccountsResponse? {
        this.searchString = searchString
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("searchString", searchString)
            .build()
}