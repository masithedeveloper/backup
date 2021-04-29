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
package com.barclays.absa.banking.account.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0874_GET_ARCHIVED_STATEMENT_LIST
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class ArchivedStatementListRequest<T>(accountNumber: String, fromDate: String, toDate: String,
                                      responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0874_GET_ARCHIVED_STATEMENT_LIST)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put("accountNumber", accountNumber)
                .put("fromDate", fromDate)
                .put("toDate", toDate)
                .put("statementType", "ARCHIVE")
                .build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ArchivedStatementListResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
