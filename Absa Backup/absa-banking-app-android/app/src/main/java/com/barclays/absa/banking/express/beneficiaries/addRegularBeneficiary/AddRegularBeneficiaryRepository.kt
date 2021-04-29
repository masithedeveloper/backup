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
package com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary

import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto.AddBeneficiaryRequest
import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto.AddBeneficiaryResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest

class AddRegularBeneficiaryRepository : SureCheckRepository() {

    private lateinit var addBeneficiaryRequest: AddBeneficiaryRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryManagementFacade"
    override val operation = "AddRegularBeneficiary"

    override var securityFunctionType: String = "addBeneficiary"

    override var mockResponseFile = "express/beneficiaries/add_regular_beneficiary_response.json"

    suspend fun addBeneficiary(addBeneficiaryRequest: AddBeneficiaryRequest): AddBeneficiaryResponse? {
        this.addBeneficiaryRequest = addBeneficiaryRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("typeOfBeneficiary", addBeneficiaryRequest.typeOfBeneficiary.value)
            .addParameter("instructionType", addBeneficiaryRequest.instructionType.value)
            .addParameter("sourceAccountReference", addBeneficiaryRequest.sourceAccountReference)
            .addParameter("targetAccountReference", addBeneficiaryRequest.targetAccountReference)
            .addParameter("targetAccountNumber", addBeneficiaryRequest.targetAccountNumber)
            .addParameter("beneficiaryName", addBeneficiaryRequest.beneficiaryName)
            .addParameter("bankNameOrInstitutionName", addBeneficiaryRequest.bankNameOrInstitutionName)
            .addParameter("clearingCodeOrInstitutionCode", addBeneficiaryRequest.clearingCodeOrInstitutionCode)
            .addParameter("targetAccountType", addBeneficiaryRequest.targetAccountType)
            .addParameter("beneficiaryNotification", addBeneficiaryRequest.beneficiaryNotification)
            .addParameter("ownNotification", addBeneficiaryRequest.ownNotification)
            .addParameter("switchDetailsForAddBeneficiary", addBeneficiaryRequest.switchDetailsForAddBeneficiary ?: "")
            .build()
}