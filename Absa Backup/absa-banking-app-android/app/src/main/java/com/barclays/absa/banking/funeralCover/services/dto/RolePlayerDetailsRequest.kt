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
package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.RolePlayerDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0859_COVER_RELATION_APPLY_PLAN

class RolePlayerDetailsRequest<T>(funeralCoverDetails: FuneralCoverDetails, rolePlayerDetails: FamilyMemberCoverDetails,
                                  responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0859_COVER_RELATION_APPLY_PLAN)
                .put(BMBConstants.PLAN_CODE, funeralCoverDetails.planCode)
                .put("sumAssured", funeralCoverDetails.mainMemberCover)
                .put("initials", rolePlayerDetails.initials)
                .put("surname", rolePlayerDetails.surname)
                .put("gender", rolePlayerDetails.gender)
                .put("dateOfBirth", rolePlayerDetails.dateOfBirth)
                .put("relationship", rolePlayerDetails.relationshipCode)
                .put("benefitCode", rolePlayerDetails.benefitCode)
                .build()
        mockResponseFile = "funeral_cover/op0859_cover_relation_apply_plan.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RolePlayerDetails::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}