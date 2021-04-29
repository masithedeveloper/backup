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

import com.barclays.absa.banking.boundary.model.overdraft.AcceptOverdraftObject
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.*

class OverdraftAcceptQuoteRequest<T>(acceptOverdraftQuote: AcceptOverdraftObject, overdraftScoreResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(overdraftScoreResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2008_OVERDRAFT_ACCEPT_QUOTE)
                .put(OVERDRAFT_ACCOUNT, acceptOverdraftQuote.accountNumber)
                .put(OVERDRAFT_TYPE, "Fixed")
                .put(OVERDRAFT_CPP_AMOUNT, acceptOverdraftQuote.cppAmount)
                .put(OVERDRAFT_CHECK_CPP, acceptOverdraftQuote.isCppChecked)
                .put(QUOTE_REFERENCE_NUMBER, acceptOverdraftQuote.quoteNumber)
                .put(OVERDRAFT_AMOUNT, acceptOverdraftQuote.overdraftAmount)
                .put(OVERDRAFT_SYSTEM_DECISION, acceptOverdraftQuote.systemDecision)
                .put(OVERDRAFT_SYSTEM_RESULT, acceptOverdraftQuote.systemResult)
                .build()

        mockResponseFile = OverdraftMockFactory.overdraftAcceptQuote()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}