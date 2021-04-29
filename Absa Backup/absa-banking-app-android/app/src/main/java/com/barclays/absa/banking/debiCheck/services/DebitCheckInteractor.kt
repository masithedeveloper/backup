/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.debiCheck.services

import com.barclays.absa.banking.debiCheck.services.dto.*
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedResponseListener

class DebitCheckInteractor : AbstractInteractor(), DebiCheckService {

    override fun fetchPendingMandates(listener: ExtendedResponseListener<MandateResponse>) {
        submitRequest(PendingMandateRequest(listener))
    }

    override fun fetchMandates(accountNumber: String, mandateStatus: String, mandateReference: String, listener: ExtendedResponseListener<MandateResponse>) {
        submitRequest(MandateRequest(accountNumber, mandateStatus, mandateReference, listener))
    }

    override fun fetchDebiCheckTransactions(accountNumber: String, listener: ExtendedResponseListener<DebiCheckTransactionsResponse>) {
        submitRequest(DebiCheckTransactionsRequest(accountNumber, listener))
    }

    override fun isTransactionDisputable(transaction: DebiCheckTransaction, disputeReason: String, listener: ExtendedResponseListener<TransactionDisputableResponse>) {
        submitRequest(TransactionDisputableRequest(transaction, disputeReason, listener), "debicheck/op2103_check_transaction_disputable.json")
    }

    override fun disputeDebiCheckTransaction(paymentKey: String, listener: ExtendedResponseListener<DebiCheckDisputeResponse>) {
        submitRequest(DebiCheckDisputeTransactionRequest(paymentKey, listener), "debicheck/op2104_dispute_transaction.json")
    }

    override fun suspendMandate(mandateReference: String, suspendReasonCode: String, listener: ExtendedResponseListener<DebiCheckSuspendResponse>) {
        submitRequest(DebiCheckSuspendMandateRequest(mandateReference, suspendReasonCode, listener))
    }

    override fun acceptDebitOrder(transactionId: String, listener: ExtendedResponseListener<DebitOrderAuthoriseResponse>) {
        if (BuildConfigHelper.STUB) {
            stubFailure = false
            val mockFile: String = when {
                stubFailure -> "debicheck/op0843_authorise_mandate_failure.json"
                firstCall -> "debicheck/op0843_authorise_mandate_verify.json"
                else -> "debicheck/op0843_approve_mandate.json"
            }
            submitRequest(DebitOrderAuthoriseRequest(transactionId, true, "MS02", listener), mockFile)
        } else {
            submitRequest(DebitOrderAuthoriseRequest(transactionId, true, "MS02", listener))
        }
    }

    override fun rejectDebitOrder(transactionId: String, rejectReasonCode: String, listener: ExtendedResponseListener<DebitOrderAuthoriseResponse>) {
        if (BuildConfigHelper.STUB) {
            stubFailure = false
            val mockFile: String = when {
                stubFailure -> "debicheck/op0843_authorise_mandate_failure.json"
                firstCall -> "debicheck/op0843_authorise_mandate_verify.json"
                else -> "debicheck/op0843_reject_mandate.json"
            }
            submitRequest(DebitOrderAuthoriseRequest(transactionId, false, rejectReasonCode, listener), mockFile)
        } else {
            submitRequest(DebitOrderAuthoriseRequest(transactionId, false, rejectReasonCode, listener))
        }
    }

    override fun fetchNumberOfPendingMandates(listener: ExtendedResponseListener<NumberOfPendingMandatesResponse>) {
        submitRequest(NumberOfPendingMandatesRequest(listener))
    }
}