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
package com.barclays.absa.banking.overdraft.services.dto

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse
import com.barclays.absa.banking.boundary.model.overdraft.RejectOverdraftObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.*

class OverdraftRejectQuoteRequest<T>(rejectOverdraftQuote: RejectOverdraftObject,
                                     overdraftScoreResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(overdraftScoreResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2009_OVERDRAFT_REJECT_QUOTE)
                .put(OVERDRAFT_ACCOUNT, rejectOverdraftQuote.accountNumber)
                .put(OVERDRAFT_CPP_AMOUNT, rejectOverdraftQuote.cppAmount)
                .put(QUOTE_REFERENCE_NUMBER, rejectOverdraftQuote.quoteNumber)
                .put(OVERDRAFT_AMOUNT, rejectOverdraftQuote.overdraftAmount)
                .build()

        mockResponseFile = OverdraftMockFactory.overdraftRejectQuote()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}