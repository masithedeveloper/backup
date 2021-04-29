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

package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.ExergyBranchListResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.services.PaymentsInteractor

class FetchExergyBranchListRequest<T>(bankName: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder(PaymentsInteractor.OP2195_FETCH_EXERGY_BANK_DETAILS)
                .put("bankName", bankName)
                .build()

        mockResponseFile = "insurance/op2195_exergy_branch_list.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ExergyBranchListResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}