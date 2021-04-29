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

class FamilyMemberCoverAmountsRequest<T>(multipleDependentsDetails: MultipleDependentsDetails, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {

        params = RequestParams.Builder(FlexiFuneralService.OP2144_FETCH_FAMILY_MEMBER_COVER_AMOUNTS)
                .put("planCode", multipleDependentsDetails.planCode)
                .put("coverAmount", multipleDependentsDetails.coverAmount)
                .put("dependentInitials", multipleDependentsDetails.dependentsInitials)
                .put("dependentSurname", multipleDependentsDetails.dependentsSurname)
                .put("dependentGender", multipleDependentsDetails.dependentsGender)
                .put("dependentDateOfBirth", multipleDependentsDetails.dependentsDateOfBirth)
                .put("dependentRelationship", multipleDependentsDetails.dependentsRelationship)
                .build()
        mockResponseFile = "flexi_funeral/op2144_flexi_funeral_add_single_family_member_cover_options.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = FamilyMemberCoverAmountsResponse::class.java as Class<T>

    override fun isEncrypted() = true
}