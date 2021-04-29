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

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteSummary
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory

import com.barclays.absa.banking.overdraft.services.OverdraftService.OP2013_OVERDRAFT_QUOTE_SUMMARY
import com.barclays.absa.banking.overdraft.services.OverdraftService.QUOTE_NO

class OverdraftQuoteSummaryRequest<T>(quoteNumber: String, overdraftQuoteSummaryResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(overdraftQuoteSummaryResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2013_OVERDRAFT_QUOTE_SUMMARY)
                .put(QUOTE_NO, quoteNumber)
                .build()

        mockResponseFile = OverdraftMockFactory.overdraftSummary()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftQuoteSummary::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}