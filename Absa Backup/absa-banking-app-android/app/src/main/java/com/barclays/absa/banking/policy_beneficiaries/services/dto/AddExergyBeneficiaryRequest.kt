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

package com.barclays.absa.banking.policy_beneficiaries.services.dto

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService

class AddExergyBeneficiaryRequest<T>(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val idType = policyBeneficiaryInfo.idType?.second ?: ""

        params = RequestParams.Builder(ManageBeneficiaryService.OP2174_ADD_EXERGY_BENEFICIARY)
                .put(ManageBeneficiaryService.TITLE, policyBeneficiaryInfo.title.second)
                .put(ManageBeneficiaryService.FIRST_NAME, policyBeneficiaryInfo.firstName)
                .put(ManageBeneficiaryService.SURNAME, policyBeneficiaryInfo.surname)
                .put(ManageBeneficiaryService.INITIALS, policyBeneficiaryInfo.firstName.first().toString())
                .put(ManageBeneficiaryService.ID_TYPE, idType)
                .put(ManageBeneficiaryService.ID_NUMBER, policyBeneficiaryInfo.idNumber)
                .put(ManageBeneficiaryService.DATE_OF_BIRTH, policyBeneficiaryInfo.dateOfBirth)
                .put(ManageBeneficiaryService.RELATIONSHIP, policyBeneficiaryInfo.relationship?.second)
                .put(ManageBeneficiaryService.ALLOCATION, policyBeneficiaryInfo.allocation)
                .put(ManageBeneficiaryService.PREFERRED_COMMUNICATION, policyInfo.preferredCommunication)
                .put(ManageBeneficiaryService.ADDRESSLINE1, policyBeneficiaryInfo.addressLine1.toUpperCase())
                .put(ManageBeneficiaryService.ADDRESSLINE2, policyBeneficiaryInfo.addressLine2.toUpperCase())
                .put(ManageBeneficiaryService.SUBURB, policyBeneficiaryInfo.suburb.toUpperCase())
                .put(ManageBeneficiaryService.TOWN, policyBeneficiaryInfo.town.toUpperCase())
                .put(ManageBeneficiaryService.POSTAL_CODE, policyBeneficiaryInfo.postalCode)
                .put(ManageBeneficiaryService.POLICY_NUMBER, policyInfo.policyNumber)
                .put(ManageBeneficiaryService.POLICY_TYPE, BMBConstants.EXERGY_POLICY_TYPE)
                .put(ManageBeneficiaryService.SOURCE_OF_FUND, "${policyInfo.sourceOfFunds?.itemCode.toString()}-${policyInfo.sourceOfFunds?.defaultLabel.toString()}")
                .put(ManageBeneficiaryService.CELLPHONE_NUMBER, policyBeneficiaryInfo.cellphoneNumber)
                .put(ManageBeneficiaryService.EMAIL_ADDRESS, policyBeneficiaryInfo.emailAddress)
                .build()

        mockResponseFile = "insurance/op2174_add_exergy_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = SureCheckResponse::class.java as Class<T>

    override fun isEncrypted() = true
}