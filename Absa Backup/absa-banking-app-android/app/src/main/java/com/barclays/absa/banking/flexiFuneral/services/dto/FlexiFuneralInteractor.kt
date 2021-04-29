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

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener

class FlexiFuneralInteractor : AbstractInteractor(), FlexiFuneralService {
    override fun fetchValidationRules(extendedResponseListener: ExtendedResponseListener<FlexiFuneralValidationRulesResponse>) {
        submitRequest(FlexiFuneralValidationRulesRequest(extendedResponseListener))
    }

    override fun fetchMainMemberCoverAmounts(extendedResponseListener: ExtendedResponseListener<MainMemberCoverAmountsResponse>) {
        submitRequest(MainMemberCoverAmountsRequest(extendedResponseListener))
    }

    override fun fetchFamilyMemberCoverAmounts(singleDependentDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<FamilyMemberCoverAmountsResponse>) {
        submitRequest(FamilyMemberCoverAmountsRequest(singleDependentDetails, extendedResponseListener))
    }

    override fun addFlexiFuneralBeneficiary(beneficiaryDetails: FlexiFuneralBeneficiaryDetails, extendedResponseListener: ExtendedResponseListener<AddBeneficiaryStatusResponse>) {
        submitRequest(AddBeneficiaryRequest(beneficiaryDetails, extendedResponseListener))
    }

    override fun fetchFlexiFuneralPremium(multipleDependentsDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<FlexiFuneralPremiumResponse>) {
        submitRequest(FlexiFuneralPremiumRequest(multipleDependentsDetails, extendedResponseListener))
    }

    override fun applyForFlexiFuneral(applyForFlexiFuneralData: ApplyForFlexiFuneralData, extendedResponseListener: ExtendedResponseListener<ApplyForFlexiFuneralResponse>) {
        submitRequest(ApplyForFlexiFuneralRequest(applyForFlexiFuneralData, extendedResponseListener))
    }
}