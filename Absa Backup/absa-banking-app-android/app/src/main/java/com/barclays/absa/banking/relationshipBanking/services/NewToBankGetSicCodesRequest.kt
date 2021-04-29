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
package com.barclays.absa.banking.relationshipBanking.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.newToBank.services.NewToBankService
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesResponse

class NewToBankGetSicCodesRequest<T>(extendedResponseListener: ExtendedResponseListener<T>, sicCodeLookUpType: String) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(NewToBankService.OP2100_GET_SIC_CODES)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.SIC_CODES_LOOKUP_TYPE.key, sicCodeLookUpType).build()
        mockResponseFile = "new_to_bank/business_banking_sic_codes.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = SicCodesResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}