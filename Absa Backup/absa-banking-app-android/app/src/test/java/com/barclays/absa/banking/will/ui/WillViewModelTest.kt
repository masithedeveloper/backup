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
package com.barclays.absa.banking.will.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.will.services.WillInteractor
import com.barclays.absa.banking.will.services.dto.PortfolioInfoResponse
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackInfo
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackResponse
import com.barclays.absa.banking.will.services.dto.WillResponse
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class WillViewModelTest {
    private var willViewModel = WillViewModel()
    private var willInteractor = mock<WillInteractor>()

    @Captor
    private lateinit var willCaptor: ArgumentCaptor<WillExtendedResponseListener>

    @Captor
    private lateinit var portfolioCaptor: ArgumentCaptor<PortfolioInfoExtendedResponseListener>

    @Captor
    private lateinit var saveCallMeBackCaptor: ArgumentCaptor<SaveCallMeBackExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        willViewModel.willInteractor = willInteractor
    }

    @Test
    fun shouldCallInteractorFetchWillWhenFetchWillMethodIsCalled() {
        val isPdfRequired = true
        willViewModel.fetchWill(isPdfRequired)
        val reflectedWillListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<WillResponse>>(willViewModel, "willExtendedResponseListener")
        verify(willInteractor).fetchWill(isPdfRequired, reflectedWillListener)
        verifyNoMoreInteractions(willInteractor)
    }

    @Test
    fun shouldCallInteractorFetchPortfolioInfoWhenFetchPortfolioInfoMethodIsCalled() {
        willViewModel.fetchPortfolioInfo()
        val reflectedPortfolioListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<PortfolioInfoResponse>>(willViewModel, "portfolioExtendedResponseListener")
        verify(willInteractor).fetchPortfolioInfo(reflectedPortfolioListener)
        verifyNoMoreInteractions(willInteractor)
    }

    @Test
    fun shouldCallInteractorSaveCallMeBackWhenSaveCallMeBackMethodIsCalled() {
        val saveCallMeBackInfo = SaveCallMeBackInfo()
        willViewModel.saveCallMeBack(saveCallMeBackInfo)
        val reflectedSaveCallMeBackListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<SaveCallMeBackResponse>>(willViewModel, "saveCallMeBackExtendedReponseListener")
        verify(willInteractor).saveCallMeBack(saveCallMeBackInfo, reflectedSaveCallMeBackListener)
        verifyNoMoreInteractions(willInteractor)
    }

    @Test
    fun shouldFetchWillWhenServiceCallIsComplete() {
        val willViewModelSpy = spy(willViewModel)
        val willExtendedResponseListener = WillExtendedResponseListener(willViewModel)
        val willResponseListener = WillResponse()
        val isPdfRequired = true
        val accountNumber = "2347658887"
        val successMessage = "Success"

        Mockito.`when`(willInteractor.fetchWill(isPdfRequired, willExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<WillExtendedResponseListener>(1).onSuccess(willResponseListener.apply {
                this.willAccountNumber = accountNumber
                this.txnStatus = successMessage
                this.transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<WillResponse>>()
        willViewModelSpy.willExtendedResponse.observeForever(observer)

        willInteractor.fetchWill(isPdfRequired, willExtendedResponseListener)
        verify(willInteractor).fetchWill(any(), capture(willCaptor))

        Assert.assertEquals(willCaptor.value, willExtendedResponseListener)
        Assert.assertEquals(willViewModelSpy.willExtendedResponse.value?.willAccountNumber, accountNumber)
        Assert.assertEquals(willViewModelSpy.willExtendedResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(willViewModelSpy.willExtendedResponse.value?.txnStatus, successMessage)

        verifyNoMoreInteractions(willInteractor)
    }

    @Test
    fun shouldFetchPortfolioInfoWhenServiceCallIsComplete() {
        val willViewModelSpy = spy(willViewModel)
        val portfolioInfoExtendedResponseListener = PortfolioInfoExtendedResponseListener(willViewModel)
        val portfolioInfoResponseListener = PortfolioInfoResponse()

        Mockito.`when`(willInteractor.fetchPortfolioInfo(portfolioInfoExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PortfolioInfoExtendedResponseListener>(0).onSuccess(portfolioInfoResponseListener.apply {
                this.transactionReferenceID = ""
                this.cellNumber = "0678943332"
            })
        }

        val observer = mock<Observer<PortfolioInfoResponse>>()
        willViewModelSpy.portfolioExtendedResponse.observeForever(observer)

        willInteractor.fetchPortfolioInfo(portfolioInfoExtendedResponseListener)
        verify(willInteractor).fetchPortfolioInfo(capture(portfolioCaptor))

        Assert.assertEquals(portfolioCaptor.value, portfolioInfoExtendedResponseListener)
        Assert.assertNotNull(willViewModelSpy.portfolioExtendedResponse.value)

        verifyNoMoreInteractions(willInteractor)
    }

    @Test
    fun shouldSaveCallMeBackWhenServiceCallIsComplete() {
        val willViewModelSpy = spy(willViewModel)
        val saveCallMeBackExtendedResponseListener = SaveCallMeBackExtendedResponseListener(willViewModel)
        val saveCallMeBackResponseListener = SaveCallMeBackResponse()
        val saveCallMeBackInfo = SaveCallMeBackInfo()

        Mockito.`when`(willInteractor.saveCallMeBack(saveCallMeBackInfo, saveCallMeBackExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<SaveCallMeBackExtendedResponseListener>(1).onSuccess(saveCallMeBackResponseListener)
        }

        val observer = mock<Observer<SaveCallMeBackResponse>>()
        willViewModelSpy.saveCallMeBackResponse.observeForever(observer)

        willInteractor.saveCallMeBack(saveCallMeBackInfo, saveCallMeBackExtendedResponseListener)
        verify(willInteractor).saveCallMeBack(any(), capture(saveCallMeBackCaptor))

        Assert.assertEquals(saveCallMeBackCaptor.value, saveCallMeBackExtendedResponseListener)

        verifyNoMoreInteractions(willInteractor)
    }
}