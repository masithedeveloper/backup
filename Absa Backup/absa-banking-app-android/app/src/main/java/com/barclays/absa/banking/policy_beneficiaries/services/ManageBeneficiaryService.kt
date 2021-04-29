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
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.policy_beneficiaries.services.dto.PolicyBeneficiaryInfo
import com.barclays.absa.banking.policy_beneficiaries.services.dto.PolicyInfo
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType

interface ManageBeneficiaryService {

    companion object {
        const val OP0909_ADD_BENEFICIARY = "OP0909"
        const val OP2174_ADD_EXERGY_BENEFICIARY = "OP2174"
        const val OP2175_EDIT_EXERGY_BENEFICIARY = "OP2175"
        const val OP2176_REMOVE_EXERGY_BENEFICIARY = "OP2176"

        const val TITLE = "title"
        const val FIRST_NAME = "firstName"
        const val SURNAME = "surname"
        const val INITIALS = "initials"
        const val ID_TYPE = "idType"
        const val ID_NUMBER = "idNumber"
        const val DATE_OF_BIRTH = "dateOfBirth"
        const val RELATIONSHIP = "relationship"
        const val ALLOCATION = "allocation"
        const val CELLPHONE_NUMBER = "cellphoneNumber"
        const val EMAIL_ADDRESS = "emailAddress"
        const val ADDRESSLINE1 = "addressLine1"
        const val ADDRESSLINE2 = "addressLine2"
        const val SUBURB = "suburbRsa"
        const val TOWN = "town"
        const val POSTAL_CODE = "postalCode"
        const val POLICY_NUMBER = "policyNumber"
        const val SOURCE_OF_FUND = "sourceOfFund"
        const val BENEFICIARY_LIFE_CLIENT_CODE = "beneficiaryLifeClientCode"
        const val BENEFICIARY_NAME = "beneficiaryName"
        const val BENEFICIARY_ALLOCATION = "beneficiaryAllocation"
        const val BENEFICIARY_ROLE_NUMBER = "beneficiaryRoleNumber"
        const val PREFERRED_COMMUNICATION = "preferredCommunication"
        const val ROLE_NUMBER = "roleNumber"
        const val LIFE_CLIENT_CODE = "lifeClientCode"
        const val POLICY_TYPE = "policyType"
    }

    fun addBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun changeBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun removeBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, callType: CallType, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun addExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun editExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun removeExergyBeneficiary(policyInfo: PolicyInfo, policyBeneficiaryInfo: PolicyBeneficiaryInfo, extendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
}