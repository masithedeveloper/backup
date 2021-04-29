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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.debiCheck.responseListeners.*
import com.barclays.absa.banking.debiCheck.services.DebitCheckInteractor
import com.barclays.absa.banking.debiCheck.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class DebiCheckViewModelTest {

    private var testSubject = DebiCheckViewModel()
    private var debiCheckMockService = mock<DebitCheckInteractor>()
    private lateinit var debitOrderDetails: DebiCheckMandateDetail

    @Captor
    private lateinit var debitOrderApprovalCaptor: ArgumentCaptor<DebitOrderApprovalExtendedResponseListener>

    @Captor
    private lateinit var debitOrderRejectionCaptor: ArgumentCaptor<DebitCheckOrderRejectionExtendedResponseListener>

    @Captor
    private lateinit var debitOrderFetchPendingMandatesCaptor: ArgumentCaptor<DebitCheckDebitOrderExtendedResponseListener>

    @Captor
    private lateinit var debitOrderFetchMandatesCaptor: ArgumentCaptor<DebitCheckMandateExtendedResponseListener>

    @Captor
    private lateinit var debiCheckTransactionsCaptor: ArgumentCaptor<DebiCheckTransactionsExtendedResponseListener>

    @Captor
    private lateinit var debiCheckDisputeTransactionCaptor: ArgumentCaptor<DebiCheckDisputeTransactionExtendedResponseListener>

    @Captor
    private lateinit var debiCheckTransactionDisputableCaptor: ArgumentCaptor<DebiCheckTransactionDisputableExtendedResponseListener>

    @Captor
    private lateinit var debiCheckSuspendMandateCaptor: ArgumentCaptor<DebiCheckSuspendMandateExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.debitCheckInteractor = debiCheckMockService
        debitOrderDetails = DebiCheckMandateDetail()
    }

    @Test
    fun shouldReturnApproveDebitOrderWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebitOrderApprovalExtendedResponseListener(testSubject)
        val debiCheckResponse = DebitOrderAuthoriseResponse()
        val transactionId = "SKSAJDKC6371"
        val successMessage = "Success"

        Mockito.`when`(debiCheckMockService.acceptDebitOrder(transactionId, debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitOrderApprovalExtendedResponseListener>(1).onSuccess(debiCheckResponse.apply {
                mandates = listOf(debitOrderDetails)
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<DebitOrderAuthoriseResponse>>()
        debiCheckViewModelSpy.debitOrderAcceptResponse.observeForever(observer)

        debiCheckMockService.acceptDebitOrder(transactionId, debiCheckResponseListener)
        verify(debiCheckMockService).acceptDebitOrder(any(), capture(debitOrderApprovalCaptor))

        Assert.assertEquals(debitOrderApprovalCaptor.value, debiCheckResponseListener)
        Assert.assertEquals(debiCheckViewModelSpy.debitOrderAcceptResponse.value?.transactionStatus, successMessage)
        Assert.assertNotNull(debiCheckViewModelSpy.debitOrderAcceptResponse.value)
        Assert.assertEquals(1, debiCheckViewModelSpy.debitOrderAcceptResponse.value?.mandates?.size)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnRejectDebitOrderWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebitCheckOrderRejectionExtendedResponseListener(testSubject)
        val debiCheckResponse = DebitOrderAuthoriseResponse()
        val transactionId = "SKSAJDKC6371"
        val rejectionReasonCode = "Unknown creditor"
        val successMessage = "Success"

        val mandateList = mutableListOf<DebiCheckMandateDetail>()
        mandateList.add(DebiCheckMandateDetail())
        debiCheckResponse.mandates = mandateList

        Mockito.`when`(debiCheckMockService.rejectDebitOrder(transactionId, rejectionReasonCode, debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitCheckOrderRejectionExtendedResponseListener>(2).onSuccess(debiCheckResponse.apply {
                mandates = listOf(debitOrderDetails)
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<DebitOrderAuthoriseResponse>>()
        debiCheckViewModelSpy.debitOrderRejectResponse.observeForever(observer)

        debiCheckMockService.rejectDebitOrder(transactionId, rejectionReasonCode, debiCheckResponseListener)
        verify(debiCheckMockService).rejectDebitOrder(any(), any(), capture(debitOrderRejectionCaptor))

        Assert.assertEquals(debitOrderRejectionCaptor.value, debiCheckResponseListener)
        Assert.assertEquals(debiCheckViewModelSpy.debitOrderRejectResponse.value?.transactionStatus, successMessage)
        Assert.assertNotNull(debiCheckViewModelSpy.debitOrderRejectResponse.value)
        Assert.assertEquals(1, debiCheckViewModelSpy.debitOrderRejectResponse.value?.mandates?.size)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnFetchPendingMandatesWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebitCheckDebitOrderExtendedResponseListener(testSubject)
        val debiCheckResponse = MandateResponse()

        Mockito.`when`(debiCheckMockService.fetchPendingMandates(debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitCheckDebitOrderExtendedResponseListener>(0).onSuccess(debiCheckResponse.apply {
                mandates = listOf(debitOrderDetails)
            })
        }

        val observer = mock<Observer<MandateResponse>>()
        debiCheckViewModelSpy.pendingMandateResponse.observeForever(observer)

        debiCheckMockService.fetchPendingMandates(debiCheckResponseListener)
        verify(debiCheckMockService).fetchPendingMandates(capture(debitOrderFetchPendingMandatesCaptor))

        Assert.assertEquals(debitOrderFetchPendingMandatesCaptor.value, debiCheckResponseListener)
        Assert.assertNotNull(debiCheckViewModelSpy.pendingMandateResponse.value)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnFetchMandatesWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebitCheckMandateExtendedResponseListener(testSubject)
        val debiCheckResponse = MandateResponse()
        val accountNumber = "6543785553"
        val mandateStatus = "Active"
        val mandateReference = "RTDF5656CFGHG788"

        Mockito.`when`(debiCheckMockService.fetchMandates(accountNumber, mandateStatus, mandateReference, debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitCheckMandateExtendedResponseListener>(3).onSuccess(debiCheckResponse.apply {
                mandates = listOf(debitOrderDetails)
            })
        }

        val observer = mock<Observer<MandateResponse>>()
        debiCheckViewModelSpy.mandateResponse.observeForever(observer)

        debiCheckMockService.fetchMandates(accountNumber, mandateStatus, mandateReference, debiCheckResponseListener)
        verify(debiCheckMockService).fetchMandates(any(), any(), any(), capture(debitOrderFetchMandatesCaptor))

        Assert.assertEquals(debitOrderFetchMandatesCaptor.value, debiCheckResponseListener)
        Assert.assertNotNull(debiCheckViewModelSpy.mandateResponse.value)
        Assert.assertNotNull(debiCheckViewModelSpy.mandateResponse.value?.mandates)
        Assert.assertEquals(1, debiCheckViewModelSpy.mandateResponse.value?.mandates?.size)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnFetchDebiCheckTransactionsWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebiCheckTransactionsExtendedResponseListener(testSubject)
        val debiCheckResponse = DebiCheckTransactionsResponse()
        val accountNumber = "6543785553"
        val debiCheckTransaction = DebiCheckTransaction()

        Mockito.`when`(debiCheckMockService.fetchDebiCheckTransactions(accountNumber, debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebiCheckTransactionsExtendedResponseListener>(1).onSuccess(debiCheckResponse.apply {
                transactions.add(debiCheckTransaction)
            })
        }

        val observer = mock<Observer<DebiCheckTransactionsResponse>>()
        debiCheckViewModelSpy.debiCheckTransactionsResponse.observeForever(observer)

        debiCheckMockService.fetchDebiCheckTransactions(accountNumber, debiCheckResponseListener)
        verify(debiCheckMockService).fetchDebiCheckTransactions(any(), capture(debiCheckTransactionsCaptor))
        debiCheckViewModelSpy.debiCheckTransactionsResponse.value = DebiCheckTransactionsResponse()

        Assert.assertEquals(debiCheckTransactionsCaptor.value, debiCheckResponseListener)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckTransactionsResponse.value)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnDisputeDebiCheckTransactionWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckResponseListener = DebiCheckDisputeTransactionExtendedResponseListener(testSubject)
        val debiCheckResponse = DebiCheckDisputeResponse()
        val paymentKey = "DFGGHGF565"
        val successMessage = "Success"

        Mockito.`when`(debiCheckMockService.disputeDebiCheckTransaction(paymentKey, debiCheckResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebiCheckDisputeTransactionExtendedResponseListener>(1).onSuccess(debiCheckResponse.apply {
                debiCheckTransaction = DebiCheckTransaction()
                transactionMessage = successMessage
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<DebiCheckDisputeResponse>>()
        debiCheckViewModelSpy.debiCheckDisputeTransactionResponse.observeForever(observer)

        debiCheckMockService.disputeDebiCheckTransaction(paymentKey, debiCheckResponseListener)
        verify(debiCheckMockService).disputeDebiCheckTransaction(any(), capture(debiCheckDisputeTransactionCaptor))

        Assert.assertEquals(debiCheckDisputeTransactionCaptor.value, debiCheckResponseListener)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckDisputeTransactionResponse.value)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckDisputeTransactionResponse.value?.debiCheckTransaction)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckDisputeTransactionResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckDisputeTransactionResponse.value?.transactionStatus, successMessage)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnIsTransactionDisputableWhenServiceCallComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckTransactionDisputableExtendedResponseListener = DebiCheckTransactionDisputableExtendedResponseListener(testSubject)
        val disputeReason = "Not authorized"
        val debiCheckDisputeTransaction = DebiCheckTransaction()
        val successMessage = "Success"

        Mockito.`when`(debiCheckMockService.isTransactionDisputable(debiCheckDisputeTransaction, disputeReason, debiCheckTransactionDisputableExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebiCheckTransactionDisputableExtendedResponseListener>(2).onSuccess(TransactionDisputableResponse().apply {
                transactionDisputableDetail = TransactionDisputableResponseDetail()
                transactionMessage = successMessage
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<TransactionDisputableResponse>>()
        debiCheckViewModelSpy.debiCheckTransactionDisputableResponse.observeForever(observer)

        debiCheckMockService.isTransactionDisputable(debiCheckDisputeTransaction, disputeReason, debiCheckTransactionDisputableExtendedResponseListener)
        verify(debiCheckMockService).isTransactionDisputable(any(), any(), capture(debiCheckTransactionDisputableCaptor))

        Assert.assertEquals(debiCheckTransactionDisputableCaptor.value, debiCheckTransactionDisputableExtendedResponseListener)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckTransactionDisputableResponse.value)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckTransactionDisputableResponse.value?.transactionDisputableDetail)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckTransactionDisputableResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckTransactionDisputableResponse.value?.transactionStatus, successMessage)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldReturnSuspendMandateWhenServiceComplete() {
        val debiCheckViewModelSpy = spy(testSubject)
        val debiCheckSuspendMandateListener = DebiCheckSuspendMandateExtendedResponseListener(testSubject)
        val mandateReference = "RTDF5656CFGHG788"
        val suspendReasonCode = "5624"
        val successMessage = "Success"

        Mockito.`when`(debiCheckMockService.suspendMandate(mandateReference, suspendReasonCode, debiCheckSuspendMandateListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebiCheckSuspendMandateExtendedResponseListener>(2).onSuccess(DebiCheckSuspendResponse().apply {
                responseDTO = DebitOrderResponseDTO()
                transactionMessage = successMessage
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<DebiCheckSuspendResponse>>()
        debiCheckViewModelSpy.debiCheckSuspendResponse.observeForever(observer)

        debiCheckMockService.suspendMandate(mandateReference, suspendReasonCode, debiCheckSuspendMandateListener)
        verify(debiCheckMockService).suspendMandate(any(), any(), capture(debiCheckSuspendMandateCaptor))

        Assert.assertEquals(debiCheckSuspendMandateCaptor.value, debiCheckSuspendMandateListener)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckSuspendResponse.value)
        Assert.assertNotNull(debiCheckViewModelSpy.debiCheckSuspendResponse.value?.responseDTO)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckSuspendResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(debiCheckViewModelSpy.debiCheckSuspendResponse.value?.transactionStatus, successMessage)

        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorAcceptDebitOrderWhenApproveDebitOrderFunctionIsCalled() {
        val firstCall = true

        testSubject.approveDebitOrder(firstCall)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebitOrderAuthoriseResponse>>(testSubject, "debitOrderApprovalResponseListener")
        verify(debiCheckMockService).setFirstCall(firstCall)
        verify(debiCheckMockService).acceptDebitOrder(debitOrderDetails.mandateRequestTransactionId, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorRejectDebitOrderWhenRejectDebitOrderFunctionIsCalled() {
        val firstCall = true

        testSubject.rejectDebitOrder(firstCall)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebitOrderAuthoriseResponse>>(testSubject, "debitOrderRejectionResponseListener")
        verify(debiCheckMockService).setFirstCall(firstCall)
        verify(debiCheckMockService).rejectDebitOrder(debitOrderDetails.mandateRequestTransactionId, "", reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorFetchPendingMandatesWhenFetchPendingDebitOrdersFunctionIsCalled() {
        testSubject.fetchPendingDebitOrders()
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<MandateResponse>>(testSubject, "pendingMandateResponseListener")
        verify(debiCheckMockService).fetchPendingMandates(reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorFetchMandatesWhenFetchMandatesFunctionIsCalled() {
        val accountNumber = "6543785553"
        val mandateStatus = "Active"
        val mandateReference = "RTDF5656CFGHG788"

        testSubject.fetchMandates(accountNumber, mandateStatus, mandateReference)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<MandateResponse>>(testSubject, "mandateResponseListener")
        verify(debiCheckMockService).fetchMandates(accountNumber, mandateStatus, mandateReference, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorFetchDebiCheckTransactionsWhenFetchDebiCheckTransactionsFunctionIsCalled() {
        val accountNumber = "6543785553"

        testSubject.fetchDebiCheckTransactions(accountNumber)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebiCheckTransactionsResponse>>(testSubject, "debiCheckTransactionsResponseListener")
        verify(debiCheckMockService).fetchDebiCheckTransactions(accountNumber, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorDisputeDebiCheckTransactionWhendisputeTransactionFunctionIsCalled() {
        val paymentKey = "DFGGHGF565"

        testSubject.disputeTransaction(paymentKey)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebiCheckDisputeResponse>>(testSubject, "debiCheckDisputeTransactionResponseListener")
        verify(debiCheckMockService).disputeDebiCheckTransaction(paymentKey, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorIsTransactionDisputableWhenCheckIfTransactionDisputableFunctionIsCalled() {
        val disputeReason = "Not authorized"
        val debiCheckDisputeTransaction = DebiCheckTransaction()

        testSubject.checkIfTransactionDisputable(debiCheckDisputeTransaction, disputeReason)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<TransactionDisputableResponse>>(testSubject, "debiCheckTransactionDisputableResponseListener")
        verify(debiCheckMockService).isTransactionDisputable(debiCheckDisputeTransaction, disputeReason, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }

    @Test
    fun shouldCallInteractorSuspendMandateWhenSuspendMandateFunctionIsCalled() {
        val selectedDebitOrder = DebiCheckMandateDetail()

        testSubject.suspendMandate()
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebiCheckSuspendResponse>>(testSubject, "debiCheckSuspendMandateResponseListener")
        verify(debiCheckMockService).suspendMandate(selectedDebitOrder.mandateReferenceNumber, "", reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(debiCheckMockService)
    }
}