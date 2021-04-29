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
package com.barclays.absa.banking.express.documentRequest.statements

import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestDetails
import com.barclays.absa.banking.avaf.documentRequest.ui.DocumentRequestType
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.express.Repository
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.NO_PATTERN_YYYYMM
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafStatementsRepository : Repository() {
    private lateinit var documentRequestDetails: DocumentRequestDetails
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()

    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFTaxCertificate"
    override var mockResponseFile = "express/avaf/certificate_request_response_success.json"

    suspend fun submitDocumentRequest(requestDetails: DocumentRequestDetails, account: String): BaseResponse? {
        documentRequestDetails = requestDetails
        accountNumber = account

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        val statementType = mutableMapOf("detailedStatement" to "", "auditCertificate" to "", "amortizedStatement" to "")
        when (documentRequestDetails.type) {
            DocumentRequestType.TAX_CERTIFICATE -> statementType["auditCertificate"] = "X"
            DocumentRequestType.LOAN_AMORTIZATION -> statementType["amortizedStatement"] = "X"
            else -> statementType["detailedStatement"] = "X"
        }

        return baseRequest
                .addParameter("clientEmailAddress1", documentRequestDetails.email)
                .addParameter("externalReferenceNumber", accountNumber)
                .addParameter("periodEnding", DateTimeHelper.formatDate(documentRequestDetails.date, NO_PATTERN_YYYYMM))
                .addParameter("customerType", CustomerProfileObject.instance.clientTypeGroup)
                .addDictionaryParameter("statementType", statementType)
                .build()
    }
}