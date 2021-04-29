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
package com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard

import com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard.dto.UpdateSecondaryCardMandateRequest
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class UpdateSecondaryCardRepository : SureCheckRepository() {
    lateinit var updateSecondaryCardMandateRequest: UpdateSecondaryCardMandateRequest
    override val apiService = createXTMSService()

    override val service = "SecondaryCardManagementFacade"
    override val operation = "UpdateSecondaryCardMandate"

    override var securityFunctionType = "updateSecondaryCardMandate_TVM"
    override var serviceOperation = "SecondaryCardManagementFacadeUpdateSecondaryCardMandate"

    override var mockResponseFile = "express/secondaryCard/update_secondary_card_mandates.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("action", "UPD")
            .addParameter("primaryPlastic", updateSecondaryCardMandateRequest.primaryPlastic)
            .addParameter("secondaryPlastic", updateSecondaryCardMandateRequest.secondaryPlastic)
            .addParameter("secondaryTenantMandate", updateSecondaryCardMandateRequest.secondaryTenantMandate)
            .addObjectParameter("secondaryCardUpdateMandateDetailsList", updateSecondaryCardMandateRequest.secondaryCardUpdateMandateDetailsList)
            .build()

    suspend fun updateSecondaryCardMandates(request: UpdateSecondaryCardMandateRequest): BaseResponse? {
        updateSecondaryCardMandateRequest = request
        additionalSureCheckParameters = request.additionalSureCheckParameters
        return submitRequest()
    }
}