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
package com.barclays.absa.banking.freeCover.services.dto

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.freeCover.services.FreeCoverInteractor
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class FreeCoverViewModelTest {
    private var testSubject = FreeCoverViewModel()
    private var freeCoverService = mock<FreeCoverInteractor>()
    private var applyFreeCoverData = ApplyFreeCoverData()

    @Captor
    private lateinit var applyFreeCoverCaptor: ArgumentCaptor<ApplyForFreeCoverExtendedResponseListener>

    @Captor
    private lateinit var fetchCoverAmountCaptor: ArgumentCaptor<CoverAmountApplyFreeCoverExtendedResponseListener>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.freeCoverInteractor = freeCoverService
    }

    @Test
    fun shouldReturnApplyFreeCoverWhenServiceCallComplete() {
        val freeCoverViewModelSpy = spy(testSubject)
        val freeCoverResponseListener = ApplyForFreeCoverExtendedResponseListener(testSubject)
        val freeCoverResponse = ApplyForFreeCoverResponse()
        val successMessage = "Success"

        Mockito.`when`(freeCoverService.applyFreeCover(applyFreeCoverData, freeCoverResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ApplyForFreeCoverExtendedResponseListener>(1).onSuccess(freeCoverResponse.apply {
                this.freeInsuranceResponse = FreeCoverInsuranceResponse()
                this.transactionMessage = successMessage
                this.transactionStatus = successMessage
            })
        }

        val observer = mock<Observer<ApplyForFreeCoverResponse>>()
        freeCoverViewModelSpy.applyForFreeCoverStatusResponse.observeForever(observer)

        freeCoverService.applyFreeCover(applyFreeCoverData, freeCoverResponseListener)
        verify(freeCoverService).applyFreeCover(any(), capture(applyFreeCoverCaptor))

        Assert.assertEquals(applyFreeCoverCaptor.value, freeCoverResponseListener)
        Assert.assertEquals(freeCoverViewModelSpy.applyForFreeCoverStatusResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(freeCoverViewModelSpy.applyForFreeCoverStatusResponse.value?.transactionStatus, successMessage)
        Assert.assertNotNull(freeCoverViewModelSpy.applyForFreeCoverStatusResponse.value?.freeInsuranceResponse)

        verifyNoMoreInteractions(freeCoverService)
    }

    @Test
    fun shouldReturnFetchCoverAmountWhenServiceCallComplete() {
        val freeCoverViewModelSpy = spy(testSubject)
        val freeCoverResponseListener = CoverAmountApplyFreeCoverExtendedResponseListener(testSubject)
        val freeCoverResponse = CoverAmountApplyFreeCoverResponse()
        val successMessage = "Success"

        Mockito.`when`(freeCoverService.fetchCoverAmount(freeCoverResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CoverAmountApplyFreeCoverExtendedResponseListener>(0).onSuccess(freeCoverResponse.apply {
                this.coverAmount = CoverAmount()
                this.transactionStatus = successMessage
                this.transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<CoverAmountApplyFreeCoverResponse>>()
        freeCoverViewModelSpy.coverAmountApplyFreeCoverResponse.observeForever(observer)

        freeCoverService.fetchCoverAmount(freeCoverResponseListener)
        verify(freeCoverService).fetchCoverAmount(capture(fetchCoverAmountCaptor))

        Assert.assertEquals(fetchCoverAmountCaptor.value, freeCoverResponseListener)
        Assert.assertEquals(freeCoverViewModelSpy.coverAmountApplyFreeCoverResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(freeCoverViewModelSpy.coverAmountApplyFreeCoverResponse.value?.transactionStatus, successMessage)
        Assert.assertNotNull(freeCoverViewModelSpy.coverAmountApplyFreeCoverResponse.value?.coverAmount)

        verifyNoMoreInteractions(freeCoverService)
    }

    @Test
    fun shouldCallApplyFreeCoverInteractorWhenApplyFreeCoverFunctionIsCalled() {
        testSubject.applyFreeCover(applyFreeCoverData)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<ApplyForFreeCoverResponse>>(testSubject, "applyForFreeCoverExtendedResponseListener")
        verify(freeCoverService).applyFreeCover(applyFreeCoverData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(freeCoverService)
    }

    @Test
    fun shouldCallFetchCoverAmountInteractorWhenFetchCoverAmountFunctionIsCalled() {
        testSubject.fetchCoverAmount()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CoverAmountApplyFreeCoverResponse>>(testSubject, "coverAmountApplyFreeCoverExtendedResponseListener")
        verify(freeCoverService).fetchCoverAmount(extendedResponseListenerReflection)
        verifyNoMoreInteractions(freeCoverService)
    }
}