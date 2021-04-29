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
package com.barclays.absa.banking.ultimateProtector.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverInteractor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import androidx.lifecycle.Observer
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.ultimateProtector.services.dto.*
import com.nhaarman.mockito_kotlin.*
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.junit.Assert

internal class UltimateProtectorViewModelTest {
    val testSubject = UltimateProtectorViewModel()
    private val lifeCoverMockService = mock<LifeCoverInteractor>()

    @Captor
    private lateinit var quotationCaptor: ArgumentCaptor<QuotationExtendedResponseListener>

    @Captor
    private lateinit var retailAccountsCaptor: ArgumentCaptor<RetailAccountsExtendedResponseListener>

    @Captor
    private lateinit var lifeCoverApplicationCaptor: ArgumentCaptor<LifeCoverApplicationExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.lifeCoverService = lifeCoverMockService
    }

    @Test
    fun shouldCallFetchLifeCoverQuotationInteractorWhenGetCoverQuotationFunctionIsCalled() {
        testSubject.getCoverQuotation()
        verify(lifeCoverMockService).fetchLifeCoverQuotation(any(), any())
    }

    @Test
    fun shouldReturnFetchUserProfileDetailsWhenServiceCallComplete() {
        val ultimateProtectorViewModelSpy = spy(testSubject)
        val ultimateProtectorViewModelResponseListener = QuotationExtendedResponseListener(testSubject.coverQuotationLiveData)
        val ultimateProtectorResponse = Quotation()
        val benefitCode = testSubject.getBenefitCode()

        Mockito.`when`(testSubject.lifeCoverService.fetchLifeCoverQuotation(benefitCode, ultimateProtectorViewModelResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<QuotationExtendedResponseListener>(1).onSuccess(ultimateProtectorResponse.apply {
                findOutMorePdfUrl = ""
                termsAndConditionsPdfUrl = ""
                benefitPdfUrl = ""
                sliderPremiums = listOf(Premium())
                benefitQualifyText = ""
                benefitCoverText1 = ""
                benefitCoverText2 = ""
            })
        }

        val observer = mock<Observer<Quotation>>()
        ultimateProtectorViewModelSpy.coverQuotationLiveData.observeForever(observer)

        ultimateProtectorViewModelSpy.lifeCoverService.fetchLifeCoverQuotation(benefitCode, ultimateProtectorViewModelResponseListener)
        verify(ultimateProtectorViewModelSpy.lifeCoverService).fetchLifeCoverQuotation(any(), capture(quotationCaptor))

        Assert.assertEquals(quotationCaptor.value, ultimateProtectorViewModelResponseListener)
        Assert.assertNotNull(ultimateProtectorViewModelSpy.coverQuotationLiveData.value)
        verifyNoMoreInteractions(ultimateProtectorViewModelSpy.lifeCoverService)
    }

    @Test
    fun shouldCallFetchRetailAccountInteractorWhenLoadRetailAccountsFunctionIsCalled() {
        testSubject.loadRetailAccounts()
        verify(lifeCoverMockService).fetchRetailAccount(any())
    }

    @Test
    fun shouldReturnFetchRetailAccountWhenServiceCallComplete() {
        val ultimateProtectorViewModelSpy = spy(testSubject)
        val ultimateProtectorViewModelResponseListener = RetailAccountsExtendedResponseListener(testSubject.retailAccountsLiveData)
        val ultimateProtectorResponseResponse = RetailAccountsResponse()

        Mockito.`when`(testSubject.lifeCoverService.fetchRetailAccount(ultimateProtectorViewModelResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RetailAccountsExtendedResponseListener>(0).onSuccess(ultimateProtectorResponseResponse.apply {
                retailAccountsList = arrayListOf(RetailAccount())
            })
        }

        val observer = mock<Observer<List<RetailAccount>>>()
        testSubject.retailAccountsLiveData.observeForever(observer)

        ultimateProtectorViewModelSpy.lifeCoverService.fetchRetailAccount(ultimateProtectorViewModelResponseListener)
        verify(ultimateProtectorViewModelSpy.lifeCoverService).fetchRetailAccount(capture(retailAccountsCaptor))

        Assert.assertEquals(retailAccountsCaptor.value, ultimateProtectorViewModelResponseListener)
        Assert.assertNotNull(ultimateProtectorViewModelSpy.retailAccountsLiveData.value)
        verifyNoMoreInteractions(ultimateProtectorViewModelSpy.lifeCoverService)
    }

    @Test
    fun shouldCallApplyForLifeCoverInteractorWhenApplyForLifeCoverFunctionIsCalled() {
        testSubject.applyForLifeCover(CallType.SURECHECKV2_PASSED)
        verify(lifeCoverMockService).applyForLifeCover(any(), any(), any())
    }

    @Test
    fun shouldReturnApplyForLifeCoverWhenServiceCallComplete() {
        val ultimateProtectorViewModelSpy = spy(testSubject)
        val ultimateProtectorViewModelResponseListener = LifeCoverApplicationExtendedResponseListener(testSubject.lifeCoverApplicationLiveData)
        val ultimateProtectorResponseResponse = LifeCoverApplicationResult()
        val ultimateProtectorInfo = LifeCoverInfo()

        Mockito.`when`(testSubject.lifeCoverService.applyForLifeCover(ultimateProtectorInfo, ultimateProtectorViewModelResponseListener, CallType.SURECHECKV2_PASSED)).thenAnswer {
            return@thenAnswer it.getArgument<LifeCoverApplicationExtendedResponseListener>(1).onSuccess(ultimateProtectorResponseResponse.apply {
                policyStartDate = ""
                policyNumber = ""
            })
        }

        val observer = mock<Observer<LifeCoverApplicationResult>>()
        ultimateProtectorViewModelSpy.lifeCoverApplicationLiveData.observeForever(observer)

        ultimateProtectorViewModelSpy.lifeCoverService.applyForLifeCover(ultimateProtectorInfo, ultimateProtectorViewModelResponseListener, CallType.SURECHECKV2_PASSED)
        verify(ultimateProtectorViewModelSpy.lifeCoverService).applyForLifeCover(any(), capture(lifeCoverApplicationCaptor), any())

        Assert.assertEquals(lifeCoverApplicationCaptor.value, ultimateProtectorViewModelResponseListener)
        Assert.assertNotNull(ultimateProtectorViewModelSpy.lifeCoverApplicationLiveData.value)
        verifyNoMoreInteractions(ultimateProtectorViewModelSpy.lifeCoverService)
    }
}