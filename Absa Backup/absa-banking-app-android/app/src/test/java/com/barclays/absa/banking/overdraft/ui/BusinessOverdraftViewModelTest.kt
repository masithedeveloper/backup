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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.overdraft.services.ApplyBusinessOverdraftResponse
import com.barclays.absa.banking.overdraft.services.ApplyBusinessOverdraftResponseListener
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor
import com.barclays.absa.banking.overdraft.ui.BusinessOverdraftViewModel
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class BusinessOverdraftViewModelTest {

    private var testSubject = BusinessOverdraftViewModel()
    private var overdraftMockService = mock<OverdraftInteractor>()

    @Captor
    private lateinit var applyBusinessOverdraftCaptor: ArgumentCaptor<ApplyBusinessOverdraftResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.interactor = overdraftMockService
    }

    @Test
    fun shouldReturnApplyBusinessOverdraftResponseSuccessWhenServiceCallComplete() {
        val overdraftViewModelSpy = spy(testSubject)
        val applyBusinessOverdraftResponseListener = ApplyBusinessOverdraftResponseListener(testSubject)
        val applyBusinessOverdraftResponse = ApplyBusinessOverdraftResponse()
        val offersBusinessBankProduct = "OFFERS_BB_NEW_OVERDRAFT"
        val existingOverdraftLimit = "0"
        val newOverdraftLimit = "0.0"
        val successMessage = "Success"

        Mockito.`when`(overdraftMockService.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, applyBusinessOverdraftResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ApplyBusinessOverdraftResponseListener>(3).onSuccess(applyBusinessOverdraftResponse.apply {
                this.bcmsReferenceNumberForBB = offersBusinessBankProduct
                this.transactionStatus = successMessage
                this.transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<ApplyBusinessOverdraftResponse>>()
        overdraftViewModelSpy.applyBusinessOverdraftResponse.observeForever(observer)

        overdraftMockService.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, applyBusinessOverdraftResponseListener)
        verify(overdraftMockService).applyBusinessOverdraft(any(), any(), any(), capture(applyBusinessOverdraftCaptor))

        Assert.assertEquals(applyBusinessOverdraftCaptor.value, applyBusinessOverdraftResponseListener)
        Assert.assertEquals(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.transactionStatus, successMessage)
        Assert.assertEquals(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.transactionMessage, successMessage)
        Assert.assertNotNull(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.bcmsReferenceNumberForBB)

        verifyNoMoreInteractions(overdraftMockService)
    }

    @Test
    fun shouldReturnApplyBusinessOverdraftResponseFailureWhenServiceCallComplete() {
        val overdraftViewModelSpy = spy(testSubject)
        val applyBusinessOverdraftResponseListener = ApplyBusinessOverdraftResponseListener(testSubject)
        val applyBusinessOverdraftResponse = ApplyBusinessOverdraftResponse()
        val offersBusinessBankProduct = "OFFERS_BB_NEW_OVERDRAFT"
        val existingOverdraftLimit = "0"
        val newOverdraftLimit = "0.0"
        val failureMessage = "Failure"

        Mockito.`when`(overdraftMockService.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, applyBusinessOverdraftResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ApplyBusinessOverdraftResponseListener>(3).onSuccess(applyBusinessOverdraftResponse.apply {
                this.bcmsReferenceNumberForBB = ""
                this.transactionStatus = failureMessage
                this.transactionMessage = failureMessage
            })
        }

        val observer = mock<Observer<ApplyBusinessOverdraftResponse>>()
        overdraftViewModelSpy.applyBusinessOverdraftResponse.observeForever(observer)

        overdraftMockService.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, applyBusinessOverdraftResponseListener)
        verify(overdraftMockService).applyBusinessOverdraft(any(), any(), any(), capture(applyBusinessOverdraftCaptor))

        Assert.assertEquals(applyBusinessOverdraftCaptor.value, applyBusinessOverdraftResponseListener)
        Assert.assertEquals(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.transactionStatus, failureMessage)
        Assert.assertEquals(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.transactionMessage, failureMessage)
        Assert.assertNotNull(overdraftViewModelSpy.applyBusinessOverdraftResponse.value?.bcmsReferenceNumberForBB)

        verifyNoMoreInteractions(overdraftMockService)
    }

    @Test
    fun shouldCallApplyBusinessOverdraftInteractorWhenApplyBusinessOverdraftFunctionIsCalled() {
        val offersBusinessBankProduct = "OFFERS_BB_NEW_OVERDRAFT"
        val existingOverdraftLimit = "0"
        val newOverdraftLimit = "0.0"

        testSubject.applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<ApplyBusinessOverdraftResponse>>(testSubject, "applyBusinessOverdraftResponseListener")
        verify(overdraftMockService).applyBusinessOverdraft(offersBusinessBankProduct, existingOverdraftLimit, newOverdraftLimit, extendedResponseListenerReflection)
        verifyNoMoreInteractions(overdraftMockService)
    }
}