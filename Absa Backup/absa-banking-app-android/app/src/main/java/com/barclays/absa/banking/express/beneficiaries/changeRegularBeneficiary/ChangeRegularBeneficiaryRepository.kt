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
package com.barclays.absa.banking.express.beneficiaries.changeRegularBeneficiary

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.beneficiaries.changeRegularBeneficiary.dto.ChangeBeneficiaryRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class ChangeRegularBeneficiaryRepository : Repository() {

    private lateinit var changeBeneficiaryRequest: ChangeBeneficiaryRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "ChangeRegularBeneficiary"

    override var mockResponseFile = "express/base_response.json"

    suspend fun changeBeneficiary(changeBeneficiaryRequest: ChangeBeneficiaryRequest): BaseResponse? {
        this.changeBeneficiaryRequest = changeBeneficiaryRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("instructionType", changeBeneficiaryRequest.instructionType.value)
            .addParameter("typeOfBeneficiary", changeBeneficiaryRequest.typeOfBeneficiary.value)
            .addParameter("beneficiaryNumber", changeBeneficiaryRequest.beneficiaryNumber)
            .addParameter("uniqueEFTNumber", changeBeneficiaryRequest.uniqueEFTNumber)
            .addParameter("tieBreaker", changeBeneficiaryRequest.tieBreaker)
            .addParameter("cifKey", changeBeneficiaryRequest.cifKey)
            .addParameter("sourceAccountReference", changeBeneficiaryRequest.sourceAccountReference)
            .addParameter("targetAccountReference", changeBeneficiaryRequest.targetAccountReference)
            .addParameter("beneficiaryName", changeBeneficiaryRequest.beneficiaryName)
            .addParameter("beneficiaryStatus", changeBeneficiaryRequest.beneficiaryStatus)
            .addParameter("ownNotification", changeBeneficiaryRequest.ownNotification.value)
            .addParameter("beneficiaryNotification", changeBeneficiaryRequest.beneficiaryNotification)
            .build()
}