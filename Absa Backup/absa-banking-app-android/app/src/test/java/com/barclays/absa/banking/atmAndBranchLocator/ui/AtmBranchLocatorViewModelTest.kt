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
package com.barclays.absa.banking.atmAndBranchLocator.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.atmAndBranchLocator.services.AtmBranchLocatorInteractor
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchDetails
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchLocatorResponse
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
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

internal class AtmBranchLocatorViewModelTest {
    private var atmBranchLocatorViewModel = AtmBranchLocatorViewModel()
    private var atmBranchLocatorInteractor = mock<AtmBranchLocatorInteractor>()

    @Captor
    private lateinit var atmBranchLocatorCaptor: ArgumentCaptor<AtmBranchLocatorExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        atmBranchLocatorViewModel.atmBranchLocatorInteractor = atmBranchLocatorInteractor
    }

    @Test
    fun shouldFetchAtmBranchDetailsInteractorWhenFetchBranchLocationsFunctionIsCalled() {
        val latitude = "78.67"
        val longitude = "86.90"
        val radius = 5000

        atmBranchLocatorViewModel.fetchBranchLocations(latitude, longitude)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<AtmBranchLocatorResponse>>(atmBranchLocatorViewModel, "atmBranchLocatorExtendedResponseListener")
        verify(atmBranchLocatorInteractor).fetchAtmBranchDetails(latitude, longitude, radius, extendedResponseListenerReflection)
        verifyNoMoreInteractions(atmBranchLocatorInteractor)
    }

    @Test
    fun shouldFetchBranchLocationsWhenServiceCallComplete() {
        val atmBranchLocatorViewModelSpy = spy(atmBranchLocatorViewModel)
        val atmBranchLocatorExtendedResponseListener = AtmBranchLocatorExtendedResponseListener(atmBranchLocatorViewModel)
        val atmBranchLocatorResponseListener = AtmBranchLocatorResponse()
        val atmBranchDetails = AtmBranchDetails()
        val latitude = "78.67"
        val longitude = "86.90"
        val radius = 5000

        Mockito.`when`(atmBranchLocatorInteractor.fetchAtmBranchDetails(latitude, longitude, radius, atmBranchLocatorExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<AtmBranchLocatorExtendedResponseListener>(3).onSuccess(atmBranchLocatorResponseListener.apply {
                this.atmBranchDetails = listOf(atmBranchDetails)
            })
        }

        val observer = mock<Observer<AtmBranchLocatorResponse>>()
        atmBranchLocatorViewModelSpy.atmBranchLocatorExtendedResponse.observeForever(observer)

        atmBranchLocatorInteractor.fetchAtmBranchDetails(latitude, longitude, radius, atmBranchLocatorExtendedResponseListener)
        verify(atmBranchLocatorInteractor).fetchAtmBranchDetails(any(), any(), any(), capture(atmBranchLocatorCaptor))

        Assert.assertEquals(atmBranchLocatorCaptor.value, atmBranchLocatorExtendedResponseListener)
        Assert.assertNotNull(atmBranchLocatorViewModelSpy.atmBranchLocatorExtendedResponse.value)
        Assert.assertNotNull(atmBranchLocatorViewModelSpy.atmBranchLocatorExtendedResponse.value?.atmBranchDetails)
        verifyNoMoreInteractions(atmBranchLocatorInteractor)
    }
}