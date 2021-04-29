/*
 * Copyright (c) 2020. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.express.shared.getDocumentDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.getDocumentDetails.dto.DocumentDetailsRequest
import com.barclays.absa.banking.express.shared.getDocumentDetails.dto.DocumentDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class DocumentDetailsRepository : Repository() {
    private lateinit var documentDetailsRequest: DocumentDetailsRequest

    override val apiService = createXSMSService()
    override val service = "DocumentFacade"
    override val operation = "GetDocumentDetails"
    override var mockResponseFile: String = "express/shared/get_document_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addObjectParameter("documentInputVO", documentDetailsRequest).build()

    suspend fun fetchDocumentDetails(documentDetailsRequest: DocumentDetailsRequest): DocumentDetailsResponse? {
        this.documentDetailsRequest = documentDetailsRequest
        return submitRequest()
    }
}