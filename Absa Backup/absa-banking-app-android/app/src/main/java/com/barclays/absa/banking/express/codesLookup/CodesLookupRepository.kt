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

package com.barclays.absa.banking.express.codesLookup

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.codesLookup.dto.CodesLookupResponse
import com.barclays.absa.banking.shared.BaseModel
import za.co.absa.networking.hmac.service.BaseRequest

class CodesLookupRepository : Repository() {

    private lateinit var codesLookupType: CodesLookupTypes

    override var apiService = createXTMSService()
    override val service = "CodesLookupFacade"
    override val operation = "GetCodes"
    override var mockResponseFile: String = "express/codes_lookup_response.json"

    suspend fun fetchCodes(codesLookupType: CodesLookupTypes): CodesLookupResponse? {
        this.codesLookupType = codesLookupType
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        val sortValueList = SortValueList().apply { sortValueList.add(SortValue()) }
        return baseRequest
                .addParameter("codesLookupType", codesLookupType.name)
                .addObjectParameter("sortValues", sortValueList)
                .build()
    }
}

private class SortValueList : BaseModel {
    val sortValueList = ArrayList<SortValue>()
}

private class SortValue : BaseModel {
    val sortDirection = "ASC"
    val sortFieldName = "itemCode"
}

enum class CodesLookupTypes {
    ALL,
    SOURCE_OF_FUNDS,
    OCCUPATION_CODE,
    EMPLOYMENT_STATUS,
    MONTHLY_INCOME_RANGE,
    TITLE,
    NATIONALITY,
    COUNTRY_OF_BIRTH,
    MEDICAL_OCCUPATION,
    RESIDENTIAL_STATUS,
    OCCUPATION_LEVEL,
    EMPLOYMENT_SECTOR,
    CORRESPONDENCE_LANGAUGE,
    MARITAL_STATUS,
    MARRIAGE_CONTRACT_TYPE,
    GENDER,
    PREFERRED_COMMUNICATION_CHANNEL,
    HOME_LANGUAGE_CODE,
    DESIGNATION,
    POST_MATRIC_QUALIFICATION,
    SIC_CODE
}
