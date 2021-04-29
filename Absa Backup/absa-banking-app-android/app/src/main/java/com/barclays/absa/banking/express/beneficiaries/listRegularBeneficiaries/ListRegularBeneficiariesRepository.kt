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
package com.barclays.absa.banking.express.beneficiaries.listRegularBeneficiaries

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryStatusGroup
import com.barclays.absa.banking.express.beneficiaries.listRegularBeneficiaries.dto.ListBeneficiariesResponse
import com.barclays.absa.banking.express.shared.dto.InstructionType
import za.co.absa.networking.hmac.service.BaseRequest

class ListRegularBeneficiariesRepository : Repository() {

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "ListRegularBeneficiaries"

    override var mockResponseFile = "express/beneficiaries/list_regular_beneficiaries_response.json"

    suspend fun fetchBeneficiaries(): ListBeneficiariesResponse? {
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("instructionType", InstructionType.NORMAL)
            .addParameter("beneficiaryStatusGroup", BeneficiaryStatusGroup.ACTIVE)
            .addParameter("detailsRequired", "true")
            .build()
}