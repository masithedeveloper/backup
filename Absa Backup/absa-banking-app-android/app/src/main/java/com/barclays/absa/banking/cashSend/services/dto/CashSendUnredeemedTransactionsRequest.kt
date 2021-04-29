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
package com.barclays.absa.banking.cashSend.services.dto

import com.barclays.absa.banking.boundary.model.cashSend.CashsendUnredeemTransactions
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.home.services.HomeScreenService.OP1301_ACCOUNT_DETAILS
import java.lang.Boolean.TRUE

class CashSendUnredeemedTransactionsRequest<T>(extendedResponseListener: ExtendedResponseListener<T>, accountNumber: String, isCashSendPlus: Boolean) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP1301_ACCOUNT_DETAILS)
                .put(Transaction.ACCOUNT_NUMBER, accountNumber)
                .put(Transaction.CASHSEND_UNREDEEMED_BOOLEAN, TRUE.toString())
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
                .build()

        mockResponseFile = MockFactory.cashSendUnredeemedTransactions()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CashsendUnredeemTransactions::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}