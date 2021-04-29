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

package com.barclays.absa.banking.express.invest.saveEmploymentDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.EmploymentDetailsRequest
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.EmploymentDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class EmploymentDetailsRepository: Repository() {
    private lateinit var employmentDetailsRequest:EmploymentDetailsRequest

    override val apiService = createXSMSService()
    override val service = "SaveAndInvestCaseManagementFacade"
    override val operation = "SaveEmploymentDetailsInfo"
    override var mockResponseFile: String = "express/invest/save_employment_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = with(employmentDetailsRequest) {
        baseRequest.addObjectParameter("employmentDetails", employmentDetails)
            .addObjectParameter("dataSharingandMarketingConcent", dataSharingAndMarketingConsent)
            .addParameter("rBAServiceStatus", rbaServiceStatus)
            .addParameter("riskRating", riskRating)
            .build()
    }

    suspend fun saveEmploymentDetails(employmentDetailsRequest: EmploymentDetailsRequest): EmploymentDetailsResponse? {
        this.employmentDetailsRequest = employmentDetailsRequest
        return submitRequest()
    }
}