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
package com.barclays.absa.banking.express.identificationAndVerification

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.identificationAndVerification.dto.BioResultResponse
import za.co.absa.networking.hmac.service.BaseRequest

class FaceBioResultRepository : Repository() {

    private lateinit var reference: String
    private lateinit var requestId: String

    override val apiService = createXSWPService()

    override val service = "FaceBioFacade"
    override val operation = "BioResultPolling"

    override var apiPollingMaxRetries = 10

    override var apiRetryDelayMillis = 5000L

    override var showProgressDialog = false

    override var mockResponseFile = "express/identificationAndVerification/get_biometric_status.json"

    suspend fun fetchFaceBioResult(reference: String, requestId: String): BioResultResponse? {
        this.reference = reference
        this.requestId = requestId
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("reference", reference)
            .addParameter("requestId", requestId)
            .build()
}