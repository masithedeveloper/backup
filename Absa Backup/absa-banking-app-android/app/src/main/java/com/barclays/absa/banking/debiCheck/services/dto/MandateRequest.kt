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
package com.barclays.absa.banking.debiCheck.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class MandateRequest<T>(accountNumber: String, mandateStatus: String, mandateReference: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {
    companion object {
        const val OP2131_FETCH_MANDATE = "OP2131"
    }

    init {
        params = RequestParams.Builder(OP2131_FETCH_MANDATE)
                .put("mandateStatus", mandateStatus)
                .put("accountNumber", accountNumber)
                .put("mandateReferenceNumber", mandateReference)
                .build()

        mockResponseFile = "debicheck/op2131_get_mandates.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = MandateResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}