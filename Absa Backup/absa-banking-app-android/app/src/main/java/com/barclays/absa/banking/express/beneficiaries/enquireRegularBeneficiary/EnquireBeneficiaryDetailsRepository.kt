/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.express.beneficiaries.enquireRegularBeneficiary

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.beneficiaries.enquireRegularBeneficiary.dto.BeneficiaryDetailsResponse
import com.barclays.absa.banking.express.beneficiaries.enquireRegularBeneficiary.dto.BeneficiaryEnquiryRequest
import za.co.absa.networking.hmac.service.BaseRequest

class EnquireBeneficiaryDetailsRepository : Repository() {

    private lateinit var beneficiaryEnquiryRequest: BeneficiaryEnquiryRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "EnquireRegularBeneficiaryDetails"

    override var mockResponseFile = "express/beneficiaries/enquire_regular_beneficiary_details_response.json"

    suspend fun fetchBeneficiaryDetails(beneficiaryEnquiryRequest: BeneficiaryEnquiryRequest): BeneficiaryDetailsResponse? {
        this.beneficiaryEnquiryRequest = beneficiaryEnquiryRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("beneficiaryEnquiry", beneficiaryEnquiryRequest)
            .build()
}