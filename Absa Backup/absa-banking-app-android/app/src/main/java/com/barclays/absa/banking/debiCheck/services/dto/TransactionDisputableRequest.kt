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
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class TransactionDisputableRequest<T>(transaction: DebiCheckTransaction, disputeReason: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OpCodeParams.OP2103_CHECK_MANDATE_DISPUTABLE)
                .put(TransactionParams.Transaction.ACCOUNTS, transaction.fromAccount)
                .put(TransactionParams.Transaction.PAYMENT_KEY, transaction.paymentKey)
                .put("actionDate", transaction.actionDate)
                .put("disputeReason", disputeReason)
                .build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionDisputableResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true

}