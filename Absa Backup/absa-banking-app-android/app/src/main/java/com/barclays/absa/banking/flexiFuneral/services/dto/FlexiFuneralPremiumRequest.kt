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

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class FlexiFuneralPremiumRequest<T>(multipleDependentsDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder(FlexiFuneralService.OP2145_CALCULATE_FLEXI_FUNERAL_PREMIUM)
                .put("planCode", multipleDependentsDetails.planCode)
                .put("coverAmount", multipleDependentsDetails.coverAmount)
                .put("dependentsInitials", multipleDependentsDetails.dependentsInitials)
                .put("dependentsSurname", multipleDependentsDetails.dependentsSurname)
                .put("dependentsGender", multipleDependentsDetails.dependentsGender)
                .put("dependentsDateOfBirth", multipleDependentsDetails.dependentsDateOfBirth)
                .put("dependentsRelationship", multipleDependentsDetails.dependentsRelationship)
                .put("dependentsCoverAmount", multipleDependentsDetails.dependentsCoverAmount)
                .put("dependentsPremiumAmount", multipleDependentsDetails.dependentsPremium)
                .build()

        mockResponseFile = "flexi_funeral/op2145_flexi_funeral_flexi_funeral_premium.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = FlexiFuneralPremiumResponse::class.java as Class<T>

    override fun isEncrypted() = true
}