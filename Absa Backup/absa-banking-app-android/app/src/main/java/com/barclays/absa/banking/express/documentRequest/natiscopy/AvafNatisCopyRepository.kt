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

package com.barclays.absa.banking.express.documentRequest.natiscopy

import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestDetails
import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafNatisCopyRepository : Repository() {
    private lateinit var documentRequestDetails: DocumentRequestDetails
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()
    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFENatis"
    override var mockResponseFile = "express/avaf/natis_copy_request_response_success.json"

    suspend fun submitDocumentRequest(requestDetails: DocumentRequestDetails, account: String): BaseResponse? {
        documentRequestDetails = requestDetails
        accountNumber = account

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addDictionaryParameter("eNatisDetails", getDocumentRequestDetails())
                .build()
    }

    private fun getDocumentRequestDetails(): Map<String, String> {
        return mapOf(
                "userEmail" to documentRequestDetails.email,
                "accountNumber" to accountNumber
        )
    }
}