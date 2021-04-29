package com.barclays.absa.banking.express.documentRequest.paidupLetter

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafPaidUpLetterRepository : Repository() {
    private lateinit var userEmail: String
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()
    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFPaidUpLetter"
    override var mockResponseFile = "express/avaf/paidup_letter_request_success.json"

    suspend fun submitDocumentRequest(email: String, account: String): BaseResponse? {
        userEmail = email
        accountNumber = account

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addDictionaryParameter("paidUpLetter", getDocumentRequestDetails())
                .build()
    }

    private fun getDocumentRequestDetails(): Map<String, String> {
        return mapOf(
                "userEmail" to userEmail,
                "accountNumber" to accountNumber
        )
    }
}