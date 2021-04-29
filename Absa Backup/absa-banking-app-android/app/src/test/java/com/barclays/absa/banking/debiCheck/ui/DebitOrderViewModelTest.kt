/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.debiCheck.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.capture
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.debiCheck.responseListeners.*
import com.barclays.absa.banking.debiCheck.services.DebitOrderService
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

internal class DebitOrderViewModelTest {

    private var debitOrderViewModel = DebitOrderViewModel()
    private var debitOrderService = mock<DebitOrderService>()
    private lateinit var debitOrderDataModel: DebitOrderDataModel

    @Captor
    private lateinit var debitOrderCaptor: ArgumentCaptor<DebitOrdersExtendedResponseListener>

    @Captor
    private lateinit var debitOrderStoppedPaymentCaptor: ArgumentCaptor<DebitOrderStoppedPaymentExtendedResponseListener>

    @Captor
    private lateinit var reverseDebitOrderPaymentCaptor: ArgumentCaptor<DebitOrderReversePaymentExtendedResponseListener>

    @Captor
    private lateinit var stopDebitOrderCaptor: ArgumentCaptor<StopDebitOrderExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        debitOrderViewModel.debitOrderService = debitOrderService
        debitOrderDataModel = DebitOrderDataModel()
    }

    @Test
    fun shouldCallInteractorFetchDebitOrdersWhenFetchDebitOrderTransactionListMethodIsCalled() {
        debitOrderViewModel.fetchDebitOrderTransactionList()
        val reflectedDebitOrderListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebitOrderList>>(debitOrderViewModel, "debitOrderExtendedResponseListener")
        verify(debitOrderService).fetchDebitOrders(debitOrderViewModel.debitOrderDataModel, reflectedDebitOrderListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldCallInteractorFetchStoppedDebitOrdersWhenFetchStoppedDebitOrderListMethodIsCalled() {
        debitOrderViewModel.fetchStoppedDebitOrderList(debitOrderViewModel.debitOrderDataModel)
        val reflectedStoppedDebitOrderListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<DebitOrderList>>(debitOrderViewModel, "debitOrderStoppedPaymentExtendedResponseListener")
        verify(debitOrderService).fetchStoppedDebitOrders(debitOrderViewModel.debitOrderDataModel, reflectedStoppedDebitOrderListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldCallInteractorReverseDebitOrderPaymentWhenReverseDebitOrderPaymentMethodIsCalled() {
        debitOrderViewModel.reverseDebitOrderPayment(debitOrderViewModel.debitOrderDataModel)
        val reflectedReverseDebitOrderPaymentListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<SureCheckResponse>>(debitOrderViewModel, "reverseDebitOrderPaymentExtendedResponseListener")
        verify(debitOrderService).reverseDebitOrderPayment(debitOrderViewModel.debitOrderDataModel, reflectedReverseDebitOrderPaymentListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldCallInteractorStopDebitOrderPaymentWhenStopDebitOrderMethodIsCalled() {
        debitOrderViewModel.stopDebitOrder(debitOrderViewModel.debitOrderDataModel)
        val reflectedStopDebitOrderListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<TransactionResponse>>(debitOrderViewModel, "stopDebitOrderExtendedResponseListener")
        verify(debitOrderService).stopDebitOrderPayment(debitOrderViewModel.debitOrderDataModel, reflectedStopDebitOrderListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldFetchDebitOrderTransactionListWhenServiceCallIsComplete() {
        val debitOrderSpy = spy(debitOrderViewModel)
        val debitOrdersExtendedResponseListener = DebitOrdersExtendedResponseListener(debitOrderViewModel)
        val debitOrderResponse = DebitOrderList()
        val debitOrderDetails = DebitOrderDetailsResponse()

        Mockito.`when`(debitOrderService.fetchDebitOrders(debitOrderDataModel, debitOrdersExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitOrdersExtendedResponseListener>(1).onSuccess(debitOrderResponse.apply {
                debitOrders = listOf(debitOrderDetails)
            })
        }

        val observer = mock<Observer<DebitOrderList>>()
        debitOrderSpy.debitOrdersList.observeForever(observer)

        debitOrderService.fetchDebitOrders(debitOrderDataModel, debitOrdersExtendedResponseListener)
        verify(debitOrderService).fetchDebitOrders(any(), capture(debitOrderCaptor))

        Assert.assertEquals(debitOrderCaptor.value, debitOrdersExtendedResponseListener)
        Assert.assertNotNull(debitOrderSpy.debitOrdersList.value)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldFetchStoppedDebitOrderListWhenServiceCallIsComplete() {
        val debitOrderSpy = spy(debitOrderViewModel)
        val debitOrderStoppedPaymentResponseListener = DebitOrderStoppedPaymentExtendedResponseListener(debitOrderViewModel)
        val debitOrderResponse = DebitOrderList()
        val debitOrderDetails = DebitOrderDetailsResponse()

        Mockito.`when`(debitOrderService.fetchStoppedDebitOrders(debitOrderDataModel, debitOrderStoppedPaymentResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitOrderStoppedPaymentExtendedResponseListener>(1).onSuccess(debitOrderResponse.apply {
                stopPayments = listOf(debitOrderDetails)
            })
        }

        val observer = mock<Observer<DebitOrderList>>()
        debitOrderSpy.stoppedDebitOrderList.observeForever(observer)

        debitOrderService.fetchStoppedDebitOrders(debitOrderDataModel, debitOrderStoppedPaymentResponseListener)
        verify(debitOrderService).fetchStoppedDebitOrders(any(), capture(debitOrderStoppedPaymentCaptor))

        Assert.assertEquals(debitOrderStoppedPaymentCaptor.value, debitOrderStoppedPaymentResponseListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldReverseDebitOrderPaymentWhenServiceCallIsComplete() {
        val debitOrderSpy = spy(debitOrderViewModel)
        val debitOrderReversePaymentResponseListener = DebitOrderReversePaymentExtendedResponseListener(debitOrderViewModel)
        val debitOrderResponse = SureCheckResponse()

        Mockito.`when`(debitOrderService.reverseDebitOrderPayment(debitOrderDataModel, debitOrderReversePaymentResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<DebitOrderReversePaymentExtendedResponseListener>(1).onSuccess(debitOrderResponse)
        }

        val observer = mock<Observer<SureCheckResponse>>()
        debitOrderSpy.reverseDebitOrderSureCheckResponse.observeForever(observer)

        debitOrderService.reverseDebitOrderPayment(debitOrderViewModel.debitOrderDataModel, debitOrderReversePaymentResponseListener)
        verify(debitOrderService).reverseDebitOrderPayment(any(), capture(reverseDebitOrderPaymentCaptor))

        Assert.assertEquals(reverseDebitOrderPaymentCaptor.value, debitOrderReversePaymentResponseListener)
        verifyNoMoreInteractions(debitOrderService)
    }

    @Test
    fun shouldStopDebitOrderWhenServiceCallIsComplete() {
        val debitOrderSpy = spy(debitOrderViewModel)
        val stopDebitOrderResponseListener = StopDebitOrderExtendedResponseListener(debitOrderViewModel)
        val debitOrderResponse = TransactionResponse()

        Mockito.`when`(debitOrderService.stopDebitOrderPayment(debitOrderDataModel, stopDebitOrderResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<StopDebitOrderExtendedResponseListener>(1).onSuccess(debitOrderResponse)
        }

        val observer = mock<Observer<TransactionResponse>>()
        debitOrderSpy.stopDebitOrderResponse.observeForever(observer)

        debitOrderService.stopDebitOrderPayment(debitOrderDataModel, stopDebitOrderResponseListener)
        verify(debitOrderService).stopDebitOrderPayment(any(), capture(stopDebitOrderCaptor))

        Assert.assertEquals(stopDebitOrderCaptor.value, stopDebitOrderResponseListener)
        verifyNoMoreInteractions(debitOrderService)
    }
}