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

package com.barclays.absa.banking.flexiFuneral.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.flexiFuneral.services.dto.*
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

internal class FlexiFuneralViewModelTest {

    private var testSubject = FlexiFuneralViewModel()
    private var flexiFuneralMockService = mock<FlexiFuneralInteractor>()

    @Captor
    private lateinit var fetchValidationRulesCaptor: ArgumentCaptor<ExtendedResponseListener<FlexiFuneralValidationRulesResponse>>

    @Captor
    private lateinit var fetchMainMemberCoverAmountsCaptor: ArgumentCaptor<ExtendedResponseListener<MainMemberCoverAmountsResponse>>

    @Captor
    private lateinit var fetchFamilyMemberCoverAmountsCaptor: ArgumentCaptor<ExtendedResponseListener<FamilyMemberCoverAmountsResponse>>

    @Captor
    private lateinit var addBeneficiaryCaptor: ArgumentCaptor<ExtendedResponseListener<AddBeneficiaryStatusResponse>>

    @Captor
    private lateinit var fetchFlexiFuneralPremiumCaptor: ArgumentCaptor<ExtendedResponseListener<FlexiFuneralPremiumResponse>>

    @Captor
    private lateinit var applyForFlexiFuneralCaptor: ArgumentCaptor<ExtendedResponseListener<ApplyForFlexiFuneralResponse>>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.flexiFuneralInteractor = flexiFuneralMockService
    }

    @Test
    fun shouldCallFetchValidationRulesFlexiFuneralInteractorWhenFetchValidationRulesFunctionIsCalled() {
        testSubject.fetchValidationRules()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FlexiFuneralValidationRulesResponse>>(testSubject, "validationRulesExtendedResponseListener")
        verify(flexiFuneralMockService).fetchValidationRules(extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldCallFetchMainMemberCoverAmountsFlexiFuneralInteractorWhenFetchMainMemberCoverAmountsFunctionIsCalled() {
        testSubject.fetchMainMemberCoverAmounts()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<MainMemberCoverAmountsResponse>>(testSubject, "mainMemberCoverAmountsExtendedResponseListener")
        verify(flexiFuneralMockService).fetchMainMemberCoverAmounts(extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldCallFetchFamilyMemberCoverAmountsFlexiFuneralInteractorWhenFetchFamilyMemberCoverAmountsFunctionIsCalled() {
        val multipleDependentsDetails = MultipleDependentsDetails()

        testSubject.fetchFamilyMemberCoverAmounts(multipleDependentsDetails)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FamilyMemberCoverAmountsResponse>>(testSubject, "familyMemberCoverAmountsExtendedResponseListener")
        verify(flexiFuneralMockService).fetchFamilyMemberCoverAmounts(multipleDependentsDetails, extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldCallAddBeneficiaryFlexiFuneralInteractorWhenAddBeneficiaryFunctionIsCalled() {
        val beneficiaryDetails = FlexiFuneralBeneficiaryDetails()

        testSubject.addBeneficiary(beneficiaryDetails)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<AddBeneficiaryStatusResponse>>(testSubject, "addFlexiFuneralBeneficiaryResponseListener")
        verify(flexiFuneralMockService).addFlexiFuneralBeneficiary(beneficiaryDetails, extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldCallFetchFlexiFuneralPremiumFlexiFuneralInteractorWhenFetchFlexiFuneralPremiumFunctionIsCalled() {
        val multipleDependentsDetails = MultipleDependentsDetails()

        testSubject.fetchFlexiFuneralPremium(multipleDependentsDetails)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FlexiFuneralPremiumResponse>>(testSubject, "flexiFuneralPremiumExtendedResponseListener")
        verify(flexiFuneralMockService).fetchFlexiFuneralPremium(multipleDependentsDetails, extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldCallApplyForFlexiFuneralFlexiFuneralInteractorWhenApplyForFlexiFuneralFunctionIsCalled() {
        val applyForFlexiFuneralData = ApplyForFlexiFuneralData()

        testSubject.applyForFlexiFuneral(applyForFlexiFuneralData)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<ApplyForFlexiFuneralResponse>>(testSubject, "applyForFlexiFuneralExtendedResponseListener")
        verify(flexiFuneralMockService).applyForFlexiFuneral(applyForFlexiFuneralData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnValidationRulesWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val validationRulesExtendedResponseListener = FlexiFuneralValidationRulesExtendedResponseListener(testSubject)
        val validationRulesResponse = FlexiFuneralValidationRulesResponse()
        val validationRules = listOf<FlexiFuneralValidationRulesDetails>()
        val transactionStatus = "success"

        Mockito.`when`(flexiFuneralMockService.fetchValidationRules(validationRulesExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FlexiFuneralValidationRulesExtendedResponseListener>(0).onSuccess(validationRulesResponse.apply {
                this.validationRules = validationRules
                this.transactionStatus = transactionStatus
            })
        }

        val observer = mock<Observer<FlexiFuneralValidationRulesResponse>>()
        flexiFuneralViewModelSpy.validationRules.observeForever(observer)

        flexiFuneralMockService.fetchValidationRules(validationRulesExtendedResponseListener)
        verify(flexiFuneralMockService).fetchValidationRules(capture(fetchValidationRulesCaptor))

        Assert.assertEquals(validationRulesExtendedResponseListener, fetchValidationRulesCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.validationRules.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.validationRules.value?.validationRules)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnMainMemberCoverAmountsWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val mainMemberCoverAmountsExtendedResponseListener = MainMemberCoverAmountsExtendedResponseListener(testSubject)
        val mainMemberCoverAmounts = MainMemberCoverAmountsResponse()
        val quotes = listOf<CoverDetails>()
        val transactionStatus = "success"

        Mockito.`when`(flexiFuneralMockService.fetchMainMemberCoverAmounts(mainMemberCoverAmountsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<MainMemberCoverAmountsExtendedResponseListener>(0).onSuccess(mainMemberCoverAmounts.apply {
                this.quotes = quotes
                this.transactionStatus = transactionStatus
            })
        }

        val observer = mock<Observer<MainMemberCoverAmountsResponse>>()
        flexiFuneralViewModelSpy.mainMemberCoverAmounts.observeForever(observer)

        flexiFuneralMockService.fetchMainMemberCoverAmounts(mainMemberCoverAmountsExtendedResponseListener)
        verify(flexiFuneralMockService).fetchMainMemberCoverAmounts(capture(fetchMainMemberCoverAmountsCaptor))

        Assert.assertEquals(mainMemberCoverAmountsExtendedResponseListener, fetchMainMemberCoverAmountsCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.mainMemberCoverAmounts.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.mainMemberCoverAmounts.value?.quotes)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnFamilyMemberCoverAmountsWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val familyMemberCoverAmountsResponse = FamilyMemberCoverAmountsResponse()
        val familyMemberCoverAmountsExtendedResponseListener = FamilyMemberCoverAmountsExtendedResponseListener(testSubject)
        val multipleDependentsDetails = MultipleDependentsDetails()
        val quotes = listOf<FamilyMemberCoverAmountsQuotes>()
        val transactionStatus = "success"

        Mockito.`when`(flexiFuneralMockService.fetchFamilyMemberCoverAmounts(multipleDependentsDetails, familyMemberCoverAmountsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FamilyMemberCoverAmountsExtendedResponseListener>(1).onSuccess(familyMemberCoverAmountsResponse.apply {
                this.quotes = quotes
                this.transactionStatus = transactionStatus
            })
        }

        val observer = mock<Observer<FamilyMemberCoverAmountsResponse>>()
        flexiFuneralViewModelSpy.familyMemberCoverAmounts.observeForever(observer)

        flexiFuneralMockService.fetchFamilyMemberCoverAmounts(multipleDependentsDetails, familyMemberCoverAmountsExtendedResponseListener)
        verify(flexiFuneralMockService).fetchFamilyMemberCoverAmounts(any(), capture(fetchFamilyMemberCoverAmountsCaptor))

        Assert.assertEquals(familyMemberCoverAmountsExtendedResponseListener, fetchFamilyMemberCoverAmountsCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.familyMemberCoverAmounts.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.familyMemberCoverAmounts.value?.quotes)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnAddBeneficiaryWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val addFlexiFuneralBeneficiaryResponseListener = AddBeneficiaryExtendedResponseListener(testSubject)
        val addBeneficiaryStatusResponse = AddBeneficiaryStatusResponse()
        val beneficiaryDetails = FlexiFuneralBeneficiaryDetails()
        val inceptionDate = "2020-03-02"
        val transactionStatus = "success"

        Mockito.`when`(flexiFuneralMockService.addFlexiFuneralBeneficiary(beneficiaryDetails, addFlexiFuneralBeneficiaryResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<AddBeneficiaryExtendedResponseListener>(1).onSuccess(addBeneficiaryStatusResponse.apply {
                this.inceptionDate = inceptionDate
                this.transactionStatus = transactionStatus
            })
        }

        val observer = mock<Observer<AddBeneficiaryStatusResponse>>()
        flexiFuneralViewModelSpy.addBeneficiaryStatus.observeForever(observer)

        flexiFuneralMockService.addFlexiFuneralBeneficiary(beneficiaryDetails, addFlexiFuneralBeneficiaryResponseListener)
        verify(flexiFuneralMockService).addFlexiFuneralBeneficiary(any(), capture(addBeneficiaryCaptor))

        Assert.assertEquals(addFlexiFuneralBeneficiaryResponseListener, addBeneficiaryCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.addBeneficiaryStatus.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.addBeneficiaryStatus.value?.inceptionDate)
        Assert.assertNotNull(flexiFuneralViewModelSpy.addBeneficiaryStatus.value?.addBeneficiaryStatus)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnFlexiFuneralPremiumWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val flexiFuneralPremiumExtendedResponseListener = FlexiFuneralPremiumExtendedResponseListener(testSubject)
        val flexiFuneralPremiumResponse = FlexiFuneralPremiumResponse()
        val multipleDependentsDetails = MultipleDependentsDetails()
        val totalCoverAmount = "120.0"
        val totalPremium = "0.0"
        val transactionStatus = "success"


        Mockito.`when`(flexiFuneralMockService.fetchFlexiFuneralPremium(multipleDependentsDetails, flexiFuneralPremiumExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FlexiFuneralPremiumExtendedResponseListener>(1).onSuccess(flexiFuneralPremiumResponse.apply {
                this.transactionStatus = transactionStatus
                this.totalPremium = totalPremium
                this.totalCoverAmount = totalCoverAmount
            })
        }

        val observer = mock<Observer<FlexiFuneralPremiumResponse>>()
        flexiFuneralViewModelSpy.flexiFuneralPremium.observeForever(observer)

        flexiFuneralMockService.fetchFlexiFuneralPremium(multipleDependentsDetails, flexiFuneralPremiumExtendedResponseListener)
        verify(flexiFuneralMockService).fetchFlexiFuneralPremium(any(), capture(fetchFlexiFuneralPremiumCaptor))

        Assert.assertEquals(flexiFuneralPremiumExtendedResponseListener, fetchFlexiFuneralPremiumCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.flexiFuneralPremium.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.flexiFuneralPremium.value?.totalCoverAmount)
        Assert.assertNotNull(flexiFuneralViewModelSpy.flexiFuneralPremium.value?.totalPremium)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }

    @Test
    fun shouldReturnApplyForFlexiFuneralWhenServiceCallComplete() {
        val flexiFuneralViewModelSpy = spy(testSubject)
        val applyForFlexiFuneralExtendedResponseListener = ApplyForFlexiFuneralExtendedResponseListener(testSubject)
        val applyForFlexiFuneralResponse = ApplyForFlexiFuneralResponse()
        val applyForFlexiFuneralData = ApplyForFlexiFuneralData()
        val flexiFuneralPlan = FlexiFuneralPolicyDetails()

        Mockito.`when`(flexiFuneralMockService.applyForFlexiFuneral(applyForFlexiFuneralData, applyForFlexiFuneralExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ApplyForFlexiFuneralExtendedResponseListener>(1).onSuccess(applyForFlexiFuneralResponse.apply {
                this.flexiFuneralPlan = flexiFuneralPlan
            })
        }

        val observer = mock<Observer<ApplyForFlexiFuneralResponse>>()
        flexiFuneralViewModelSpy.applyForFlexiFuneral.observeForever(observer)

        flexiFuneralMockService.applyForFlexiFuneral(applyForFlexiFuneralData, applyForFlexiFuneralExtendedResponseListener)
        verify(flexiFuneralMockService).applyForFlexiFuneral(any(), capture(applyForFlexiFuneralCaptor))

        Assert.assertEquals(applyForFlexiFuneralExtendedResponseListener, applyForFlexiFuneralCaptor.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.applyForFlexiFuneral.value)
        Assert.assertNotNull(flexiFuneralViewModelSpy.applyForFlexiFuneral.value?.flexiFuneralPlan)

        verifyNoMoreInteractions(flexiFuneralMockService)
    }
}