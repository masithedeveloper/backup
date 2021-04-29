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
package com.barclays.absa.banking.express.beneficiaries.deleteRegularBeneficiary

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.beneficiaries.deleteRegularBeneficiary.dto.DeleteBeneficiaryRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class DeleteRegularBeneficiaryRepository : Repository() {

    private lateinit var deleteBeneficiaryRequest: DeleteBeneficiaryRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "DeleteRegularBeneficiary"

    override var mockResponseFile = "express/base_response.json"

    suspend fun deleteBeneficiary(deleteBeneficiaryRequest: DeleteBeneficiaryRequest): BaseResponse? {
        this.deleteBeneficiaryRequest = deleteBeneficiaryRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("beneficiaryEnquiryVO", deleteBeneficiaryRequest)
            .build()
}