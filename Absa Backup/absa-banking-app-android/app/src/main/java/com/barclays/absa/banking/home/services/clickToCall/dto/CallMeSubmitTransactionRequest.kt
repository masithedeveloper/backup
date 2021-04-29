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
import com.barclays.absa.banking.boundary.model.CallMeResult
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0875_CALL_ME
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.data.ResponseObject

class CallMeSubmitTransactionRequest<T>(responseObject: ResponseObject, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        val callMeOverview = responseObject as CallMeOverview
        params = RequestParams.Builder()
                .put(OP0875_CALL_ME)
                .put(Transaction.SURECHECK_REFERENCE_NUMBER, callMeOverview.referenceNumber)
                .put(Transaction.SERVICE_CELL_NUMBER_AIRTIME, callMeOverview.cellNumber)
                .put(Transaction.EMAIL_ID, callMeOverview.emailId)
                .put(Transaction.QUERY_TYPE, callMeOverview.queryType)
                .put("campaignSourceCode", "View_Funeral")
                .build()

        mockResponseFile = "op0875_submit_transaction_reference_number"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CallMeResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}