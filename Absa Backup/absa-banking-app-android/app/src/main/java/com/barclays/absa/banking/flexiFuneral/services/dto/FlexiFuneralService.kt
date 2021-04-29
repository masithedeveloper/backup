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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.framework.ExtendedResponseListener

interface FlexiFuneralService {

    companion object {
        const val OP2142_FETCH_VALIDATION_RULES = "OP2142"
        const val OP2143_FETCH_MAIN_MEMBER_COVER_AMOUNTS = "OP2143"
        const val OP2144_FETCH_FAMILY_MEMBER_COVER_AMOUNTS = "OP2144"
        const val OP2145_CALCULATE_FLEXI_FUNERAL_PREMIUM = "OP2145"
        const val OP2146_ADD_FLEXI_FUNERAL_BENEFICIARY = "OP2146"
        const val OP2147_APPLY_FOR_FLEXI_FUNERAL = "OP2147"
    }

    fun fetchValidationRules(extendedResponseListener: ExtendedResponseListener<FlexiFuneralValidationRulesResponse>)
    fun fetchMainMemberCoverAmounts(extendedResponseListener: ExtendedResponseListener<MainMemberCoverAmountsResponse>)
    fun fetchFamilyMemberCoverAmounts(singleDependentDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<FamilyMemberCoverAmountsResponse>)
    fun addFlexiFuneralBeneficiary(beneficiaryDetails: FlexiFuneralBeneficiaryDetails, extendedResponseListener: ExtendedResponseListener<AddBeneficiaryStatusResponse>)
    fun fetchFlexiFuneralPremium(multipleDependentsDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<FlexiFuneralPremiumResponse>)
    fun applyForFlexiFuneral(applyForFlexiFuneralData: ApplyForFlexiFuneralData, extendedResponseListener: ExtendedResponseListener<ApplyForFlexiFuneralResponse>)
}