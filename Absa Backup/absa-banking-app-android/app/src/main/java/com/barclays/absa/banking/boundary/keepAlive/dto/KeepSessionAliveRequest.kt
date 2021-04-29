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
package com.barclays.absa.banking.boundary.keepAlive.dto

import com.barclays.absa.banking.boundary.model.SessionAlive
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0997_SESSION_KEEP_ALIVE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class KeepSessionAliveRequest<T>(iVal: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(BuildConfigHelper.serverPath, responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0997_SESSION_KEEP_ALIVE)
                .put(Transaction.I_VAL, iVal)
                .build()

        mockResponseFile = "op0997_session_alive.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SessionAlive::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}