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

package com.barclays.absa.banking.directMarketing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.directMarketing.services.DirectMarketingInteractor
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.directMarketing.services.dto.UpdateMarketingIndicatorResponse
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

class DirectMarketingViewModelTest {

    private var testSubject = DirectMarketingViewModel()
    private var directMarketingMockService = mock<DirectMarketingInteractor>()

    @Captor
    private lateinit var updateMarketingIndicatorsCaptor: ArgumentCaptor<ExtendedResponseListener<UpdateMarketingIndicatorResponse?>>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.directMarketingInteractor = directMarketingMockService
    }

    @Test
    fun shouldCallUpdateMarketingIndicatorsDirectMarketingInteractorWhenUpdateMarketingIndicatorsFunctionIsCalled() {
        val marketingIndicators = MarketingIndicators()

        testSubject.updateMarketingIndicators(marketingIndicators)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<UpdateMarketingIndicatorResponse?>>(testSubject, "updateMarketingIndicatorExtendedResponseListener")
        verify(directMarketingMockService).updateMarketingIndicators(marketingIndicators, extendedResponseListener)
        verifyNoMoreInteractions(directMarketingMockService)
    }

    @Test
    fun shouldReturnUpdateMarketingIndicatorsWhenServiceCallComplete() {
        val directMarketingViewModelSpy = spy(testSubject)
        val updateMarketingIndicatorExtendedResponseListener = UpdateDirectMarketingIndicatorsExtendedResponseListener(testSubject)
        val marketingIndicatorResponse = UpdateMarketingIndicatorResponse()
        val marketingIndicators = MarketingIndicators()

        Mockito.`when`(directMarketingMockService.updateMarketingIndicators(marketingIndicators, updateMarketingIndicatorExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<UpdateDirectMarketingIndicatorsExtendedResponseListener>(1).onSuccess(marketingIndicatorResponse)
        }

        val observer = mock<Observer<UpdateMarketingIndicatorResponse?>>()
        directMarketingViewModelSpy.marketingIndicatorResponse.observeForever(observer)

        directMarketingMockService.updateMarketingIndicators(marketingIndicators, updateMarketingIndicatorExtendedResponseListener)
        verify(directMarketingMockService).updateMarketingIndicators(any(), capture(updateMarketingIndicatorsCaptor))

        Assert.assertEquals(updateMarketingIndicatorExtendedResponseListener, updateMarketingIndicatorsCaptor.value)
        Assert.assertNotNull(directMarketingViewModelSpy.marketingIndicatorResponse.value)

        verifyNoMoreInteractions(directMarketingMockService)
    }
}