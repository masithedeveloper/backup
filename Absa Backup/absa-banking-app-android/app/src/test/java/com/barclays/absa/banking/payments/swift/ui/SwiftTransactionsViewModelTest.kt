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
 *
 */

package com.barclays.absa.banking.payments.swift.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.account.services.dto.LinkedAndUnlinkedAccounts
import com.barclays.absa.banking.account.services.dto.ManageAccounts
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeObject
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse
import com.barclays.absa.banking.payments.swift.services.SwiftInteractor
import com.barclays.absa.banking.payments.swift.services.response.*
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionClaimed
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionHistoryResponse
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionPending
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionsListResponse
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class SwiftTransactionsViewModelTest : DaggerTest() {
    private var testSubject = SwiftTransactionsViewModel()
    private var swiftMockService = mock<SwiftInteractor>()
    private var accountMockService = mock<AccountInteractor>()

    @Captor
    private lateinit var fetchTransactionsListCaptor: ArgumentCaptor<SwiftTransactionsListResponseListener>

    @Captor
    private lateinit var fetchTransactionDetailsCaptor: ArgumentCaptor<SwiftTransactionDetailsResponseListener>

    @Captor
    private lateinit var fetchTransactionHistoryCaptor: ArgumentCaptor<SwiftTransactionHistoryResponseListener>

    @Captor
    private lateinit var validateForHolidaysAndTimeCaptor: ArgumentCaptor<SwiftValidateForHolidaysAndTimeResponseListener>

    @Captor
    private lateinit var fetchAccessAccountCaptor: ArgumentCaptor<SwiftLinkedAndUnlinkedAccountsResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.swiftInteractor = swiftMockService
        testSubject.accountInteractor = accountMockService
    }

    @Test
    fun shouldCallRequestTransactionsListSwiftInteractorWhenFetchTransactionsListFunctionIsCalled() {
        testSubject.fetchTransactionsList()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<SwiftTransactionsListResponseListener>(testSubject, "swiftTransactionsListResponseListener")
        verify(swiftMockService).requestTransactionsList(extendedResponseListenerReflection)
        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldCallRequestTransactionsListSwiftInteractorWhenFetchTransactionDetailsFunctionIsCalled() {
        val caseId = "I81753D4C53C"

        testSubject.fetchTransactionDetails(caseId)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<SwiftTransactionDetailsResponseListener>(testSubject, "swiftTransactionDetailsResponseListener")
        verify(swiftMockService).requestTransactionsList(extendedResponseListenerReflection, caseId)
        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldCallRequestTransactionHistorySwiftInteractorWhenFetchTransactionHistoryFunctionIsCalled() {
        val accessAccountNumber = "300233828326"

        testSubject.fetchTransactionHistory(accessAccountNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<SwiftTransactionHistoryResponseListener>(testSubject, "swiftTransactionsHistoryResponseListener")
        verify(swiftMockService).requestTransactionHistory(extendedResponseListenerReflection, accessAccountNumber)
        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldCallValidateForHolidaysAndTimeSwiftInteractorWhenValidateForHolidaysAndTimeFunctionIsCalled() {
        testSubject.validateForHolidaysAndTime()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<SwiftValidateForHolidaysAndTimeResponseListener>(testSubject, "validateForHolidaysAndTimeResponseListener")
        verify(swiftMockService).validateForHolidaysAndTime(extendedResponseListenerReflection)
        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldCallFetchAccountStatsSwiftInteractorWhenFetchAccessAccountFunctionIsCalled() {
        testSubject.fetchAccessAccount()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<SwiftLinkedAndUnlinkedAccountsResponseListener>(testSubject, "fetchLinkedAndUnlinkedAccountsExtendedResponseListener")
        verify(accountMockService).fetchAccountStats(extendedResponseListenerReflection)
        verifyNoMoreInteractions(accountMockService)
    }

    @Test
    fun shouldReturnTransactionsListWhenServiceCallComplete() {
        val swiftTransactionsViewModelSpy = spy(testSubject)
        val swiftTransactionsListResponseListener = SwiftTransactionsListResponseListener(testSubject)
        val swiftTransactionsListResponse = SwiftTransactionsListResponse()
        val swiftTransactions = listOf<SwiftTransactionPending>()

        Mockito.`when`(swiftMockService.requestTransactionsList(swiftTransactionsListResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SwiftTransactionsListResponseListener>(0).onSuccess(swiftTransactionsListResponse.apply {
                this.swiftTransactions = swiftTransactions
            })
        }

        val observer = mock<Observer<SwiftTransactionsListResponse>>()
        swiftTransactionsViewModelSpy.swiftTransactionsListMutableLiveData.observeForever(observer)

        swiftMockService.requestTransactionsList(swiftTransactionsListResponseListener)
        verify(swiftMockService).requestTransactionsList(capture(fetchTransactionsListCaptor), any())

        Assert.assertEquals(swiftTransactionsListResponseListener, fetchTransactionsListCaptor.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionsListMutableLiveData.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionsListMutableLiveData.value?.swiftTransactions)

        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldReturnTransactionDetailsWhenServiceCallComplete() {
        val swiftTransactionsViewModelSpy = spy(testSubject)
        val swiftTransactionDetailsResponseListener = SwiftTransactionDetailsResponseListener(testSubject)
        val swiftTransactionDetailsResponse = SwiftTransactionsListResponse()
        val caseId = "I81753D4C53C"
        val swiftTransactions = listOf<SwiftTransactionPending>()

        Mockito.`when`(swiftMockService.requestTransactionsList(swiftTransactionDetailsResponseListener, caseId)).thenAnswer {
            return@thenAnswer it.getArgument<SwiftTransactionDetailsResponseListener>(0).onSuccess(swiftTransactionDetailsResponse.apply {
                this.swiftTransactions = swiftTransactions
            })
        }

        val observer = mock<Observer<SwiftTransactionsListResponse>>()
        swiftTransactionsViewModelSpy.swiftTransactionDetailsMutableLiveData.observeForever(observer)

        swiftMockService.requestTransactionsList(swiftTransactionDetailsResponseListener, caseId)
        verify(swiftMockService).requestTransactionsList(capture(fetchTransactionDetailsCaptor), any())

        Assert.assertEquals(swiftTransactionDetailsResponseListener, fetchTransactionDetailsCaptor.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionDetailsMutableLiveData.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionDetailsMutableLiveData.value?.swiftTransactions)

        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldReturnTransactionHistoryWhenServiceCallComplete() {
        val swiftTransactionsViewModelSpy = spy(testSubject)
        val swiftTransactionsHistoryResponseListener = SwiftTransactionHistoryResponseListener(testSubject)
        val swiftTransactionHistoryResponse = SwiftTransactionHistoryResponse()
        val accessAccountNumber = "300233828326"
        val receiptsIFTHistoryLine = listOf<SwiftTransactionClaimed>()

        Mockito.`when`(swiftMockService.requestTransactionHistory(swiftTransactionsHistoryResponseListener, accessAccountNumber)).thenAnswer {
            return@thenAnswer it.getArgument<SwiftTransactionHistoryResponseListener>(0).onSuccess(swiftTransactionHistoryResponse.apply {
                this.receiptsIFTHistoryLine = receiptsIFTHistoryLine
            })
        }

        val observer = mock<Observer<SwiftTransactionHistoryResponse>>()
        swiftTransactionsViewModelSpy.swiftTransactionHistoryMutableLiveData.observeForever(observer)

        swiftMockService.requestTransactionHistory(swiftTransactionsHistoryResponseListener, accessAccountNumber)
        verify(swiftMockService).requestTransactionHistory(capture(fetchTransactionHistoryCaptor), any())

        Assert.assertEquals(swiftTransactionsHistoryResponseListener, fetchTransactionHistoryCaptor.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionHistoryMutableLiveData.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftTransactionHistoryMutableLiveData.value?.receiptsIFTHistoryLine)

        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldReturnValidateForHolidaysAndTimeWhenServiceCallComplete() {
        val swiftTransactionsViewModelSpy = spy(testSubject)
        val validateForHolidaysAndTimeResponseListener = SwiftValidateForHolidaysAndTimeResponseListener(testSubject)
        val validateForHolidaysAndTimeResponse = ValidateForHolidaysAndTimeResponse()

        Mockito.`when`(swiftMockService.validateForHolidaysAndTime(validateForHolidaysAndTimeResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SwiftValidateForHolidaysAndTimeResponseListener>(0).onSuccess(validateForHolidaysAndTimeResponse.apply {
                responseDTO = ValidateForHolidaysAndTimeObject()
            })
        }

        val observer = mock<Observer<ValidateForHolidaysAndTimeResponse>>()
        swiftTransactionsViewModelSpy.swiftValidateHolidaysAndTimeMutableLiveData.observeForever(observer)

        swiftMockService.validateForHolidaysAndTime(validateForHolidaysAndTimeResponseListener)
        verify(swiftMockService).validateForHolidaysAndTime(capture(validateForHolidaysAndTimeCaptor))

        Assert.assertEquals(validateForHolidaysAndTimeResponseListener, validateForHolidaysAndTimeCaptor.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftValidateHolidaysAndTimeMutableLiveData.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftValidateHolidaysAndTimeMutableLiveData.value?.responseDTO)

        verifyNoMoreInteractions(swiftMockService)
    }

    @Test
    fun shouldReturnAccessAccountWhenServiceCallComplete() {
        val swiftTransactionsViewModelSpy = spy(testSubject)
        val fetchLinkedAndUnlinkedAccountsExtendedResponseListener = SwiftLinkedAndUnlinkedAccountsResponseListener(testSubject)
        val fetchLinkedAndUnlinkedAccountsResponse = LinkedAndUnlinkedAccounts()
        val accountList = listOf<ManageAccounts>()

        Mockito.`when`(accountMockService.fetchAccountStats(fetchLinkedAndUnlinkedAccountsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SwiftLinkedAndUnlinkedAccountsResponseListener>(0).onSuccess(fetchLinkedAndUnlinkedAccountsResponse.apply {
                this.accountList = accountList
            })
        }

        val observer = mock<Observer<LinkedAndUnlinkedAccounts>>()
        swiftTransactionsViewModelSpy.swiftLinkedAndUnlinkedAccounts.observeForever(observer)

        accountMockService.fetchAccountStats(fetchLinkedAndUnlinkedAccountsExtendedResponseListener)
        verify(accountMockService).fetchAccountStats(capture(fetchAccessAccountCaptor))

        Assert.assertEquals(fetchLinkedAndUnlinkedAccountsExtendedResponseListener, fetchAccessAccountCaptor.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftLinkedAndUnlinkedAccounts.value)
        Assert.assertNotNull(swiftTransactionsViewModelSpy.swiftLinkedAndUnlinkedAccounts.value?.accountList)

        verifyNoMoreInteractions(accountMockService)
    }
}