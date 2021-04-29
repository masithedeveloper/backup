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
package com.barclays.absa.banking.debiCheck.services

import com.barclays.absa.banking.debiCheck.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface DebiCheckService {
    fun fetchPendingMandates(listener: ExtendedResponseListener<MandateResponse>)
    fun fetchNumberOfPendingMandates(listener: ExtendedResponseListener<NumberOfPendingMandatesResponse>)
    fun fetchMandates(accountNumber: String, mandateStatus: String, mandateReference: String, listener: ExtendedResponseListener<MandateResponse>)
    fun acceptDebitOrder(transactionId: String, listener: ExtendedResponseListener<DebitOrderAuthoriseResponse>)
    fun rejectDebitOrder(transactionId: String, rejectReasonCode: String, listener: ExtendedResponseListener<DebitOrderAuthoriseResponse>)
    fun fetchDebiCheckTransactions(accountNumber: String, listener: ExtendedResponseListener<DebiCheckTransactionsResponse>)
    fun isTransactionDisputable(transaction: DebiCheckTransaction, disputeReason: String, listener: ExtendedResponseListener<TransactionDisputableResponse>)
    fun disputeDebiCheckTransaction(paymentKey: String, listener: ExtendedResponseListener<DebiCheckDisputeResponse>)
    fun suspendMandate(mandateReference: String, suspendReasonCode: String, listener: ExtendedResponseListener<DebiCheckSuspendResponse>)
}