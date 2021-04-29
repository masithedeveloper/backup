/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.personalLoan.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse
import com.barclays.absa.banking.personalLoan.services.CallBackExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoanFicaCheckExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoanHubExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoansInteractor
import com.barclays.absa.banking.riskBasedApproach.services.dto.FicaStatus
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class PersonalLoanVCLViewModelTest : DaggerTest() {

    private var testSubject = PersonalLoanVCLViewModel()
    private var personalLoanMockService = mock<PersonalLoansInteractor>()
    private var homeScreenMockService = mock<HomeScreenInteractor>()

    @Captor
    private lateinit var fetchPersonalInformationCaptor: ArgumentCaptor<ExtendedResponseListener<PersonalLoanHubInformation>>

    @Captor
    private lateinit var fetchFicaStatusCaptor: ArgumentCaptor<ExtendedResponseListener<FicaStatus>>

    @Captor
    private lateinit var requestCallBackCaptor: ArgumentCaptor<ExtendedResponseListener<CallBackResponse>>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.personalLoanInteractor = personalLoanMockService
        testSubject.homeScreenInteractor = homeScreenMockService
    }

    @Test
    fun shouldCallFetchFicaStatusPersonalLoansInteractorWhenFetchFicaStatusFunctionIsCalled() {
        testSubject.fetchFicaStatus()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FicaStatus>>(testSubject, "ficaCheckExtendedResponseListener")
        verify(personalLoanMockService).fetchFicaStatus(extendedResponseListenerReflection)
        verifyNoMoreInteractions(personalLoanMockService)
    }

    @Test
    fun shouldCallFetchPersonalLoanInformationPersonalLoansInteractorWhenFetchPersonalLoanInformationFunctionIsCalled() {
        val accountNumber = "3001432596"

        testSubject.fetchPersonalLoanInformation(accountNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<PersonalLoanHubInformation>>(testSubject, "personalLoanHubExtendedResponseListener")
        verify(personalLoanMockService).fetchPersonalLoanInformation(accountNumber, extendedResponseListenerReflection)
        verifyNoMoreInteractions(personalLoanMockService)
    }

    @Test
    fun shouldCallRequestCallBackHomeScreenInteractorWhenRequestCallBackFunctionIsCalled() {
        val secretCode = "PL_LEAD"
        val callBackDateTime = "NOW"

        testSubject.requestCallBack(secretCode, callBackDateTime)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CallBackResponse>>(testSubject, "callBackExtendedResponseListener")
        verify(homeScreenMockService).requestCallBack(secretCode, callBackDateTime, extendedResponseListenerReflection)
        verifyNoMoreInteractions(homeScreenMockService)
    }

    @Test
    fun shouldReturnPersonalLoanInformationWhenServiceCallComplete() {
        val personalLoanVCLViewModelSpy = spy(testSubject)
        val ficaStatusExtendedResponseListener = PersonalLoanHubExtendedResponseListener(testSubject)
        val personalLoanInformationResponse = PersonalLoanHubInformation()
        val accountNumber = "3001432596"
        val personalLoan = PersonalLoanHubInformationValues()

        Mockito.`when`(personalLoanMockService.fetchPersonalLoanInformation(accountNumber, ficaStatusExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PersonalLoanHubExtendedResponseListener>(1).onSuccess(personalLoanInformationResponse.apply {
                this.personalLoan = personalLoan
            })
        }

        val observer = mock<Observer<PersonalLoanHubInformation>>()
        personalLoanVCLViewModelSpy.personalLoanHubExtendedResponse.observeForever(observer)

        personalLoanMockService.fetchPersonalLoanInformation(accountNumber, ficaStatusExtendedResponseListener)
        verify(personalLoanMockService).fetchPersonalLoanInformation(any(), capture(fetchPersonalInformationCaptor))

        Assert.assertEquals(ficaStatusExtendedResponseListener, fetchPersonalInformationCaptor.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.personalLoanHubExtendedResponse.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.personalLoanHubExtendedResponse.value?.personalLoan)

        verifyNoMoreInteractions(personalLoanMockService)
    }

    @Test
    fun shouldReturnFicaStatusWhenServiceCallComplete() {
        val personalLoanVCLViewModelSpy = spy(testSubject)
        val ficaCheckExtendedResponseListener = PersonalLoanFicaCheckExtendedResponseListener(testSubject)
        val ficaCheckResponseExtendedResponse = FicaStatus()
        val ficaStatus = "Y"

        Mockito.`when`(personalLoanMockService.fetchFicaStatus(ficaCheckExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PersonalLoanFicaCheckExtendedResponseListener>(0).onSuccess(ficaCheckResponseExtendedResponse.apply {
                status = ficaStatus
            })
        }

        val observer = mock<Observer<FicaStatus>>()
        personalLoanVCLViewModelSpy.ficaCheckResponseExtendedResponse.observeForever(observer)

        personalLoanMockService.fetchFicaStatus(ficaCheckExtendedResponseListener)
        verify(personalLoanMockService).fetchFicaStatus(capture(fetchFicaStatusCaptor))

        Assert.assertEquals(ficaCheckExtendedResponseListener, fetchFicaStatusCaptor.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.ficaCheckResponseExtendedResponse.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.ficaCheckResponseExtendedResponse.value?.status)

        verifyNoMoreInteractions(personalLoanMockService)
    }

    @Test
    fun shouldReturnCallBackWhenServiceCallComplete() {
        val personalLoanVCLViewModelSpy = spy(testSubject)
        val callBackExtendedResponseListener = CallBackExtendedResponseListener(testSubject)
        val callBackExtendedResponse = CallBackResponse()
        val secretCode = "PL_LEAD"
        val callBackDateTime = "NOW"
        val uniqueReferenceNumber = "Mjd2N0F6aU5"

        Mockito.`when`(homeScreenMockService.requestCallBack(secretCode, callBackDateTime, callBackExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CallBackExtendedResponseListener>(2).onSuccess(callBackExtendedResponse.apply {
                this.uniqueReferenceNumber = uniqueReferenceNumber
            })
        }

        val observer = mock<Observer<CallBackResponse>>()
        personalLoanVCLViewModelSpy.callBackExtendedResponse.observeForever(observer)

        homeScreenMockService.requestCallBack(secretCode, callBackDateTime, callBackExtendedResponseListener)
        verify(homeScreenMockService).requestCallBack(any(), any(), capture(requestCallBackCaptor))

        Assert.assertEquals(callBackExtendedResponseListener, requestCallBackCaptor.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.callBackExtendedResponse.value)
        Assert.assertNotNull(personalLoanVCLViewModelSpy.callBackExtendedResponse.value?.uniqueReferenceNumber)

        verifyNoMoreInteractions(homeScreenMockService)
    }

}