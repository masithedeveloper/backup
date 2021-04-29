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
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.*

class OverdraftScoreRequest<T>(overdraftSetup: OverdraftSetup, overdraftScoreResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(overdraftScoreResponseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP2005_OVERDRAFT_SCORE)
                .put(FACILITY_REQUIRED, "3")
                .put(PRODUCT_CODE, "11001")
                .put(OVERDRAFT_ACCOUNT_NUMBER, overdraftSetup.accountNumber)
                .put(OVERDRAFT_TYPE, "Fixed")
                .put(OVERDRAFT_AMOUNT, overdraftSetup.overdraftAmount)
                .put(PURPOSE_OF_OVERDRAFT, "02")
                .put(OVERDRAFT_CHECK_CPP, java.lang.Boolean.toString(overdraftSetup.creditProtection))
                .put(CREDIT_DEFAULT_DELIVERY, overdraftSetup.creditAgreementNoticeMethod)
                .put(MARKETINGUPDATESFLAG, overdraftSetup.marketingConsent)
                .put(SMSMARKETING, overdraftSetup.smsChannel)
                .put(TELEPHONEMAKERTING, overdraftSetup.telephoneChannel)
                .put(EMAILMARKETING, overdraftSetup.emailChannel)
                .put(MAILMARKETING, overdraftSetup.directMailChannel)
                .build()

        mockResponseFile = OverdraftMockFactory.overdraftScore()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}