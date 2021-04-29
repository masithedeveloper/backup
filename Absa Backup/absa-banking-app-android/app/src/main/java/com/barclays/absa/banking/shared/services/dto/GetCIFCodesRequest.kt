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
package com.barclays.absa.banking.shared.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.shared.services.SharedService.Companion.OP2022_GET_CODES

class GetCIFCodesRequest<T>(sourceOfFundsFilter: SourceOfFundsLookUpType, lookUpType: CIFCodeLookUpType, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2022_GET_CODES)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.CODES_LOOKUP_TYPE.key, lookUpType.name)
                .put(NewToBankParams.SOURCE_OF_FUNDS_RULE.key, sourceOfFundsFilter.key)
                .build()

        mockResponseFile = "shared/op2022_codes_lookup_all.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = GetCodesResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}