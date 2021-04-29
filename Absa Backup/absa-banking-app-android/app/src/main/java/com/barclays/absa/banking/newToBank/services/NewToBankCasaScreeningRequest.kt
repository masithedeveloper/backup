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
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2025_CASA_SCREENING
import com.barclays.absa.banking.newToBank.services.dto.CasaScreeningResponse

class NewToBankCasaScreeningRequest<T>(nationality: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2025_CASA_SCREENING)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.NATIONALITY.key, nationality)
                .build()

        mockResponseFile = "new_to_bank/op2025_casa_screening.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CasaScreeningResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}