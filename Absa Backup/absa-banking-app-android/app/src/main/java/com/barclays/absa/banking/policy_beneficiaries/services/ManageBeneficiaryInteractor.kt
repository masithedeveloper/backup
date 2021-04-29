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

package com.barclays.absa.banking.policy_beneficiaries.services

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.policy_beneficiaries.services.dto.*
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType

class ManageBeneficiaryInteractor : AbstractInteractor(), ManageBeneficiaryService {

    override fun addBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        val addBeneficiaryRequest = AddPolicyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, callType, extendedResponseListener)
        submitRequest(addBeneficiaryRequest)
    }

    override fun changeBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        val changeBeneficiaryRequest = ChangePolicyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, callType, extendedResponseListener)
        submitRequest(changeBeneficiaryRequest)
    }

    override fun removeBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        val changeBeneficiaryRequest = RemovePolicyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, callType, extendedResponseListener)
        submitRequest(changeBeneficiaryRequest)
    }

    override fun addExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        submitRequest(AddExergyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, extendedResponseListener))
    }

    override fun editExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        submitRequest(EditExergyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, extendedResponseListener))
    }

    override fun removeExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        submitRequest(RemoveExergyBeneficiaryRequest(policyInfo, policyBeneficiaryInfo, extendedResponseListener))
    }
}