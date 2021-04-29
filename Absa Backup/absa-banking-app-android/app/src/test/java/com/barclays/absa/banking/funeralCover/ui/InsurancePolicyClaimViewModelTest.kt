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
package com.barclays.absa.banking.funeralCover.ui

import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.notification.ClaimNotification
import com.barclays.absa.banking.boundary.model.notification.SubmitClaim
import com.barclays.absa.banking.boundary.model.policy.ClaimTypeDetails
import com.barclays.absa.banking.boundary.model.policy.PolicyClaimTypes
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.BeneficiaryDetailsExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.ClaimExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.NotificationExtendedResponseListener
import com.barclays.absa.banking.funeralCover.ui.responseListeners.PolicyClaimTypesExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class InsurancePolicyClaimViewModelTest {
    val testSubject = InsurancePolicyClaimViewModel()
    private val insurancePolicyMockService = mock<InsurancePolicyInteractor>()

    @Captor
    private lateinit var beneficiaryDetailsCaptor: ArgumentCaptor<BeneficiaryDetailsExtendedResponseListener>

    @Captor
    private lateinit var policyClaimTypesCaptor: ArgumentCaptor<PolicyClaimTypesExtendedResponseListener>

    @Captor
    private lateinit var notificationCaptor: ArgumentCaptor<NotificationExtendedResponseListener>

    @Captor
    private lateinit var claimCaptor: ArgumentCaptor<ClaimExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.insurancePolicyInteractor = insurancePolicyMockService
    }

    @Test
    fun shouldCallRequestCustomerDetailsInteractorWhenFetchCustomerDetailsFunctionIsCalled() {
        testSubject.fetchCustomerDetails()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<BeneficiaryDetailsExtendedResponseListener>(testSubject, "beneficiaryDetailsExtendedResponseListener")
        verify(insurancePolicyMockService).requestCustomerDetails(extendedResponseListenerReflection)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldCallFetchPolicyClaimTypesInteractorWhenFetchPolicyClaimTypesFunctionIsCalled() {
        testSubject.fetchPolicyClaimTypes("")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<PolicyClaimTypes>>(testSubject, "policyClaimTypesExtendedResponseListener")
        verify(insurancePolicyMockService).fetchPolicyClaimTypes("", extendedResponseListenerReflection)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldCallRequestPolicyClaimNotificationInteractorWhenRequestPolicyClaimNotificationFunctionIsCalled() {
        val policyClaimItem = PolicyClaimItem()

        testSubject.requestPolicyClaimNotification(policyClaimItem)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<ClaimNotification>>(testSubject, "notificationExtendedResponseListener")
        verify(insurancePolicyMockService).requestPolicyClaimNotification(policyClaimItem, extendedResponseListenerReflection)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldCallSubmitInsurancePolicyClaimInteractorWhenSubmitInsurancePolicyClaimFunctionIsCalled() {
        testSubject.submitInsurancePolicyClaim("")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<SubmitClaim>>(testSubject, "claimExtendedResponseListener")
        verify(insurancePolicyMockService).submitInsurancePolicyClaim("", extendedResponseListenerReflection)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldReturnRequestCustomerDetailsWhenServiceCallComplete() {
        val insurancePolicyClaimViewModelSpy = spy(testSubject)
        val insurancePolicyClaimResponseListener = BeneficiaryDetailsExtendedResponseListener(testSubject)
        val insurancePolicyClaimResponse = BeneficiaryDetailObject()

        Mockito.`when`(insurancePolicyMockService.requestCustomerDetails(insurancePolicyClaimResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<BeneficiaryDetailsExtendedResponseListener>(0).onSuccess(insurancePolicyClaimResponse)
        }

        val observer = mock<Observer<BeneficiaryDetailObject>>()
        insurancePolicyClaimViewModelSpy.beneficiaryDetailsLiveData.observeForever(observer)

        insurancePolicyMockService.requestCustomerDetails(insurancePolicyClaimResponseListener)
        verify(insurancePolicyMockService).requestCustomerDetails(capture(beneficiaryDetailsCaptor))

        Assert.assertEquals(beneficiaryDetailsCaptor.value, insurancePolicyClaimResponseListener)
        Assert.assertNotNull(insurancePolicyClaimViewModelSpy.beneficiaryDetailsLiveData.value)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldReturnFetchPolicyClaimTypesWhenServiceCallComplete() {
        val insurancePolicyClaimViewModelSpy = spy(testSubject)
        val insurancePolicyClaimResponseListener = PolicyClaimTypesExtendedResponseListener(testSubject)
        val insurancePolicyClaimResponse = PolicyClaimTypes()

        Mockito.`when`(insurancePolicyMockService.fetchPolicyClaimTypes("", insurancePolicyClaimResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PolicyClaimTypesExtendedResponseListener>(1).onSuccess(insurancePolicyClaimResponse.apply {
                this.claimTypes = listOf(ClaimTypeDetails())
            })
        }

        val observer = mock<Observer<PolicyClaimTypes>>()
        insurancePolicyClaimViewModelSpy.policyClaimTypesLiveData.observeForever(observer)

        insurancePolicyMockService.fetchPolicyClaimTypes("", insurancePolicyClaimResponseListener)
        verify(insurancePolicyMockService).fetchPolicyClaimTypes(any(), capture(policyClaimTypesCaptor))

        Assert.assertEquals(policyClaimTypesCaptor.value, insurancePolicyClaimResponseListener)
        Assert.assertNotNull(insurancePolicyClaimViewModelSpy.policyClaimTypesLiveData.value)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldReturnRequestPolicyClaimNotificationWhenServiceCallComplete() {
        val insurancePolicyClaimViewModelSpy = spy(testSubject)
        val insurancePolicyClaimResponseListener = NotificationExtendedResponseListener(testSubject)
        val insurancePolicyClaimResponse = ClaimNotification()
        val policyClaimItem = PolicyClaimItem()

        Mockito.`when`(insurancePolicyMockService.requestPolicyClaimNotification(policyClaimItem, insurancePolicyClaimResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<NotificationExtendedResponseListener>(1).onSuccess(insurancePolicyClaimResponse.apply {
                policyNumber = ""
                claimType = ""
                claimDescription = ""
                occuranceDate = ""
                contactNumber = ""
                emailId = ""
                policyType = ""
                contactTime = ""
                transactionReferenceId = ""
            })
        }

        val observer = mock<Observer<ClaimNotification>>()
        insurancePolicyClaimViewModelSpy.claimNotificationLiveData.observeForever(observer)

        insurancePolicyMockService.requestPolicyClaimNotification(policyClaimItem, insurancePolicyClaimResponseListener)
        verify(insurancePolicyMockService).requestPolicyClaimNotification(any(), capture(notificationCaptor))

        Assert.assertEquals(notificationCaptor.value, insurancePolicyClaimResponseListener)
        Assert.assertNotNull(insurancePolicyClaimViewModelSpy.claimNotificationLiveData.value)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }

    @Test
    fun shouldReturnSubmitInsurancePolicyClaimWhenServiceCallComplete() {
        val insurancePolicyClaimViewModelSpy = spy(testSubject)
        val insurancePolicyClaimResponseListener = ClaimExtendedResponseListener(testSubject)
        val insurancePolicyClaimResponse = SubmitClaim()

        Mockito.`when`(insurancePolicyMockService.submitInsurancePolicyClaim("", insurancePolicyClaimResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ClaimExtendedResponseListener>(1).onSuccess(insurancePolicyClaimResponse.apply {
                this.isClaimSuccessful = true
                this.referenceNumber = ""
            })
        }

        val observer = mock<Observer<SubmitClaim>>()
        insurancePolicyClaimViewModelSpy.submitClaimLiveData.observeForever(observer)

        insurancePolicyMockService.submitInsurancePolicyClaim("", insurancePolicyClaimResponseListener)
        verify(insurancePolicyMockService).submitInsurancePolicyClaim(any(), capture(claimCaptor))

        Assert.assertEquals(claimCaptor.value, insurancePolicyClaimResponseListener)
        Assert.assertNotNull(insurancePolicyClaimViewModelSpy.submitClaimLiveData.value)
        verifyNoMoreInteractions(insurancePolicyMockService)
    }
}