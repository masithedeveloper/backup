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

package com.barclays.absa.banking.express.avafDocumentPrevalidation

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.avafDocumentPrevalidation.dto.DocumentPrevalidationRulesResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AvafDocumentPrevalidationRepository : Repository() {
    private lateinit var accountNumber: String

    override val apiService = createXTMSService()

    override val service = "AVAFDocumentsFacade"
    override val operation = "GetAVAFPreValidateDocument"

    override var mockResponseFile = "express/avaf/avaf_document_prevalidation_success.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.addParameter("accountNumber", accountNumber).build()

    suspend fun fetchValidationRules(accountNumber: String): DocumentPrevalidationRulesResponse? {
        this.accountNumber = accountNumber
        return submitRequest()
    }
}