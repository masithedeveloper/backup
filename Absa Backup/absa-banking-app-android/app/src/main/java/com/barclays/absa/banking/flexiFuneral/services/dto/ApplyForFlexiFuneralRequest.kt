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

class ApplyForFlexiFuneralRequest<T>(applyForFlexiFuneralData: ApplyForFlexiFuneralData, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder(FlexiFuneralService.OP2147_APPLY_FOR_FLEXI_FUNERAL)
                .put("bankName", "ABSA Bank")
                .put("dayOfDebit", applyForFlexiFuneralData.dayOfDebit)
                .put("accountToBeDebited", "${applyForFlexiFuneralData.accountToBeDebited} - ${applyForFlexiFuneralData.accountNumber}")
                .put("planCode", applyForFlexiFuneralData.planCode)
                .put("sourceOfFund", applyForFlexiFuneralData.sourceOfFund?.itemCode)
                .put("totalCoverAmount", applyForFlexiFuneralData.totalCoverAmount)
                .put("totalPremium", applyForFlexiFuneralData.totalPremium)
                .put("isReplacement", applyForFlexiFuneralData.isReplacement)
                .put("company", applyForFlexiFuneralData.company)
                .build()
        mockResponseFile = "flexi_funeral/op2147_apply_for_flexi_funeral.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = ApplyForFlexiFuneralResponse::class.java as Class<T>

    override fun isEncrypted() = true
}