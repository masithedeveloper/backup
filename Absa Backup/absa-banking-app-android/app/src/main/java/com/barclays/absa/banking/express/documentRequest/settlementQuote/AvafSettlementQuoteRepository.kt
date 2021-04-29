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
 */

package com.barclays.absa.banking.express.documentRequest.settlementQuote

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafSettlementQuoteRepository : Repository() {
    private lateinit var userEmail: String
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()
    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFSettlementQuote"
    override var mockResponseFile = "express/avaf/settlement_quote_request_success.json"

    suspend fun submitDocumentRequest(email: String, account: String): BaseResponse? {
        userEmail = email
        accountNumber = account

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addDictionaryParameter("settlementDetails", getSettlementQuoteDetails())
                .build()
    }

    private fun getSettlementQuoteDetails(): Map<String, String> {
        return mapOf(
                "userEmail" to userEmail,
                "accountNumber" to accountNumber
        )
    }
}