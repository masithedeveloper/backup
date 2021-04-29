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
package com.barclays.absa.banking.card.ui.creditCardHotLeads

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationDetails
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardHotLeadApplicationResponse
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

internal class HotLeadsViewModelTest {
    private var testSubject = HotLeadsViewModel()
    private var hotLeadsMockService = mock<CreditCardInteractor>()

    @Captor
    private lateinit var hotLeadsCaptor: ArgumentCaptor<HotLeadApplicationExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.creditCardService = hotLeadsMockService
    }

    @Test
    fun shouldReturnApplyForCreditCardWhenServiceCallComplete() {
        val hotLeadsViewModelSpy = spy(testSubject)
        val hotLeadsResponseListener = HotLeadApplicationExtendedResponseListener(testSubject)
        val hotLeadsResponse = CreditCardHotLeadApplicationResponse()
        val cellphoneNumber = "0764673864"
        val hotLeadsResponseCode = "AP01"
        val successMessage = "Success"

        Mockito.`when`(hotLeadsMockService.applyForCreditCardHotLead(cellphoneNumber, hotLeadsResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<HotLeadApplicationExtendedResponseListener>(1).onSuccess(hotLeadsResponse.apply {
                this.transactionStatus = successMessage
                this.applyForCreditCardDetails = CreditCardHotLeadApplicationDetails().apply {
                    this.responseCode = hotLeadsResponseCode
                }
            })
        }

        val observer = mock<Observer<CreditCardHotLeadApplicationDetails>>()
        hotLeadsViewModelSpy.hotLeadApplicationResponse.observeForever(observer)

        hotLeadsMockService.applyForCreditCardHotLead(cellphoneNumber, hotLeadsResponseListener)
        verify(hotLeadsMockService).applyForCreditCardHotLead(any(), capture(hotLeadsCaptor))

        Assert.assertEquals(hotLeadsCaptor.value, hotLeadsResponseListener)
        Assert.assertNotNull(hotLeadsViewModelSpy.hotLeadApplicationResponse.value)
        Assert.assertEquals(hotLeadsViewModelSpy.hotLeadApplicationResponse.value?.responseCode, hotLeadsResponseCode)

        verifyNoMoreInteractions(hotLeadsMockService)
    }

    @Test
    fun shouldCallApplyForCreditCardHotLeadInteractorWhenApplyForCreditCardFunctionIsCalled() {
        val cellphoneNumber = "0764673864"

        testSubject.applyForCreditCard(cellphoneNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CreditCardHotLeadApplicationResponse>>(testSubject, "hotLeadApplicationExtendedResponseListener")
        verify(hotLeadsMockService).applyForCreditCardHotLead(cellphoneNumber, extendedResponseListenerReflection)
        verifyNoMoreInteractions(hotLeadsMockService)
    }
}