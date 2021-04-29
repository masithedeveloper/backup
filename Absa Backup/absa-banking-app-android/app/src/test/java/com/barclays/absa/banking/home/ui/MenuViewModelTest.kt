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
package com.barclays.absa.banking.home.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.ClientTypeResponse
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class MenuViewModelTest : DaggerTest() {
    val testSubject = MenuViewModel()
    private val internationalPaymentsMockService = mock<InternationalPaymentsInteractor>()

    @Captor
    private lateinit var clientTypeResponseCaptor: ArgumentCaptor<ClientTypeResponseExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.internationalPaymentsInteractor = internationalPaymentsMockService
    }

    @Test
    fun shouldCallFetchClientTypeInteractorWhenFetchCustomerDetailsFunctionIsCalled() {
        testSubject.fetchCustomerDetails()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ClientTypeResponseExtendedResponseListener>(testSubject, "clientTypeResponseExtendedResponseListener")
        verify(internationalPaymentsMockService).fetchClientType(extendedResponseListenerReflection)
        verifyNoMoreInteractions(internationalPaymentsMockService)
    }

    @Test
    fun shouldReturnFetchClientTypeWhenServiceCallComplete() {
        val menuViewModelSpy = spy(testSubject)
        val menuResponseListener = ClientTypeResponseExtendedResponseListener(testSubject)
        val menuResponse = ClientTypeResponse()

        Mockito.`when`(internationalPaymentsMockService.fetchClientType(menuResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ClientTypeResponseExtendedResponseListener>(0).onSuccess(menuResponse.apply {
                claimType = ""
                contactNumber = ""
                emailId = ""
                identityNumber = ""
                identityType = ""
                isInternationalPaymentsOptionVisible = true
                policyNumber = ""
                policyType = ""
            })
        }

        val observer = mock<Observer<ClientTypeResponse>>()
        menuViewModelSpy.isInternationalPaymentsOptionVisible.observeForever(observer)

        internationalPaymentsMockService.fetchClientType(menuResponseListener)
        verify(internationalPaymentsMockService).fetchClientType(capture(clientTypeResponseCaptor))

        Assert.assertEquals(clientTypeResponseCaptor.value, menuResponseListener)
        Assert.assertNotNull(menuViewModelSpy.isInternationalPaymentsOptionVisible.value)
        verifyNoMoreInteractions(internationalPaymentsMockService)
    }
}