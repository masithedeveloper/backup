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
package com.barclays.absa.banking.shared.services

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.shared.services.dto.*
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class SharedViewModelTest {
    val testSubject = SharedViewModel()
    private val sharedMockService = mock<SharedInteractor>()

    @Captor
    private lateinit var getCIFCodesCaptor: ArgumentCaptor<GetCIFCodesExtendedResponseListener>

    @Captor
    private lateinit var suburbsCaptor: ArgumentCaptor<SuburbsExtendedResponseListener>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.sharedService = sharedMockService
    }

    @Test
    fun shouldCallPerformLookupInteractorWhenGetCIFCodesFunctionIsCalled() {
        testSubject.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<LookupResult>>(testSubject, "getCifCodesExtendedResponseListener")
        verify(sharedMockService).performLookup(CIFGroupCode.SOURCE_OF_FUNDS, extendedResponseListenerReflection)
        verifyNoMoreInteractions(sharedMockService)
    }

    @Test
    fun shouldCallFetchSuburbsWhenGetSuburbsFunctionIsCalled() {
        testSubject.getSuburbs("")
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<SuburbResponse>>(testSubject, "suburbsExtendedResponseListener")
        verify(sharedMockService).fetchSuburbs("", extendedResponseListenerReflection)
        verifyNoMoreInteractions(sharedMockService)
    }

    @Test
    fun shouldReturnPerformLookupWhenServiceCallComplete() {
        val sharedViewModelSpy = spy(testSubject)
        val sharedViewModelExtendedResponseListener = GetCIFCodesExtendedResponseListener(testSubject)
        val sharedViewModelResponse = LookupResult()

        Mockito.`when`(sharedMockService.performLookup(CIFGroupCode.SOURCE_OF_FUNDS, sharedViewModelExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<GetCIFCodesExtendedResponseListener>(1).onSuccess(sharedViewModelResponse.apply {
                this.items = listOf(LookupItem())
            })
        }

        val observer = mock<Observer<LookupResult>>()
        sharedViewModelSpy.codesLiveData.observeForever(observer)

        sharedMockService.performLookup(CIFGroupCode.SOURCE_OF_FUNDS, sharedViewModelExtendedResponseListener)
        verify(sharedMockService).performLookup(any(), capture(getCIFCodesCaptor))

        Assert.assertEquals(getCIFCodesCaptor.value, sharedViewModelExtendedResponseListener)
        Assert.assertNotNull(sharedViewModelSpy.codesLiveData.value)
        Assert.assertNotNull(sharedViewModelSpy.codesLiveData.value?.items)

        verifyNoMoreInteractions(sharedMockService)
    }

    @Test
    fun shouldReturnFetchSuburbsWhenServiceCallComplete() {
        val sharedViewModelSpy = spy(testSubject)
        val sharedViewModelExtendedResponseListener = SuburbsExtendedResponseListener(testSubject.suburbsMutableLiveData)
        val sharedViewModelResponse = SuburbResponse()

        Mockito.`when`(sharedMockService.fetchSuburbs("", sharedViewModelExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SuburbsExtendedResponseListener>(1).onSuccess(sharedViewModelResponse.apply {
                this.suburbs = listOf(SuburbResult())
            })
        }

        val observer = mock<Observer<List<SuburbResult>>>()
        testSubject.suburbsMutableLiveData.observeForever(observer)

        sharedMockService.fetchSuburbs("", sharedViewModelExtendedResponseListener)
        verify(sharedMockService).fetchSuburbs(any(), capture(suburbsCaptor))

        Assert.assertEquals(suburbsCaptor.value, sharedViewModelExtendedResponseListener)
        Assert.assertNotNull(testSubject.suburbsMutableLiveData.value)

        verifyNoMoreInteractions(sharedMockService)
    }
}