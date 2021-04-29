/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.dualAuthorisations.services

import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionAcceptReject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0572_AUTHORISATION_TRANSACTION_ACCEPT_REJECT
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class AuthorisationTransactionAcceptRejectRequest<T>(transactionType: String, transactionTypeCode: String, authStatusCode: String,
                                                     transactionDateTime: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0572_AUTHORISATION_TRANSACTION_ACCEPT_REJECT)
                .put(Transaction.TRANSACTION_TYPE, transactionType)
                .put(Transaction.TRANSACTION_TYPE_CODE, transactionTypeCode)
                .put(Transaction.AUTH_STATUS_CODE, authStatusCode)
                .put(Transaction.TRANSACTION_DATE_TIME, transactionDateTime)
                .build()

        mockResponseFile = "op0572_authorisation_transaction_accept_reject.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AuthorisationTransactionAcceptReject::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}