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

package com.barclays.absa.banking.freeCover.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.freeCover.services.FreeCoverService.Companion.OP2191_APPLY_FOR_FREE_COVER

class ApplyForFreeCoverRequest<T>(applyFreeCoverData: ApplyFreeCoverData, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2191_APPLY_FOR_FREE_COVER)
                .put("coverAmount", applyFreeCoverData.coverAmount)
                .put("monthlyPremium", applyFreeCoverData.monthlyPremium)
                .put("accountHolderName", applyFreeCoverData.accountHolderName)
                .put("title", applyFreeCoverData.titleCode)
                .put("firstName", applyFreeCoverData.firstName)
                .put("surname", applyFreeCoverData.surname)
                .put("initials", applyFreeCoverData.initials)
                .put("idType", applyFreeCoverData.idType)
                .put("idNumber", applyFreeCoverData.idNumber)
                .put("dateOfBirth", applyFreeCoverData.dateOfBirth)
                .put("relationship", applyFreeCoverData.relationshipCode)
                .put("cellphoneNumber", applyFreeCoverData.cellphoneNumber)
                .put("emailAddress", applyFreeCoverData.emailAddress)
                .put("addressLine1", applyFreeCoverData.addressLineOne)
                .put("suburbRsa", applyFreeCoverData.suburbRsa)
                .put("town", applyFreeCoverData.town)
                .put("postalCode", applyFreeCoverData.postalCode)
                .put("sourceOfFund", applyFreeCoverData.sourceOfFund)
                .build()
        mockResponseFile = "free_cover/op2191_apply_for_free_cover_status.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ApplyForFreeCoverResponse::class.java as Class<T>
    override fun isEncrypted() = true
}