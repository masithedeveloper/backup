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

package com.barclays.absa.banking.riskBasedApproach.services.dto

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachInteractor
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class RiskBasedApproachViewModelTest {

    private var testSubject = RiskBasedApproachViewModel()
    private var riskBasedApproachMockService = mock<RiskBasedApproachInteractor>()

    @Captor
    private lateinit var fetchCasaStatusCaptor: ArgumentCaptor<CasaStatusExtendedResponseListener>

    @Captor
    private lateinit var fetchRiskProfileCaptor: ArgumentCaptor<RiskProfileExtendedResponseListener>

    @Captor
    private lateinit var fetchPersonalInformationCaptor: ArgumentCaptor<PersonalInformationExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.riskBasedApproachInteractor = riskBasedApproachMockService
    }

    @Test
    fun shouldCallFetchCasaStatusRiskBasedApproachInteractorWhenFetchCasaStatusFunctionIsCalled() {
        testSubject.fetchCasaStatus()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CasaStatusResponse>>(testSubject, "casaStatusExtendedResponseListener")
        verify(riskBasedApproachMockService).getCasaStatus(extendedResponseListenerReflection)
        verifyNoMoreInteractions(riskBasedApproachMockService)
    }

    @Test
    fun shouldCallFetchRiskProfileRiskBasedApproachInteractorWhenFetchRiskProfileFunctionIsCalled() {
        val riskProfileDetails = RiskProfileDetails()

        testSubject.fetchRiskProfile(riskProfileDetails)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<RiskProfileResponse?>>(testSubject, "riskProfileExtendedResponseListener")
        verify(riskBasedApproachMockService).getRiskProfile(riskProfileDetails, extendedResponseListenerReflection)
        verifyNoMoreInteractions(riskBasedApproachMockService)
    }

    @Test
    fun shouldCallFetchPersonalInformationRiskBasedApproachInteractorWhenFetchPersonalInformationFunctionIsCalled() {
        testSubject.fetchPersonalInformation()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<PersonalInformationResponse?>>(testSubject, "personalInformationResponseListener")
        verify(riskBasedApproachMockService).fetchPersonalInformation(extendedResponseListenerReflection)
        verifyNoMoreInteractions(riskBasedApproachMockService)
    }

    @Test
    fun shouldReturnCasaStatusWhenServiceCallComplete() {
        val riskBasedApproachViewModelSpy = spy(testSubject)
        val casaStatusExtendedResponseListener = CasaStatusExtendedResponseListener(testSubject)
        val casaStatusResponse = CasaStatusResponse()
        val casaReference = "1840977"

        Mockito.`when`(riskBasedApproachMockService.getCasaStatus(casaStatusExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CasaStatusExtendedResponseListener>(0).onSuccess(casaStatusResponse.apply {
                this.casaReference = casaReference
            })
        }

        val observer = mock<Observer<CasaStatusResponse>>()
        riskBasedApproachViewModelSpy.casaStatusResponse.observeForever(observer)

        riskBasedApproachMockService.getCasaStatus(casaStatusExtendedResponseListener)
        verify(riskBasedApproachMockService).getCasaStatus(capture(fetchCasaStatusCaptor))

        Assert.assertEquals(casaStatusExtendedResponseListener, fetchCasaStatusCaptor.value)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.casaStatusResponse.value)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.casaStatusResponse.value?.casaReference)

        verifyNoMoreInteractions(riskBasedApproachMockService)
    }

    @Test
    fun shouldReturnRiskProfileWhenServiceCallComplete() {
        val riskBasedApproachViewModelSpy = spy(testSubject)
        val riskProfileExtendedResponseListener = RiskProfileExtendedResponseListener(testSubject)
        val riskProfileResponse = RiskProfileResponse()
        val riskProfileDetails = RiskProfileDetails()
        val casaReference = "1840977"
        val riskRating = "M"

        Mockito.`when`(riskBasedApproachMockService.getRiskProfile(riskProfileDetails, riskProfileExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RiskProfileExtendedResponseListener>(1).onSuccess(riskProfileResponse.apply {
                this.casaReference = casaReference
                this.riskRating = riskRating
            })
        }

        val observer = mock<Observer<RiskProfileResponse>>()
        riskBasedApproachViewModelSpy.riskProfileResponse.observeForever(observer)

        riskBasedApproachMockService.getRiskProfile(riskProfileDetails, riskProfileExtendedResponseListener)
        verify(riskBasedApproachMockService).getRiskProfile(any(), capture(fetchRiskProfileCaptor))

        Assert.assertEquals(riskProfileExtendedResponseListener, fetchRiskProfileCaptor.value)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.riskProfileResponse.value?.casaReference)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.riskProfileResponse.value?.riskRating)

        verifyNoMoreInteractions(riskBasedApproachMockService)
    }

    @Test
    fun shouldReturnPersonalInformationWhenServiceCallComplete() {
        val riskBasedApproachViewModelSpy = spy(testSubject)
        val personalInformationResponseListener = PersonalInformationExtendedResponseListener(testSubject)
        val personalInformationResponse = PersonalInformationResponse()

        Mockito.`when`(riskBasedApproachMockService.fetchPersonalInformation(personalInformationResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PersonalInformationExtendedResponseListener>(0).onSuccess(personalInformationResponse.apply {
                this.customerInformation = PersonalInformationResponse.CustomerInformation()
            })
        }

        val observer = mock<Observer<PersonalInformationResponse>>()
        riskBasedApproachViewModelSpy.personalInformationResponse.observeForever(observer)

        riskBasedApproachMockService.fetchPersonalInformation(personalInformationResponseListener)
        verify(riskBasedApproachMockService).fetchPersonalInformation(capture(fetchPersonalInformationCaptor))

        Assert.assertEquals(personalInformationResponseListener, fetchPersonalInformationCaptor.value)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.personalInformationResponse.value)
        Assert.assertNotNull(riskBasedApproachViewModelSpy.personalInformationResponse.value?.customerInformation)

        verifyNoMoreInteractions(riskBasedApproachMockService)
    }
}