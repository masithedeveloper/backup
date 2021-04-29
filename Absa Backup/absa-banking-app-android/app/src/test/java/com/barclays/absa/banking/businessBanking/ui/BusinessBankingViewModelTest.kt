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
package com.barclays.absa.banking.businessBanking.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.newToBank.services.NewToBankInteractor
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveOptionalExtrasResponse
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class BusinessBankingViewModelTest {

    private var businessBankingViewModel = BusinessBankingViewModel()
    private var newToBankInteractor = mock<NewToBankInteractor>()

    @Captor
    private lateinit var businessEvolveIslamicCaptor: ArgumentCaptor<BusinessEvolveIslamicExtendedResponseListener>

    @Captor
    private lateinit var businessEvolveStandardCaptor: ArgumentCaptor<BusinessEvolveStandardExtendedResponseListener>

    @Captor
    private lateinit var businessEvolveOptionalExtrasCaptor: ArgumentCaptor<BusinessEvolveOptionalExtrasExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        businessBankingViewModel.newToBankService = newToBankInteractor
    }

    @Test
    fun shouldCallFetchBusinessEvolveIslamicAccountAndFetchBusinessEvolveStandardInteractorWhenFetchBusinessEvolvePackagesFunctionIsCalled() {
        businessBankingViewModel.fetchBusinessEvolvePackages()
        val reflectedBusinessEvolveIslamicListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BusinessEvolveCardPackageResponse>>(businessBankingViewModel, "businessEvolveIslamicExtendedResponseListener")
        val reflectedBusinessEvolveStandardListener = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BusinessEvolveCardPackageResponse>>(businessBankingViewModel, "businessEvolveStandardExtendedResponseListener")

        verify(newToBankInteractor).fetchBusinessEvolveIslamicAccount(reflectedBusinessEvolveIslamicListener)
        verify(newToBankInteractor).fetchBusinessEvolveStandardAccount(reflectedBusinessEvolveStandardListener)
        verifyNoMoreInteractions(newToBankInteractor)
    }

    @Test
    fun shouldCallInteractorFetchBusinessEvolveOptionalExtrasWhenFetchBusinessEvolveOptionalExtrasPackageFunctionIsCalled() {
        businessBankingViewModel.fetchBusinessEvolveOptionalExtrasPackage()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BusinessEvolveOptionalExtrasResponse>>(businessBankingViewModel, "businessEvolveOptionalExtrasExtendedResponseListener")
        verify(newToBankInteractor).fetchBusinessEvolveOptionalExtras(extendedResponseListenerReflection)
        verifyNoMoreInteractions(newToBankInteractor)
    }

    @Test
    fun shouldFetchBusinessEvolveIslamicPackageWhenServiceCallIsComplete() {
        val businessEvolveIslamicExtendedResponseListener = BusinessEvolveIslamicExtendedResponseListener(businessBankingViewModel)

        newToBankInteractor.fetchBusinessEvolveIslamicAccount(businessEvolveIslamicExtendedResponseListener)
        verify(newToBankInteractor).fetchBusinessEvolveIslamicAccount(capture(businessEvolveIslamicCaptor))
        Assert.assertEquals(businessEvolveIslamicCaptor.value, businessEvolveIslamicExtendedResponseListener)
    }

    @Test
    fun shouldFetchBusinessEvolveStandardPackageWhenServiceCallIsComplete() {
        val businessEvolveIslamicExtendedResponseListener = BusinessEvolveStandardExtendedResponseListener(businessBankingViewModel)

        newToBankInteractor.fetchBusinessEvolveStandardAccount(businessEvolveIslamicExtendedResponseListener)
        verify(newToBankInteractor).fetchBusinessEvolveStandardAccount(capture(businessEvolveStandardCaptor))
        Assert.assertEquals(businessEvolveStandardCaptor.value, businessEvolveIslamicExtendedResponseListener)
    }

    @Test
    fun shoulReturnBusinessEvolveOptionalExtrasPackageWhenServiceCallIsComplete() {
        val businessBankingViewModelSpy = spy(businessBankingViewModel)
        val businessEvolveOptionalExtrasExtendedResponseListener = BusinessEvolveOptionalExtrasExtendedResponseListener(businessBankingViewModel)
        val businessEvolveOptionalExtrasResponse = BusinessEvolveOptionalExtrasResponse()
        val optionsExtras = mutableListOf<BusinessEvolveOptionalExtrasResponse.OptionalExtras>()

        Mockito.`when`(newToBankInteractor.fetchBusinessEvolveOptionalExtras(businessEvolveOptionalExtrasExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<BusinessEvolveOptionalExtrasExtendedResponseListener>(0).onSuccess(businessEvolveOptionalExtrasResponse.apply {
                this.footerText = ""
                this.headerText = ""
                this.optionsExtras = optionsExtras
            })
        }

        val observer = mock<Observer<BusinessEvolveOptionalExtrasResponse>>()
        businessBankingViewModelSpy.businessEvolveOptionalExtrasMutableLiveData.observeForever(observer)

        newToBankInteractor.fetchBusinessEvolveOptionalExtras(businessEvolveOptionalExtrasExtendedResponseListener)
        verify(newToBankInteractor).fetchBusinessEvolveOptionalExtras(capture(businessEvolveOptionalExtrasCaptor))

        Assert.assertEquals(businessEvolveOptionalExtrasCaptor.value, businessEvolveOptionalExtrasExtendedResponseListener)
        Assert.assertNotNull(businessBankingViewModelSpy.businessEvolveOptionalExtrasMutableLiveData.value)
        Assert.assertNotNull(businessBankingViewModelSpy.businessEvolveOptionalExtrasMutableLiveData.value?.headerText)
        Assert.assertNotNull(businessBankingViewModelSpy.businessEvolveOptionalExtrasMutableLiveData.value?.footerText)
        Assert.assertNotNull(businessBankingViewModelSpy.businessEvolveOptionalExtrasMutableLiveData.value?.optionsExtras)
        verifyNoMoreInteractions(newToBankInteractor)
    }
}