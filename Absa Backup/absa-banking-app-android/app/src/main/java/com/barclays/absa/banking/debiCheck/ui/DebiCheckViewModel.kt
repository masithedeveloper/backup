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

package com.barclays.absa.banking.debiCheck.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.debiCheck.responseListeners.*
import com.barclays.absa.banking.debiCheck.services.DebitCheckInteractor
import com.barclays.absa.banking.debiCheck.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class DebiCheckViewModel : BaseViewModel() {

    var debitCheckInteractor: DebitCheckInteractor = DebitCheckInteractor()

    private val pendingMandateResponseListener: ExtendedResponseListener<MandateResponse> by lazy { DebitCheckDebitOrderExtendedResponseListener(this) }
    private val mandateResponseListener: ExtendedResponseListener<MandateResponse> by lazy { DebitCheckMandateExtendedResponseListener(this) }
    private val debitOrderApprovalResponseListener: ExtendedResponseListener<DebitOrderAuthoriseResponse> by lazy { DebitOrderApprovalExtendedResponseListener(this) }
    private val debitOrderRejectionResponseListener: ExtendedResponseListener<DebitOrderAuthoriseResponse> by lazy { DebitCheckOrderRejectionExtendedResponseListener(this) }
    private val debiCheckTransactionsResponseListener: ExtendedResponseListener<DebiCheckTransactionsResponse> by lazy { DebiCheckTransactionsExtendedResponseListener(this) }
    private val debiCheckTransactionDisputableResponseListener: ExtendedResponseListener<TransactionDisputableResponse> by lazy { DebiCheckTransactionDisputableExtendedResponseListener(this) }
    private val debiCheckDisputeTransactionResponseListener: ExtendedResponseListener<DebiCheckDisputeResponse> by lazy { DebiCheckDisputeTransactionExtendedResponseListener(this) }
    private val debiCheckSuspendMandateResponseListener: ExtendedResponseListener<DebiCheckSuspendResponse> by lazy { DebiCheckSuspendMandateExtendedResponseListener(this) }

    val pendingMandateResponse = MutableLiveData<MandateResponse>()
    val mandateResponse = MutableLiveData<MandateResponse>()
    var debitOrderAcceptResponse = MutableLiveData<DebitOrderAuthoriseResponse>()
    var debitOrderRejectResponse = MutableLiveData<DebitOrderAuthoriseResponse>()
    val debiCheckTransactionsResponse = MutableLiveData<DebiCheckTransactionsResponse>()
    val debiCheckDisputeTransactionResponse = MutableLiveData<DebiCheckDisputeResponse>()
    val debiCheckTransactionDisputableResponse = MutableLiveData<TransactionDisputableResponse>()
    val debiCheckSuspendResponse = MutableLiveData<DebiCheckSuspendResponse>()
    val networkUnreachableLiveData = MutableLiveData<ResponseObject>()

    var disputeReasonCode: String = ""
    var suspendReasonCode: String = ""
    var rejectionReasonCode: String = ""

    var selectedTransaction: DebiCheckTransaction? = null
    var selectedDebitOrder: DebiCheckMandateDetail = DebiCheckMandateDetail()
    var currentFlow: Flow = Flow.UNKNOWN

    fun approveDebitOrder(firstCall: Boolean) {
        debitCheckInteractor.setFirstCall(firstCall)
        debitCheckInteractor.acceptDebitOrder(selectedDebitOrder.mandateRequestTransactionId, debitOrderApprovalResponseListener)
    }

    fun rejectDebitOrder(firstCall: Boolean = true) {
        debitCheckInteractor.setFirstCall(firstCall)
        debitCheckInteractor.rejectDebitOrder(selectedDebitOrder.mandateRequestTransactionId, rejectionReasonCode, debitOrderRejectionResponseListener)
    }

    fun fetchPendingDebitOrders() {
        debitCheckInteractor.fetchPendingMandates(pendingMandateResponseListener)
    }

    fun fetchMandates(accountNumber: String, mandateStatus: String, mandateReference: String) {
        debitCheckInteractor.fetchMandates(accountNumber, mandateStatus, mandateReference, mandateResponseListener)
    }

    fun fetchDebiCheckTransactions(accountNumber: String) {
        debitCheckInteractor.fetchDebiCheckTransactions(accountNumber, debiCheckTransactionsResponseListener)
    }

    fun disputeTransaction(paymentKey: String) {
        debitCheckInteractor.disputeDebiCheckTransaction(paymentKey, debiCheckDisputeTransactionResponseListener)
    }

    fun checkIfTransactionDisputable(transaction: DebiCheckTransaction, disputeReason: String) {
        debitCheckInteractor.isTransactionDisputable(transaction, disputeReason, debiCheckTransactionDisputableResponseListener)
    }

    fun suspendMandate() {
        debitCheckInteractor.suspendMandate(selectedDebitOrder.mandateReferenceNumber, suspendReasonCode, debiCheckSuspendMandateResponseListener)
    }

    enum class Flow {
        APPROVED,
        SUSPENDED,
        PENDING,
        TRANSACTIONS,
        UNKNOWN
    }
}