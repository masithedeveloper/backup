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
 */
package com.barclays.absa.banking.presentation.homeLoan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.homeLoan.services.HomeLoanService
import com.barclays.absa.banking.presentation.homeLoan.services.dto.HomeLoanDTO
import com.barclays.absa.banking.presentation.homeLoan.services.dto.HomeLoanDetails
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class HomeLoanViewModelTest {

    private var homeLoanViewModel = HomeLoanViewModel()
    private var homeLoanService = mock<HomeLoanService>()

    @Captor
    private lateinit var homeLoanAccountDetailsCaptor: ArgumentCaptor<HomeLoanAccountDetailsExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        homeLoanViewModel.homeLoanService = homeLoanService
    }

    @Test
    fun shouldCallInteractorFetchHomeLoanAccountDetailsWhenFetchHomeLoanAccountDetails() {
        val accountNumber = "123456453"

        homeLoanViewModel.fetchHomeLoanDetails(accountNumber)
        val reflectedFetchAccountDetailsListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<HomeLoanDetails>>(homeLoanViewModel, "homeLoanAccountDetailsExtendedResponseListener")
        verify(homeLoanService).fetchHomeLoanAccountDetails(accountNumber, reflectedFetchAccountDetailsListener)
        verifyNoMoreInteractions(homeLoanService)
    }

    @Test
    fun shouldFetchHomeLoanAccountDetailsWhenServiceCallIsComplete() {
        val homeLoanViewModelSpy = spy(homeLoanViewModel)
        val homeLoanAccountDetailsListener = HomeLoanAccountDetailsExtendedResponseListener(homeLoanViewModel)
        val homeLoanResponse = HomeLoanDetails()
        val homeLoanDTO = HomeLoanDTO()
        val accountNumber = "123456453"

        Mockito.`when`(homeLoanService.fetchHomeLoanAccountDetails(accountNumber, homeLoanAccountDetailsListener)).thenAnswer {
            return@thenAnswer it.getArgument<HomeLoanAccountDetailsExtendedResponseListener>(1).onSuccess(homeLoanResponse.apply {
                this.homeLoanDTO = homeLoanDTO
            })
        }

        val observer = mock<Observer<HomeLoanDTO>>()
        homeLoanViewModelSpy.homeLoanDetails.observeForever(observer)

        homeLoanService.fetchHomeLoanAccountDetails(accountNumber, homeLoanAccountDetailsListener)
        verify(homeLoanService).fetchHomeLoanAccountDetails(any(), capture(homeLoanAccountDetailsCaptor))

        Assert.assertEquals(homeLoanAccountDetailsCaptor.value, homeLoanAccountDetailsListener)
        Assert.assertNotNull(homeLoanViewModelSpy.homeLoanDetails.value)

        verifyNoMoreInteractions(homeLoanService)
    }
}