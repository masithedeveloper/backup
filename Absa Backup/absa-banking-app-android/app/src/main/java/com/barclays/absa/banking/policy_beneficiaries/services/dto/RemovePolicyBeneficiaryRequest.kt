/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService.Companion.LIFE_CLIENT_CODE
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService.Companion.ROLE_NUMBER
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType

class RemovePolicyBeneficiaryRequest<T>(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType,
                                        extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val requestBuilder = RequestParams.Builder()
        requestBuilder.put(OpCodeParams.OPCODE_KEY, OP0914_REMOVE_BENEFICIARY)
        requestBuilder.put(ROLE_NUMBER, policyBeneficiaryInfo.roleNumber)
        requestBuilder.put(LIFE_CLIENT_CODE, policyBeneficiaryInfo.lifeClientCode)
        requestBuilder.put(ManageBeneficiaryService.TITLE, policyBeneficiaryInfo.title.second)
        requestBuilder.put(ManageBeneficiaryService.RELATIONSHIP, policyBeneficiaryInfo.relationship?.second)
        requestBuilder.put(ManageBeneficiaryService.ALLOCATION, policyBeneficiaryInfo.allocation)
        requestBuilder.put(ManageBeneficiaryService.POLICY_NUMBER, policyInfo.policyNumber)

        policyInfo.sourceOfFunds?.let {
            requestBuilder.put(ManageBeneficiaryService.SOURCE_OF_FUND, "${it.itemCode}-${it.defaultLabel}")
        }

        if (policyBeneficiaryInfo.beneficiaryLifeClientCode.isNotEmpty()) {
            requestBuilder.put(ManageBeneficiaryService.BENEFICIARY_LIFE_CLIENT_CODE, policyBeneficiaryInfo.beneficiaryLifeClientCode)
        }

        if (policyBeneficiaryInfo.beneficiaryAllocation.isNotEmpty()) {
            requestBuilder.put(ManageBeneficiaryService.BENEFICIARY_ALLOCATION, policyBeneficiaryInfo.beneficiaryAllocation)
        }

        if (policyBeneficiaryInfo.beneficiaryName.isNotEmpty()) {
            requestBuilder.put(ManageBeneficiaryService.BENEFICIARY_NAME, policyBeneficiaryInfo.beneficiaryName)
        }

        if (policyBeneficiaryInfo.beneficiaryRoleNumber.isNotEmpty()) {
            requestBuilder.put(ManageBeneficiaryService.BENEFICIARY_ROLE_NUMBER, policyBeneficiaryInfo.beneficiaryRoleNumber)
        }

        requestBuilder.put(ManageBeneficiaryService.PREFERRED_COMMUNICATION, policyInfo.preferredCommunication)
        mockResponseFile = ManageBeneficiaryMockFactory.removeBeneficiary(callType)
        params = requestBuilder.build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SureCheckResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true

    companion object {
        const val OP0914_REMOVE_BENEFICIARY = "OP0914"
    }
}