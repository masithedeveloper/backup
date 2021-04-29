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
package com.barclays.absa.banking.home.services.clickToCall.dto

import com.barclays.absa.banking.boundary.model.CallMeOverview
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0851_CALL_ME
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class CallMeOverviewRequest<T>(phoneNumber: String, emailId: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0851_CALL_ME)
                .put(TransactionParams.Transaction.SERVICE_CELL_NUMBER_AIRTIME, phoneNumber)
                .put(TransactionParams.Transaction.EMAIL_ID, emailId)
                .build()

        mockResponseFile = "op0851_submit_call_me"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CallMeOverview::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}