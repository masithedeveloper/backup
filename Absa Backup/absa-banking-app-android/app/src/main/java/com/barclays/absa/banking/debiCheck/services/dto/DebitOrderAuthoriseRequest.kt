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
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0843_AUTHORISE_MANDATE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class DebitOrderAuthoriseRequest<T>(transactionId: String, accepted: Boolean, rejectReasonCode: String?,
                                    responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        val requestBuilder = RequestParams.Builder()
                .put(OP0843_AUTHORISE_MANDATE)
                .put(Transaction.MANDATE_TRANSACTION_ID, transactionId)
                .put(Transaction.ACCEPTED, accepted.toString())
                .put(Transaction.REJECT_REASON, rejectReasonCode)

        params = requestBuilder.build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitOrderAuthoriseResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}