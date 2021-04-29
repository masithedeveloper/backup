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
package com.barclays.absa.banking.bankConfirmationLetter.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterInteractor
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterResponse
import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterResponseListener
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

class BankConfirmationLetterViewModelTest {
    private var testSubject = BankConfirmationLetterViewModel()
    private var bankConfirmationLetterMockService = mock<BankConfirmationLetterInteractor>()

    @Captor
    private lateinit var bankConfirmationLetterCaptor: ArgumentCaptor<BankConfirmationLetterResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.interactor = bankConfirmationLetterMockService
    }

    @Test
    fun shouldReturnBankConfirmationWhenServiceCallComplete() {
        val bankConfirmationLetterViewModelSpy = spy(testSubject)
        val bankConfirmationLetterResponseListener = BankConfirmationLetterResponseListener(testSubject)
        val bankConfirmationLetterResponse = BankConfirmationLetterResponse()
        val bclDocument = "sdsfgdnbgfd56tyue3fWD323FWE"
        val accountNumber = "4045454911"
        val successMessage = "Success"

        Mockito.`when`(bankConfirmationLetterMockService.fetchConfirmationLetter(accountNumber, bankConfirmationLetterResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<BankConfirmationLetterResponseListener>(1).onSuccess(bankConfirmationLetterResponse.apply {
                this.bclDocument = bclDocument
                this.transactionStatus = successMessage
                this.transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<BankConfirmationLetterResponse>>()
        bankConfirmationLetterViewModelSpy.bankConfirmationLetterResponse.observeForever(observer)

        bankConfirmationLetterMockService.fetchConfirmationLetter(accountNumber, bankConfirmationLetterResponseListener)
        verify(bankConfirmationLetterMockService).fetchConfirmationLetter(any(), capture(bankConfirmationLetterCaptor))

        Assert.assertEquals(bankConfirmationLetterCaptor.value, bankConfirmationLetterResponseListener)
        Assert.assertEquals(bankConfirmationLetterViewModelSpy.bankConfirmationLetterResponse.value?.transactionStatus, successMessage)
        Assert.assertEquals(bankConfirmationLetterViewModelSpy.bankConfirmationLetterResponse.value?.transactionMessage, successMessage)
        Assert.assertNotNull(bankConfirmationLetterViewModelSpy.bankConfirmationLetterResponse.value?.bclDocument)
        Assert.assertEquals(bankConfirmationLetterViewModelSpy.bankConfirmationLetterResponse.value?.bclDocument, bclDocument)

        verifyNoMoreInteractions(bankConfirmationLetterMockService)
    }

    @Test
    fun shouldCallFetchBankConfirmationLetterInteractorWhenFetchBankConfirmationLetterFunctionIsCalled() {
        val accountNumber = "4045454911"

        testSubject.fetchBankConfirmationLetter(accountNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BankConfirmationLetterResponse>>(testSubject, "bankConfirmationLetterResponseListener")
        verify(bankConfirmationLetterMockService).fetchConfirmationLetter(accountNumber, extendedResponseListenerReflection)
        verifyNoMoreInteractions(bankConfirmationLetterMockService)
    }
}