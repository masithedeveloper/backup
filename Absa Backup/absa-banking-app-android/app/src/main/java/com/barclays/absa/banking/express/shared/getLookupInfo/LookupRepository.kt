/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.express.shared.getLookupInfo

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupRequest
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupResponse
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class LookupRepository : Repository() {
    private lateinit var lookupRequest: LookupRequest

    override val apiService: ApiService = createXSMSService()
    override val service = "LookupFacade"
    override val operation = "GetLookUpDataDetails"
    override var mockResponseFile: String = "express/shared/get_lookup_data_details_title.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
            baseRequest.addParameter("groupCode", lookupRequest.groupCode)
                    .addParameter("language", lookupRequest.language)
                    .build()

    suspend fun fetchLookupItems(lookupRequest: LookupRequest): LookupResponse? {
        mockResponseFile = when (lookupRequest.groupCode) {
            CIFGroupCode.OCCUPATION_STATUS.key -> "express/shared/get_lookup_data_details_occupation_status.json"
            CIFGroupCode.OCCUPATION.key -> "express/shared/get_lookup_data_details_occupation.json"
            CIFGroupCode.SOURCE_OF_FUNDS_I.key -> "express/shared/get_lookup_data_details_source_of_funds.json"
            CIFGroupCode.SOURCE_OF_INCOME_I.key -> "express/shared/get_lookup_data_details_source_of_income.json"
            else -> "express/shared/get_lookup_data_details_title.json"
        }

        this.lookupRequest = lookupRequest
        return submitRequest()
    }
}