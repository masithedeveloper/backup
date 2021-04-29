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

import com.barclays.absa.banking.boundary.model.OverdraftSnooze
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0828_OVERDRAFT_SNOOZE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class OverdraftSnoozeRequest<T>(snoozeResponseListener: ExtendedResponseListener<T>, snoozeOptionValue: String)
    : ExtendedRequest<T>(snoozeResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0828_OVERDRAFT_SNOOZE)
                .put(Transaction.OD_SOURCE, snoozeOptionValue)
                .put(Transaction.OD_FUNCTION_NAME, "persistSnoozedData")
                .build()

        mockResponseFile = "vcl/op0828_overdraft_snooze.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftSnooze::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}