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

package com.barclays.absa.banking.lawForYou.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.lawForYou.services.*
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYouResponse
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmounts
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileResponse
import com.barclays.absa.banking.shared.services.dto.SuburbResponse
import com.barclays.absa.banking.shared.services.dto.SuburbResult
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class LawForYouViewModelTest {

    private var testSubject = LawForYouViewModel()
    private var lawForYouMockService = mock<LawForYouInteractor>()

    @Captor
    private lateinit var fetchCoverAmountsCaptor: ArgumentCaptor<RequestCoverAmountsResponseListener>

    @Captor
    private lateinit var fetchPersonalInformationCaptor: ArgumentCaptor<PersonalInformationResponseListener>

    @Captor
    private lateinit var fetchCityAndPostalCodesCaptor: ArgumentCaptor<SuburbResponseListener>

    @Captor
    private lateinit var requestRiskProfileCaptor: ArgumentCaptor<RiskProfileDetailsResponseListener>

    @Captor
    private lateinit var submitLawForYouApplicationCaptor: ArgumentCaptor<ApplyLawForYouResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.lawForYouService = lawForYouMockService
    }

    @Test
    fun shouldCallRequestCoverAmountsLawForYouInteractorWhenFetchCoverAmountsFunctionIsCalled() {
        testSubject.fetchCoverAmounts()
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<RequestCoverAmountsResponseListener>(testSubject, "requestCoverAmountResponseListener")
        verify(lawForYouMockService).requestCoverAmounts(extendedResponseListener)
        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldCallRequestPersonInformationLawForYouInteractorWhenFetchPersonalInformationFunctionIsCalled() {
        testSubject.fetchPersonalInformation()
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<PersonalInformationResponseListener>(testSubject, "personalInformationResponseListener")
        verify(lawForYouMockService).requestPersonInformation(extendedResponseListener)
        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldCallRequestSuburbLawForYouInteractorWhenFetchCityAndPostalCodesIsCalled() {
        val area = "Sandton"

        testSubject.fetchCityAndPostalCodes(area)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<SuburbResponseListener>(testSubject, "suburbResponseListener")
        verify(lawForYouMockService).requestSuburb(area, extendedResponseListener)
        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldCallRequestLawForYouApplicationLawForYouInteractorWhenSubmitLawForYouApplicationFunctionIsCalled() {
        val lawForYouDetails = LawForYouDetails()

        testSubject.submitLawForYouApplication()
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<ApplyLawForYouResponseListener>(testSubject, "applyLawForYouResponseListener")
        verify(lawForYouMockService).requestLawForYouApplication(lawForYouDetails, extendedResponseListener)
        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldCallRequestRiskProfileLawForYouInteractorWhenRequestRiskProfileFunctionIsCalled() {
        val riskProfileDetails = RiskProfileDetails()

        testSubject.requestRiskProfile(riskProfileDetails)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<RiskProfileDetailsResponseListener>(testSubject, "riskProfileResponseListener")
        verify(lawForYouMockService).requestRiskProfile(riskProfileDetails, extendedResponseListener)
        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldReturnFetchCoverAmountsWhenServiceCallComplete() {
        val lawForYouViewModelSpy = spy(testSubject)
        val requestCoverAmountResponseListener = RequestCoverAmountsResponseListener(testSubject)
        val requestCoverAmountsResponse = CoverAmountsResponse()
        val coverAmounts = listOf<CoverAmounts>()

        Mockito.`when`(lawForYouMockService.requestCoverAmounts(requestCoverAmountResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RequestCoverAmountsResponseListener>(0).onSuccess(requestCoverAmountsResponse.apply {
                this.coverAmounts = coverAmounts
            })
        }

        val observer = mock<Observer<CoverAmountsResponse>>()
        lawForYouViewModelSpy.coverOptionsMutableLifeData.observeForever(observer)

        lawForYouMockService.requestCoverAmounts(requestCoverAmountResponseListener)
        verify(lawForYouMockService).requestCoverAmounts(capture(fetchCoverAmountsCaptor))

        Assert.assertEquals(requestCoverAmountResponseListener, fetchCoverAmountsCaptor.value)
        Assert.assertNotNull(lawForYouViewModelSpy.coverOptionsMutableLifeData.value)
        Assert.assertNotNull(lawForYouViewModelSpy.coverOptionsMutableLifeData.value?.coverAmounts)

        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldReturnFetchPersonalInformationWhenServiceCallComplete() {
        val lawForYouViewModelSpy = spy(testSubject)
        val personalInformationResponseListener = PersonalInformationResponseListener(testSubject)
        val personalInformationResponse = PersonalInformationResponse()
        val customerInformation = PersonalInformationResponse.CustomerInformation()

        Mockito.`when`(lawForYouMockService.requestPersonInformation(personalInformationResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PersonalInformationResponseListener>(0).onSuccess(personalInformationResponse.apply {
                this.customerInformation = customerInformation
            })
        }

        val observer = mock<Observer<PersonalInformationResponse>>()
        lawForYouViewModelSpy.personalInformationResponseMutableLiveData.observeForever(observer)

        lawForYouMockService.requestPersonInformation(personalInformationResponseListener)
        verify(lawForYouMockService).requestPersonInformation(capture(fetchPersonalInformationCaptor))

        Assert.assertEquals(personalInformationResponseListener, fetchPersonalInformationCaptor.value)
        Assert.assertNotNull(lawForYouViewModelSpy.personalInformationResponseMutableLiveData.value)
        Assert.assertNotNull(lawForYouViewModelSpy.personalInformationResponseMutableLiveData.value?.customerInformation)

        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldReturnFetchCityAndPostalCodesWhenServiceCallComplete() {
        val lawForYouViewModelSpy = spy(testSubject)
        val suburbResponseListener = SuburbResponseListener(testSubject)
        val suburbResponse = SuburbResponse()
        val area = "Sandton"
        val suburbs = listOf<SuburbResult>()

        Mockito.`when`(lawForYouMockService.requestSuburb(area, suburbResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SuburbResponseListener>(1).onSuccess(suburbResponse.apply {
                this.suburbs = suburbs
            })
        }

        val observer = mock<Observer<SuburbResponse>>()
        lawForYouViewModelSpy.cityAndPostalCodeMutableLiveData.observeForever(observer)

        lawForYouMockService.requestSuburb(area, suburbResponseListener)
        verify(lawForYouMockService).requestSuburb(any(), capture(fetchCityAndPostalCodesCaptor))

        Assert.assertEquals(suburbResponseListener, fetchCityAndPostalCodesCaptor.value)
        Assert.assertNotNull(lawForYouViewModelSpy.cityAndPostalCodeMutableLiveData.value)
        Assert.assertNotNull(lawForYouViewModelSpy.cityAndPostalCodeMutableLiveData.value?.suburbs)

        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldReturnFetchRiskProfileWhenServiceCallComplete() {
        val lawForYouViewModelSpy = spy(testSubject)
        val riskProfileResponseListener = RiskProfileDetailsResponseListener(testSubject)
        val riskProfileResponse = RiskProfileResponse()
        val riskProfileDetails = RiskProfileDetails()
        val riskRating = "M"
        val casaReference = "1840977"

        Mockito.`when`(lawForYouMockService.requestRiskProfile(riskProfileDetails, riskProfileResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RiskProfileDetailsResponseListener>(1).onSuccess(riskProfileResponse.apply {
                this.riskRating = riskRating
                this.casaReference = casaReference
            })
        }

        val observer = mock<Observer<RiskProfileResponse>>()
        lawForYouViewModelSpy.riskProfileResponseMutableLiveData.observeForever(observer)

        lawForYouMockService.requestRiskProfile(riskProfileDetails, riskProfileResponseListener)
        verify(lawForYouMockService).requestRiskProfile(any(), capture(requestRiskProfileCaptor))

        Assert.assertEquals(riskProfileResponseListener, requestRiskProfileCaptor.value)
        Assert.assertNotNull(lawForYouViewModelSpy.riskProfileResponseMutableLiveData.value)
        Assert.assertNotNull(lawForYouViewModelSpy.riskProfileResponseMutableLiveData.value?.riskRating)
        Assert.assertNotNull(lawForYouViewModelSpy.riskProfileResponseMutableLiveData.value?.casaReference)

        verifyNoMoreInteractions(lawForYouMockService)
    }

    @Test
    fun shouldReturnSubmitLawForYouApplicationWhenServiceCallComplete() {
        val lawForYouViewModelSpy = spy(testSubject)
        val applyLawForYouResponseListener = ApplyLawForYouResponseListener(testSubject)
        val applyLawForYouResponse = ApplyLawForYouResponse()
        val lawForYouDetails = LawForYouDetails()
        val activeIndicator = "08252020"
        val planCounter = "0"
        val productReferenceNumber = "1-1FZ4IPU"
        val spousePlanCounter = "0"

        Mockito.`when`(lawForYouMockService.requestLawForYouApplication(lawForYouDetails, applyLawForYouResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ApplyLawForYouResponseListener>(1).onSuccess(applyLawForYouResponse.apply {
                this.activeIndicator = activeIndicator
                this.planCounter = planCounter
                this.productReferenceNumber = productReferenceNumber
                this.spousePlanCounter = spousePlanCounter
            })
        }

        val observer = mock<Observer<ApplyLawForYouResponse>>()
        lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.observeForever(observer)

        lawForYouMockService.requestLawForYouApplication(lawForYouDetails, applyLawForYouResponseListener)
        verify(lawForYouMockService).requestLawForYouApplication(any(), capture(submitLawForYouApplicationCaptor))

        Assert.assertEquals(applyLawForYouResponseListener, submitLawForYouApplicationCaptor.value)
        Assert.assertNotNull(lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.value)
        Assert.assertNotNull(lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.value?.activeIndicator)
        Assert.assertNotNull(lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.value?.planCounter)
        Assert.assertNotNull(lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.value?.productReferenceNumber)
        Assert.assertNotNull(lawForYouViewModelSpy.applyLawForYouResponseMutableLiveData.value?.spousePlanCounter)

        verifyNoMoreInteractions(lawForYouMockService)
    }
}