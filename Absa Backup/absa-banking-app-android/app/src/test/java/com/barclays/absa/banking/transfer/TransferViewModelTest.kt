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
package com.barclays.absa.banking.transfer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.rewards.*
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.rewards.services.RewardsInteractor
import com.barclays.absa.banking.transfer.responseListeners.AccountListExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RedeemRewardsExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RewardsRedeemConfirmationExtendedResponseListener
import com.barclays.absa.banking.transfer.responseListeners.RewardsRedeemResultExtendedResponseListener
import com.barclays.absa.banking.transfer.services.TransferFundsInteractor
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import styleguide.forms.SelectorList

internal class TransferViewModelTest : DaggerTest() {
    val testSubject = TransferViewModel()
    private val accountMockService = mock<AccountInteractor>()
    private val transferFundsMockService = mock<TransferFundsInteractor>()
    private val rewardsMockService = mock<RewardsInteractor>()

    @Captor
    private lateinit var accountListCaptor: ArgumentCaptor<AccountListExtendedResponseListener>

    @Captor
    private lateinit var redeemRewardsCaptor: ArgumentCaptor<RedeemRewardsExtendedResponseListener>

    @Captor
    private lateinit var rewardsRedeemConfirmationCaptor: ArgumentCaptor<RewardsRedeemConfirmationExtendedResponseListener>

