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

package com.barclays.absa.banking.express.shared.getSavedApplications

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplicationRequest
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplicationResponse
import za.co.absa.networking.hmac.service.BaseRequest

class SavedApplicationRepository : Repository() {
    private lateinit var savedApplicationRequest: SavedApplicationRequest

    override val apiService = createXSMSService()
    override val service = "SavedApplicationsFacade"
    override val operation = "GetSavedApplicationsDetails"
    override var mockResponseFile: String = "express/invest/get_saved_applications.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addParameter("idNumber", savedApplicationRequest.idNumber)
            .addParameter("applicationType", savedApplicationRequest.applicationType)
            .build()

    suspend fun fetchSavedApplications(savedApplicationRequest: SavedApplicationRequest): SavedApplicationResponse? {
        this.savedApplicationRequest = savedApplicationRequest
        return submitRequest()
    }
}