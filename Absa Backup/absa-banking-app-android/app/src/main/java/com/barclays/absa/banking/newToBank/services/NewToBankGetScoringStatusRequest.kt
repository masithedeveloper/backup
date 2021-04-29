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
package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2036_GET_SCORING_STATUS
import com.barclays.absa.banking.newToBank.services.dto.GetScoringStatusResponse

class NewToBankGetScoringStatusRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2036_GET_SCORING_STATUS)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .build()

        mockResponseFile = "new_to_bank/op2036_scoring_success.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = GetScoringStatusResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}