    @Captor
    private lateinit var rewardsRedeemResultCaptor: ArgumentCaptor<RewardsRedeemResultExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.accountInteractor = accountMockService
        testSubject.transferFundsService = transferFundsMockService
        testSubject.rewardsInteractor = rewardsMockService
    }

    @Test
    fun shouldCallFetchExpressAccountsInteractorWhenFetchAccountListFunctionIsCalled() {
        testSubject.fetchAccountList(TransferType.INTERACCOUNT_TRANSFER)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<AccountListExtendedResponseListener>(testSubject, "accountListExtendedResponseListener")
        verify(accountMockService).fetchExpressAccounts(TransferType.INTERACCOUNT_TRANSFER, extendedResponseListenerReflection)
        verifyNoMoreInteractions(accountMockService)
    }

    @Test
    fun shouldCallFetchRewardsRedeemDataInteractorWhenFetchRewardsToAccountListFunctionIsCalled() {
        testSubject.fetchRewardsToAccountList()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<RedeemRewardsExtendedResponseListener>(testSubject, "redeemRewardsExtendedResponseListener")
        verify(rewardsMockService).fetchRewardsRedeemData(extendedResponseListenerReflection)
        verifyNoMoreInteractions(rewardsMockService)
    }

    @Test
    fun shouldCallValidateRewardsRedemptionInteractorWhenValidateRewardsRedemptionFunctionIsCalled() {
        testSubject.validateRewardsRedemption("")
        verify(transferFundsMockService).validateRewardsRedemption(any(), any())
        verifyNoMoreInteractions(transferFundsMockService)
    }

    @Test
    fun shouldCallSendTransferRequestInteractorWhenRewardsTransferConfirmedFunctionIsCalled() {
        val rewardsRedeemConfirmation = RewardsRedeemConfirmation()

        testSubject.rewardsTransferConfirmed(rewardsRedeemConfirmation)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<RewardsRedeemResultExtendedResponseListener>(testSubject, "rewardsRedeemResultExtendedResponseListener")
        verify(transferFundsMockService).sendTransferRequest(rewardsRedeemConfirmation.txnReferenceID.toString(), extendedResponseListenerReflection)
        verifyNoMoreInteractions(transferFundsMockService)
    }

    @Test
    fun shouldCallSendTransferRequestInteractorWhenSureCheckConfirmedFunctionIsCalled() {
        val rewardsRedeemConfirmation = RewardsRedeemConfirmation()

        testSubject.sureCheckConfirmed()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<RewardsRedeemResultExtendedResponseListener>(testSubject, "rewardsRedeemResultExtendedResponseListener")
        verify(transferFundsMockService).sendTransferRequest(rewardsRedeemConfirmation.txnReferenceID.toString(), extendedResponseListenerReflection)
        verifyNoMoreInteractions(transferFundsMockService)
    }

    @Test
    fun shouldReturnFetchExpressAccountsWhenServiceCallComplete() {
        val transferViewModelSpy = spy(testSubject)
        val transferResponseListener = AccountListExtendedResponseListener(testSubject)
        val transferResponse = AccountList()

        Mockito.`when`(accountMockService.fetchExpressAccounts(TransferType.INTERACCOUNT_TRANSFER, transferResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<AccountListExtendedResponseListener>(1).onSuccess(transferResponse.apply {
                balNotYetClrdDt = ""
                accountsList = arrayListOf(AccountObject())
                fromAccountList = arrayListOf(AccountObject())
                toAccountList = arrayListOf(AccountObject())
                customerAccounts = arrayListOf(AccountObject())
                transferViewModelSpy.baseAccountListLoaded(this)
            })
        }

        val observer = mock<Observer<SelectorList<AccountListItem>>>()
        transferViewModelSpy.fromAccountItemList.observeForever(observer)

        accountMockService.fetchExpressAccounts(TransferType.INTERACCOUNT_TRANSFER, transferResponseListener)
        verify(accountMockService).fetchExpressAccounts(any(), capture(accountListCaptor))

        Assert.assertEquals(accountListCaptor.value, transferResponseListener)
        Assert.assertNotNull(transferViewModelSpy.fromAccountItemList.value)
        verifyNoMoreInteractions(accountMockService)
    }

    @Test
    fun shouldReturnFetchRewardsRedeemDataWhenServiceCallComplete() {
        val transferViewModelSpy = spy(testSubject)
        val transferResponseListener = RedeemRewardsExtendedResponseListener(testSubject)
        val transferResponse = RedeemRewards()

        Mockito.`when`(rewardsMockService.fetchRewardsRedeemData(transferResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RedeemRewardsExtendedResponseListener>(0).onSuccess(transferResponse.apply {
                transferViewModelSpy.populateRewardsRedemptionData(this)
            })
        }

        val observer = mock<Observer<List<RewardsAccountDetails>>>()
        transferViewModelSpy.rewardsAccountListLiveData.observeForever(observer)

        rewardsMockService.fetchRewardsRedeemData(transferResponseListener)
        verify(rewardsMockService).fetchRewardsRedeemData(capture(redeemRewardsCaptor))

        Assert.assertEquals(redeemRewardsCaptor.value, transferResponseListener)
        verifyNoMoreInteractions(rewardsMockService)
    }

    @Test
    fun shouldReturnValidateRewardsRedemptionWhenServiceCallComplete() {
        val transferViewModelSpy = spy(testSubject)
        val transferResponseListener = RewardsRedeemConfirmationExtendedResponseListener(testSubject)
        val transferResponse = RewardsRedeemConfirmation()
        val redeemRewardsCash = RedeemRewardsCash()

        Mockito.`when`(transferFundsMockService.validateRewardsRedemption(redeemRewardsCash, transferResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RewardsRedeemConfirmationExtendedResponseListener>(1).onSuccess(transferResponse)
        }

        val observer = mock<Observer<RewardsRedeemConfirmation>>()
        transferViewModelSpy.rewardsRedeemConfirmationLiveData.observeForever(observer)

        transferFundsMockService.validateRewardsRedemption(redeemRewardsCash, transferResponseListener)
        verify(transferFundsMockService).validateRewardsRedemption(any(), capture(rewardsRedeemConfirmationCaptor))

        Assert.assertEquals(rewardsRedeemConfirmationCaptor.value, transferResponseListener)
        Assert.assertNotNull(transferViewModelSpy.rewardsRedeemConfirmationLiveData.value)
        verifyNoMoreInteractions(transferFundsMockService)
    }

    @Test
    fun shouldReturnSendTransferRequestWhenServiceCallComplete() {
        val transferViewModelSpy = spy(testSubject)
        val transferResponseListener = RewardsRedeemResultExtendedResponseListener(testSubject)
        val transferResponse = RewardsRedeemResult()
        val rewardsRedeemConfirmation = RewardsRedeemConfirmation()

        Mockito.`when`(transferFundsMockService.sendTransferRequest(rewardsRedeemConfirmation.txnReferenceID.toString(), transferResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<RewardsRedeemResultExtendedResponseListener>(1).onSuccess(transferResponse.apply {
                membershipNo = ""
                redemptionCode = ""
                registrationMessage = ""
                registrationStatus = ""
                transactionReferenceNumber = ""
            })
        }

        val observer = mock<Observer<RewardsRedeemResult>>()
        transferViewModelSpy.rewardsRedeemResultLiveData.observeForever(observer)

        transferFundsMockService.sendTransferRequest(rewardsRedeemConfirmation.txnReferenceID.toString(), transferResponseListener)
        verify(transferFundsMockService).sendTransferRequest(any(), capture(rewardsRedeemResultCaptor))

        Assert.assertEquals(rewardsRedeemResultCaptor.value, transferResponseListener)
        Assert.assertNotNull(transferViewModelSpy.rewardsRedeemResultLiveData.value)
        verifyNoMoreInteractions(transferFundsMockService)
    }
}