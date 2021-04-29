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
package com.barclays.absa.banking.lotto.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.lotto.services.*
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.banking.lotto.services.dto.LottoGameRulesResponse
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class LottoViewModelTest : DaggerTest() {
    val testSubject = LottoViewModel()
    private val lottoMockService = mock<LottoInteractor>()

    @Captor
    private lateinit var lottoTermsAcceptanceCaptor: ArgumentCaptor<LottoTermsAcceptanceResponseListener>

    @Captor
    private lateinit var lottoServiceAvailabilityCaptor: ArgumentCaptor<LottoServiceAvailabilityResponseListener>

    @Captor
    private lateinit var lottoAcceptTermsCaptor: ArgumentCaptor<LottoAcceptTermsResponseListener>

    @Captor
    private lateinit var lottoFetchGameRulesCaptor: ArgumentCaptor<LottoFetchGameRulesResponseListener>

    @Captor
    private lateinit var lottoTicketHistoryCaptor: ArgumentCaptor<LottoTicketHistoryResponseListener>

    @Captor
    private lateinit var lottoDrawResultsCaptor: ArgumentCaptor<LottoDrawResultsResponseListener>

    @Captor
    private lateinit var lottoTicketPurchaseCaptor: ArgumentCaptor<LottoTicketPurchaseResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.lottoService = lottoMockService
    }

    @Test
    fun shouldCallCheckLottoTermsAcceptanceInteractorWhenLoadTermsAcceptanceDataFunctionIsCalled() {
        testSubject.loadTermsAcceptanceData()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoTermsAcceptanceResponseListener>(testSubject, "termsAcceptanceResponseListener")
        verify(lottoMockService).checkLottoTermsAcceptance(extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallAcceptLottoTermsAndConditionsInteractorWhenAcceptLottoTermsFunctionIsCalled() {
        testSubject.acceptLottoTerms()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoAcceptTermsResponseListener>(testSubject, "lottoAcceptTermsResponseListener")
        verify(lottoMockService).acceptLottoTermsAndConditions(extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallCheckLottoServiceAvailabilityInteractorWhenCheckServiceAvailabilityFunctionIsCalled() {
        testSubject.checkServiceAvailability("")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoServiceAvailabilityResponseListener>(testSubject, "lottoServiceAvailabilityResponseListener")
        verify(lottoMockService).checkLottoServiceAvailability("", extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallFetchLottoGameRulesInteractorWhenFetchGameRulesFunctionIsCalled() {
        testSubject.fetchGameRules()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoFetchGameRulesResponseListener>(testSubject, "lottoFetchGameRulesResponseListener")
        verify(lottoMockService).fetchLottoGameRules(extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallFetchTicketHistoryInteractorWhenFetchTicketHistoryFunctionIsCalled() {
        testSubject.fetchTicketHistory("", "")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoTicketHistoryResponseListener>(testSubject, "lottoTicketHistoryResponseListener")
        verify(lottoMockService).fetchTicketHistory("", "", extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallFetchDrawResultsInteractorWhenFetchLottoDrawResultsFunctionIsCalled() {
        testSubject.fetchLottoDrawResults("", "", "")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoDrawResultsResponseListener>(testSubject, "lottoDrawResultsResponseListener")
        verify(lottoMockService).fetchDrawResults("", "", "", extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallFetchDrawResultsInteractorWhenFetchLastLottoDrawResultsRangeFunctionIsCalled() {
        testSubject.fetchLastLottoDrawResultsRange("")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoDrawResultsResponseListener>(testSubject, "lottoDrawResultsResponseListener")
        verify(lottoMockService).fetchDrawResults("", "", "", extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldCallPurchaseLottoTicketsInteractorWhenPurchaseLottoTicketFunctionIsCalled() {
        testSubject.purchaseLottoTicket()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<LottoTicketPurchaseResponseListener>(testSubject, "lottoTicketPurchaseResponseListener")
        verify(lottoMockService).purchaseLottoTickets(testSubject.purchaseData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnFetchUserProfileDetailsWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoTermsAcceptanceResponseListener(testSubject)
        val lottoResponse = TermsAcceptanceResponse()

        Mockito.`when`(lottoMockService.checkLottoTermsAcceptance(lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoTermsAcceptanceResponseListener>(0).onSuccess(lottoResponse.apply {
                lottoTermsAcceptedInd = "Y"
            })
        }

        val observer = mock<Observer<Boolean>>()
        lottoViewModelSpy.termsAcceptanceStateLiveData.observeForever(observer)

        lottoMockService.checkLottoTermsAcceptance(lottoResponseListener)
        verify(lottoMockService).checkLottoTermsAcceptance(capture(lottoTermsAcceptanceCaptor))

        Assert.assertEquals(lottoTermsAcceptanceCaptor.value, lottoResponseListener)
        Assert.assertNotNull(lottoViewModelSpy.termsAcceptanceStateLiveData.value)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnCheckLottoServiceAvailabilityWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoServiceAvailabilityResponseListener(testSubject)
        val lottoResponse = LottoServiceAvailabilityResponse()

        Mockito.`when`(lottoMockService.checkLottoServiceAvailability("", lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoServiceAvailabilityResponseListener>(1).onSuccess(lottoResponse.apply {
                lottoGameAvailable = ""
                nextStartDate = ""
            })
        }

        val observer = mock<Observer<LottoServiceAvailabilityResponse>>()
        lottoViewModelSpy.serviceAvailabilityLiveData.observeForever(observer)

        lottoMockService.checkLottoServiceAvailability("", lottoResponseListener)
        verify(lottoMockService).checkLottoServiceAvailability(any(), capture(lottoServiceAvailabilityCaptor))

        Assert.assertEquals(lottoServiceAvailabilityCaptor.value, lottoResponseListener)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnAcceptLottoTermsAndConditionsWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoAcceptTermsResponseListener(testSubject)
        val lottoResponse = TransactionResponse()
        val successMessage = "Success"

        Mockito.`when`(lottoMockService.acceptLottoTermsAndConditions(lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoAcceptTermsResponseListener>(0).onSuccess(lottoResponse.apply {
                transactionMessage = successMessage
                transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<Boolean>>()
        lottoViewModelSpy.termsAcceptedLiveData.observeForever(observer)

        lottoMockService.acceptLottoTermsAndConditions(lottoResponseListener)
        verify(lottoMockService).acceptLottoTermsAndConditions(capture(lottoAcceptTermsCaptor))

        Assert.assertEquals(lottoAcceptTermsCaptor.value, lottoResponseListener)
        Assert.assertNotNull(lottoViewModelSpy.termsAcceptedLiveData.value)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnFetchLottoGameRulesWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoFetchGameRulesResponseListener(testSubject)
        val lottoResponse = LottoGameRulesResponse()

        Mockito.`when`(lottoMockService.fetchLottoGameRules(lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoFetchGameRulesResponseListener>(0).onSuccess(lottoResponse.apply {
                this.lottoGameRules = mutableListOf(LottoGameRules())
            })
        }

        val observer = mock<Observer<MutableList<LottoGameRules>>>()
        lottoViewModelSpy.lottoGameRulesListLiveData.observeForever(observer)

        lottoMockService.fetchLottoGameRules(lottoResponseListener)
        verify(lottoMockService).fetchLottoGameRules(capture(lottoFetchGameRulesCaptor))

        Assert.assertEquals(lottoFetchGameRulesCaptor.value, lottoResponseListener)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnFetchTicketHistoryWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoTicketHistoryResponseListener(testSubject)
        val lottoResponse = LottoTicketHistoryResponse()

        Mockito.`when`(lottoMockService.fetchTicketHistory("", "", lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoTicketHistoryResponseListener>(2).onSuccess(lottoResponse.apply {
                lottoTicketHistory = mutableListOf(LottoTicketHistory())
            })
        }

        val observer = mock<Observer<MutableList<LottoTicketHistory>>>()
        lottoViewModelSpy.lottoTicketHistoryLiveData.observeForever(observer)

        lottoMockService.fetchTicketHistory("", "", lottoResponseListener)
        verify(lottoMockService).fetchTicketHistory(any(), any(), capture(lottoTicketHistoryCaptor))

        Assert.assertEquals(lottoTicketHistoryCaptor.value, lottoResponseListener)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnFetchDrawResultsWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoDrawResultsResponseListener(testSubject)
        val lottoResponse = LottoDrawResultsResponse()

        Mockito.`when`(lottoMockService.fetchDrawResults("", "", "", lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoDrawResultsResponseListener>(3).onSuccess(lottoResponse.apply {
                this.lottoDrawResults = mutableListOf(LottoDrawResult())
            })
        }

        val observer = mock<Observer<LottoDrawResultsResponse>>()
        lottoViewModelSpy.lottoDrawResultsLiveData.observeForever(observer)

        lottoMockService.fetchDrawResults("", "", "", lottoResponseListener)
        verify(lottoMockService).fetchDrawResults(any(), any(), any(), capture(lottoDrawResultsCaptor))

        Assert.assertEquals(lottoDrawResultsCaptor.value, lottoResponseListener)
        verifyNoMoreInteractions(lottoMockService)
    }

    @Test
    fun shouldReturnPurchaseLottoTicketsWhenServiceCallComplete() {
        val lottoViewModelSpy = spy(testSubject)
        val lottoResponseListener = LottoTicketPurchaseResponseListener(testSubject)
        val lottoResponse = LottoTicketPurchaseResponse()

        Mockito.`when`(lottoMockService.purchaseLottoTickets(testSubject.purchaseData, lottoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<LottoTicketPurchaseResponseListener>(1).onSuccess(lottoResponse.apply {
                this.lottoBoards = mutableListOf(LottoBoardNumbers())
                this.lottoGameAvailable = ""
                this.lottoLimitErrorType = "Y"
            })
        }

        val observer = mock<Observer<LottoTicketPurchaseResponse>>()
        lottoViewModelSpy.lottoPurchaseResponseLiveData.observeForever(observer)

        lottoMockService.purchaseLottoTickets(testSubject.purchaseData, lottoResponseListener)
        verify(lottoMockService).purchaseLottoTickets(any(), capture(lottoTicketPurchaseCaptor))

        Assert.assertEquals(lottoTicketPurchaseCaptor.value, lottoResponseListener)
        Assert.assertNotNull(lottoViewModelSpy.lottoPurchaseResponseLiveData.value)
        Assert.assertNotNull(lottoViewModelSpy.lottoPurchaseResponseLiveData.value?.lottoBoards)
        verifyNoMoreInteractions(lottoMockService)
    }
}