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

import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryResult
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.cashSend.services.CashSendService.OP0614_SEND_BENEFICIARY_CASHSEND_RESULT
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class CashSendBeneficiaryResultRequest<T>(isCashSendPlus: Boolean, transactionReferenceId: String,
                                          shouldUseResultStub: Boolean,
                                          extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0614_SEND_BENEFICIARY_CASHSEND_RESULT)
                .put(Transaction.SERVICE_TXN_REF_CASHSEND, transactionReferenceId)
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
                .build()

        mockResponseFile = if (shouldUseResultStub)
            "beneficiaries/op0614_cash_send_success_result.json" else
            "cash_send/op0614_send_beneficiary_cashsend_result_verify.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CashSendBeneficiaryResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}