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

package com.barclays.absa.banking.fixedDeposit.responseListeners

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.BankBranches
import com.barclays.absa.banking.boundary.model.BankDetails
import com.barclays.absa.banking.boundary.model.Branch
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.fixedDeposit.FixedDepositData
import com.barclays.absa.banking.fixedDeposit.FixedDepositPayoutDetailsData
import com.barclays.absa.banking.fixedDeposit.FixedDepositRenewalInstructionData
import com.barclays.absa.banking.fixedDeposit.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.model.FicaCheckResponse
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor
import com.barclays.absa.banking.payments.services.PaymentsInteractor
import com.barclays.absa.banking.shared.responseListeners.FetchClientAgreementDetailsExtendedResponseListener
import com.barclays.absa.banking.shared.responseListeners.UpdatePersonalClientAgreementExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class FixedDepositViewModelTest : DaggerTest() {

    private var testSubject = FixedDepositViewModel()
    private var fixedDepositMockService = mock<FixedDepositInteractor>()
    private var paymentsMockService = mock<PaymentsInteractor>()
    private var overdraftMockService = mock<OverdraftInteractor>()
    private var beneficiariesMockService = mock<BeneficiariesInteractor>()
    private var renewalInstructionData = FixedDepositRenewalInstructionData()

    @Captor
    private lateinit var fetchInterestRateInfoCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositInterestRateInfoResponse>>

    @Captor
    private lateinit var createAccountCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositCreateAccountResponse>>

    @Captor
    private lateinit var confirmAccountCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositCreateAccountConfirmResponse>>

    @Captor
    private lateinit var processAccountCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositCreateAccountProcessResponse>>

    @Captor
    private lateinit var fetchAccountDetailsCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositAccountDetailsResponse>>

    @Captor
    private lateinit var updateAccountDetailsCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositUpdateAccountDetailsResponse>>

    @Captor
    private lateinit var fetchRenewalInstructionCaptor: ArgumentCaptor<ExtendedResponseListener<FixedDepositRenewalInstructionResponse>>

    @Captor
    private lateinit var confirmRenewalInstructionCaptor: ArgumentCaptor<FixedDepositConfirmRenewalInstructionExtendedResponseListener>

    @Captor
    private lateinit var createRenewalInstructionCaptor: ArgumentCaptor<FixedDepositCreateRenewalInstructionExtendedResponseListener>

    @Captor
    private lateinit var bankListCaptor: ArgumentCaptor<FixedDepositBankListExtendedResponseListener>

    @Captor
    private lateinit var branchListCaptor: ArgumentCaptor<FixedDepositBranchListExtendedResponseListener>

    @Captor
    private lateinit var ficaCheckCaptor: ArgumentCaptor<FixedDepositFicaCheckExtendedResponseListener>

    @Captor
    private lateinit var personalClientAgreementCaptor: ArgumentCaptor<UpdatePersonalClientAgreementExtendedResponseListener>

    @Captor
    private lateinit var clientAgreementDetailsCaptor: ArgumentCaptor<FetchClientAgreementDetailsExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        with(testSubject) {
            fixedDepositInteractor = fixedDepositMockService
            paymentsInteractor = paymentsMockService
            overdraftInteractor = overdraftMockService
            beneficiariesInteractor = beneficiariesMockService
        }
    }

    @Test
    fun shouldCallFetchInterestRateInfoFixedDepositInteractorWhenFetchInterestRateInfoFunctionIsCalled() {
        testSubject.fetchInterestRateInfo()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositInterestRateInfoResponse>>(testSubject, "interestRateInfoResponseListener")
        verify(fixedDepositMockService).fetchInterestRateInfo(extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallConfirmAccountFixedDepositInteractorWhenConfirmAccountFunctionIsCalled() {
        testSubject.confirmAccount()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositCreateAccountConfirmResponse>>(testSubject, "createAccountConfirmExtendedResponseListener")
        verify(fixedDepositMockService).createAccountConfirm(extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallCreateAccountFixedDepositInteractorWhenCreateAccountFunctionIsCalled() {
        testSubject.createAccount()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositCreateAccountResponse>>(testSubject, "createAccountExtendedResponseListener")
        verify(fixedDepositMockService).createAccount(extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallProcessAccountFixedDepositInteractorWhenProcessAccountFunctionIsCalled() {
        val fixedDepositData = FixedDepositData()
        val sureCheckAccepted = true

        testSubject.processAccount(fixedDepositData, sureCheckAccepted)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositCreateAccountProcessResponse>>(testSubject, "createAccountProcessExtendedResponseListener")
        verify(fixedDepositMockService).createAccountProcess(fixedDepositData, sureCheckAccepted, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallFetchAccountDetailsFixedDepositInteractorWhenFetchAccountDetailsFunctionIsCalled() {
        val accountNumber = "3001432596"

        testSubject.fetchAccountDetails(accountNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositAccountDetailsResponse>>(testSubject, "accountDetailResponseListener")
        verify(fixedDepositMockService).fetchAccountDetail(accountNumber, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallFetchRenewalInstructionFixedDepositInteractorWhenFetchRenewalInstructionFunctionIsCalled() {
        val accountNumber = "3001432596"

        testSubject.fetchRenewalInstruction(accountNumber)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositRenewalInstructionResponse>>(testSubject, "renewalInstructionExtendedResponseListener")
        verify(fixedDepositMockService).requestRenewalInstruction(accountNumber, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallConfirmRenewalInstructionFixedDepositInteractorWhenConfirmRenewalInstructionFunctionIsCalled() {
        testSubject.confirmRenewalInstruction()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<FixedDepositConfirmRenewalInstructionExtendedResponseListener>(testSubject, "confirmRenewalInstructionExtendedResponseListener")
        verify(fixedDepositMockService).requestRenewalInstructionConfirmation(renewalInstructionData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallCreateRenewalInstructionFixedDepositInteractorWhenCreateRenewalInstructionFunctionIsCalled() {
        testSubject.createRenewalInstruction()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<FixedDepositCreateRenewalInstructionExtendedResponseListener>(testSubject, "createRenewalInstructionExtendedResponseListener")
        verify(fixedDepositMockService).createRenewalInstruction(renewalInstructionData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallUpdateAccountDetailsFixedDepositInteractorWhenUpdateAccountDetailsFunctionIsCalled() {
        val fixedDepositPayoutDetailsData = FixedDepositPayoutDetailsData()

        testSubject.updateAccountDetails()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FixedDepositUpdateAccountDetailsResponse>>(testSubject, "updateAccountExtendedResponseListener")
        verify(fixedDepositMockService).requestAccountUpdate(fixedDepositPayoutDetailsData, extendedResponseListenerReflection)
        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldCallFetchBankListWhenFetchBankListFunctionIsCalled() {
        testSubject.fetchBankList()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BankDetails>>(testSubject, "bankListExtendedResponseListener")
        verify(paymentsMockService).fetchBankList(extendedResponseListenerReflection)
        verifyNoMoreInteractions(paymentsMockService)
    }

    @Test
    fun shouldCallFetchBranchListWhenFetchBranchListFunctionIsCalled() {
        val bankName = "Absa"

        testSubject.fetchBranchList(bankName)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<BankBranches>>(testSubject, "branchListExtendedResponseListener")
        verify(paymentsMockService).fetchBranchList(bankName, extendedResponseListenerReflection)
        verifyNoMoreInteractions(paymentsMockService)
    }

    @Test
    fun shouldCallFetchFICAAndCIFStatusWhenPerformFicaCheckFunctionIsCalled() {
        testSubject.performFicaCheck()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<FicaCheckResponse>>(testSubject, "ficaCheckExtendedResponseListener")
        verify(overdraftMockService).fetchFICAAndCIFStatus(extendedResponseListenerReflection)
        verifyNoMoreInteractions(overdraftMockService)
    }

    @Test
    fun shouldCallUpdateClientAgreementDetailsWhenUpdatePersonalClientAgreementFunctionIsCalled() {
        testSubject.updatePersonalClientAgreement()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<TransactionResponse>>(testSubject, "personalClientAgreementExtendedResponseListener")
        verify(beneficiariesMockService).updateClientAgreementDetails(extendedResponseListenerReflection)
        verifyNoMoreInteractions(beneficiariesMockService)
    }

    @Test
    fun shouldCallFetchClientAgreementDetailsWhenFetchPersonalClientAgreementDetailsFunctionIsCalled() {
        testSubject.fetchPersonalClientAgreementDetails()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<ClientAgreementDetails>>(testSubject, "fetchClientAgreementDetailsExtendedResponseListener")
        verify(beneficiariesMockService).fetchClientAgreementDetails(extendedResponseListenerReflection)
        verifyNoMoreInteractions(beneficiariesMockService)
    }

    @Test
    fun shouldReturnInterestRateInfoWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val interestRateInfoResponseListener = FixedDepositInterestRateInfoExtendedResponseListener(testSubject)
        val interestRateInfoResponse = FixedDepositInterestRateInfoResponse()
        val categoryCode = "aoctd001"
        val checksum = "D9C0F55F245F0E7CFFA48054598DFD0D"
        val currentDate = "20200712"
        val categoryName = "Term Deposit"
        val effectiveDate = "20181210"
        val interestRateTable = emptyList<InterestRateTable>()
        val productCode = "03040"
        val shortName = "Fixed Deposit"

        Mockito.`when`(fixedDepositMockService.fetchInterestRateInfo(interestRateInfoResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositInterestRateInfoExtendedResponseListener>(0).onSuccess(interestRateInfoResponse.apply {
                this.categoryCode = categoryCode
                this.categoryName = categoryName
                this.checksum = checksum
                this.currentDate = currentDate
                this.effectiveDate = effectiveDate
                this.interestRateTable = interestRateTable
                this.productCode = productCode
                this.shortName = shortName
            })
        }

        val observer = mock<Observer<FixedDepositInterestRateInfoResponse>>()
        fixedDepositViewModelSpy.interestRateInfo.observeForever(observer)

        fixedDepositMockService.fetchInterestRateInfo(interestRateInfoResponseListener)
        verify(fixedDepositMockService).fetchInterestRateInfo(capture(fetchInterestRateInfoCaptor))

        Assert.assertEquals(interestRateInfoResponseListener, fetchInterestRateInfoCaptor.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.categoryCode)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.categoryName)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.checksum)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.currentDate)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.effectiveDate)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.interestRateTable)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.shortName)
        Assert.assertNotNull(fixedDepositViewModelSpy.interestRateInfo.value?.productCode)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnCreateAccountWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val createAccountExtendedResponseListener = FixedDepositCreateAccountExtendedResponseListener(testSubject)
        val createAccountExtendedResponse = FixedDepositCreateAccountResponse()
        val minimumDeposit = "R1 000"

        Mockito.`when`(fixedDepositMockService.createAccount(createAccountExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositCreateAccountExtendedResponseListener>(0).onSuccess(createAccountExtendedResponse.apply {
                this.minimumDeposit = minimumDeposit
            })
        }

        val observer = mock<Observer<FixedDepositCreateAccountResponse>>()
        fixedDepositViewModelSpy.createAccountResponse.observeForever(observer)

        fixedDepositMockService.createAccount(createAccountExtendedResponseListener)
        verify(fixedDepositMockService).createAccount(capture(createAccountCaptor))

        Assert.assertEquals(createAccountCaptor.value, createAccountExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountResponse.value?.minimumDeposit)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnConfirmAccountWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val createAccountConfirmExtendedResponseListener = FixedDepositCreateAccountConfirmExtendedResponseListener(testSubject)
        val createAccountConfirmResponse = FixedDepositCreateAccountConfirmResponse()
        val accountTypes = listOf<AccountTypes>()

        Mockito.`when`(fixedDepositMockService.createAccountConfirm(createAccountConfirmExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositCreateAccountConfirmExtendedResponseListener>(0).onSuccess(createAccountConfirmResponse.apply {
                this.accountTypes = accountTypes
            })
        }

        val observer = mock<Observer<FixedDepositCreateAccountConfirmResponse>>()
        fixedDepositViewModelSpy.createAccountConfirmResponse.observeForever(observer)

        fixedDepositMockService.createAccountConfirm(createAccountConfirmExtendedResponseListener)
        verify(fixedDepositMockService).createAccountConfirm(capture(confirmAccountCaptor))

        Assert.assertEquals(confirmAccountCaptor.value, createAccountConfirmExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountConfirmResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountConfirmResponse.value?.accountTypes)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnProcessAccountWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val createAccountProcessExtendedResponseListener = FixedDepositCreateAccountProcessExtendedResponseListener(testSubject)
        val fixedDepositCreateAccountProcessResponse = FixedDepositCreateAccountProcessResponse()
        val sureCheckAccepted = true
        val fixedDepositData = FixedDepositData()
        val accountNumber = "3001432596"
        val correlationId = "Mjd2N0F6aU5ZeUVIR2U1T1FBM0lub1NvcUtkZlFCY0Y"

        Mockito.`when`(fixedDepositMockService.createAccountProcess(fixedDepositData, sureCheckAccepted, createAccountProcessExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositCreateAccountProcessExtendedResponseListener>(2).onSuccess(fixedDepositCreateAccountProcessResponse.apply {
                this.accountNumber = accountNumber
                this.corelationId = correlationId
            })
        }

        val observer = mock<Observer<FixedDepositCreateAccountProcessResponse>>()
        fixedDepositViewModelSpy.createAccountProcessResponse.observeForever(observer)

        fixedDepositMockService.createAccountProcess(fixedDepositData, sureCheckAccepted, createAccountProcessExtendedResponseListener)
        verify(fixedDepositMockService).createAccountProcess(any(), any(), capture(processAccountCaptor))

        Assert.assertEquals(processAccountCaptor.value, createAccountProcessExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountProcessResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountProcessResponse.value?.accountNumber)
        Assert.assertNotNull(fixedDepositViewModelSpy.createAccountProcessResponse.value?.corelationId)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnAccountDetailsWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val accountDetailResponseListener = FixedDepositAccountDetailsExtendedResponseListener(testSubject)
        val accountDetailResponse = FixedDepositAccountDetailsResponse()
        val accountNumber = "3001432596"

        Mockito.`when`(fixedDepositMockService.fetchAccountDetail(accountNumber, accountDetailResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositAccountDetailsExtendedResponseListener>(1).onSuccess(accountDetailResponse.apply {
                this.fixedDeposit = FixedDeposit()
                this.interestPaymentInstruction = InterestPaymentInstruction()
            })
        }

        val observer = mock<Observer<FixedDepositAccountDetailsResponse>>()
        fixedDepositViewModelSpy.accountDetailsResponse.observeForever(observer)

        fixedDepositMockService.fetchAccountDetail(accountNumber, accountDetailResponseListener)
        verify(fixedDepositMockService).fetchAccountDetail(any(), capture(fetchAccountDetailsCaptor))

        Assert.assertEquals(fetchAccountDetailsCaptor.value, accountDetailResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.accountDetailsResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.accountDetailsResponse.value?.fixedDeposit)
        Assert.assertNotNull(fixedDepositViewModelSpy.accountDetailsResponse.value?.interestPaymentInstruction)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnUpdateAccountDetailsWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val updateAccountExtendedResponseListener = FixedDepositUpdateAccountExtendedResponseListener(testSubject)
        val updateAccountDetailsResponse = FixedDepositUpdateAccountDetailsResponse()
        val fixedDepositPayoutDetailsData = FixedDepositPayoutDetailsData()

        Mockito.`when`(fixedDepositMockService.requestAccountUpdate(fixedDepositPayoutDetailsData, updateAccountExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositUpdateAccountExtendedResponseListener>(1).onSuccess(updateAccountDetailsResponse.apply {
                this.product = Product()
            })
        }

        val observer = mock<Observer<FixedDepositUpdateAccountDetailsResponse>>()
        fixedDepositViewModelSpy.updateAccountDetailsResponse.observeForever(observer)

        fixedDepositMockService.requestAccountUpdate(fixedDepositPayoutDetailsData, updateAccountExtendedResponseListener)
        verify(fixedDepositMockService).requestAccountUpdate(any(), capture(updateAccountDetailsCaptor))

        Assert.assertEquals(updateAccountDetailsCaptor.value, updateAccountExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.updateAccountDetailsResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.updateAccountDetailsResponse.value?.product)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnFetchRenewalInstructionWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val renewalInstructionExtendedResponseListener = FixedDepositRenewalInstructionExtendedResponseListener(testSubject)
        val renewalInstructionResponse = FixedDepositRenewalInstructionResponse()
        val accountNumber = "3001432596"

        Mockito.`when`(fixedDepositMockService.requestRenewalInstruction(accountNumber, renewalInstructionExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositRenewalInstructionExtendedResponseListener>(1).onSuccess(renewalInstructionResponse.apply {
                this.renewalInstructionDetails = RenewalInstructionDetails()
            })
        }

        val observer = mock<Observer<FixedDepositRenewalInstructionResponse>>()
        fixedDepositViewModelSpy.renewalInstructionResponse.observeForever(observer)

        fixedDepositMockService.requestRenewalInstruction(accountNumber, renewalInstructionExtendedResponseListener)
        verify(fixedDepositMockService).requestRenewalInstruction(any(), capture(fetchRenewalInstructionCaptor))

        Assert.assertEquals(fetchRenewalInstructionCaptor.value, renewalInstructionExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.renewalInstructionResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.renewalInstructionResponse.value?.renewalInstructionDetails)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnConfirmRenewalInstructionWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val confirmRenewalInstructionExtendedResponseListener = FixedDepositConfirmRenewalInstructionExtendedResponseListener(testSubject)
        val confirmRenewalInstructionResponse = FixedDepositConfirmRenewalInstructionResponse()
        val renewalInstructionData = FixedDepositRenewalInstructionData()

        Mockito.`when`(fixedDepositMockService.requestRenewalInstructionConfirmation(renewalInstructionData, confirmRenewalInstructionExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositConfirmRenewalInstructionExtendedResponseListener>(1).onSuccess(confirmRenewalInstructionResponse.apply {
                this.fundTransferInstruction = FundTransferInstruction()
                this.renewalInstruction = RenewalInstruction()
            })
        }

        val observer = mock<Observer<FixedDepositConfirmRenewalInstructionResponse>>()
        fixedDepositViewModelSpy.confirmRenewalInstructionResponse.observeForever(observer)

        fixedDepositMockService.requestRenewalInstructionConfirmation(renewalInstructionData, confirmRenewalInstructionExtendedResponseListener)
        verify(fixedDepositMockService).requestRenewalInstructionConfirmation(any(), capture(confirmRenewalInstructionCaptor))

        Assert.assertEquals(confirmRenewalInstructionCaptor.value, confirmRenewalInstructionExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.confirmRenewalInstructionResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.confirmRenewalInstructionResponse.value?.fundTransferInstruction)
        Assert.assertNotNull(fixedDepositViewModelSpy.confirmRenewalInstructionResponse.value?.renewalInstruction)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnCreateRenewalInstructionWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val createRenewalInstructionExtendedResponseListener = FixedDepositCreateRenewalInstructionExtendedResponseListener(testSubject)
        val createRenewalInstructionResponse = FixedDepositCreateRenewalInstructionResponse()
        val renewalInstructionData = FixedDepositRenewalInstructionData()

        Mockito.`when`(fixedDepositMockService.createRenewalInstruction(renewalInstructionData, createRenewalInstructionExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositCreateRenewalInstructionExtendedResponseListener>(1).onSuccess(createRenewalInstructionResponse.apply {
                this.fundTransferInstruction = FundTransferInstruction()
                this.renewalInstruction = RenewalInstruction()
            })
        }

        val observer = mock<Observer<FixedDepositCreateRenewalInstructionResponse>>()
        fixedDepositViewModelSpy.createRenewalInstructionResponse.observeForever(observer)

        fixedDepositMockService.createRenewalInstruction(renewalInstructionData, createRenewalInstructionExtendedResponseListener)
        verify(fixedDepositMockService).createRenewalInstruction(any(), capture(createRenewalInstructionCaptor))

        Assert.assertEquals(createRenewalInstructionCaptor.value, createRenewalInstructionExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.createRenewalInstructionResponse.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.createRenewalInstructionResponse.value?.fundTransferInstruction)
        Assert.assertNotNull(fixedDepositViewModelSpy.createRenewalInstructionResponse.value?.renewalInstruction)

        verifyNoMoreInteractions(fixedDepositMockService)
    }

    @Test
    fun shouldReturnFetchBankListWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val bankListExtendedResponseListener = FixedDepositBankListExtendedResponseListener(testSubject)
        val bankListExtendedResponse = BankDetails()
        val bankList = arrayListOf("Absa", "Capitec", "FNB", "Standard Bank")

        Mockito.`when`(paymentsMockService.fetchBankList(bankListExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositBankListExtendedResponseListener>(0).onSuccess(bankListExtendedResponse.apply {
                this.bankList = bankList
            })
        }

        val observer = mock<Observer<BankDetails>>()
        fixedDepositViewModelSpy.bankDetails.observeForever(observer)

        paymentsMockService.fetchBankList(bankListExtendedResponseListener)
        verify(paymentsMockService).fetchBankList(capture(bankListCaptor))

        Assert.assertEquals(bankListCaptor.value, bankListExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.bankDetails.value)
        Assert.assertNotNull(fixedDepositViewModelSpy.bankDetails.value?.bankList)

        verifyNoMoreInteractions(paymentsMockService)
    }

    @Test
    fun shouldReturnFetchBranchListWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val branchListExtendedResponseListener = FixedDepositBranchListExtendedResponseListener(testSubject)
        val branchListExtendedResponse = BankBranches()
        val bankName = "Absa"

        Mockito.`when`(paymentsMockService.fetchBranchList(bankName, branchListExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositBranchListExtendedResponseListener>(1).onSuccess(branchListExtendedResponse.apply {
                this.bankName = bankName
                this.branchList = listOf(Branch())
            })
        }

        val observer = mock<Observer<BankBranches>>()
        fixedDepositViewModelSpy.bankBranches.observeForever(observer)

        paymentsMockService.fetchBranchList(bankName, branchListExtendedResponseListener)
        verify(paymentsMockService).fetchBranchList(any(), capture(branchListCaptor))

        Assert.assertEquals(branchListCaptor.value, branchListExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.bankBranches.value)
        Assert.assertEquals(fixedDepositViewModelSpy.bankBranches.value?.bankName, bankName)
        Assert.assertNotNull(fixedDepositViewModelSpy.bankBranches.value?.branchList)

        verifyNoMoreInteractions(paymentsMockService)
    }

    @Test
    fun shouldReturnFetchFICAAndCIFStatusWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val fICAAndCIFStatusExtendedResponseListener = FixedDepositFicaCheckExtendedResponseListener(testSubject)
        val fICAAndCIFStatusExtendedResponse = FicaCheckResponse()

        Mockito.`when`(overdraftMockService.fetchFICAAndCIFStatus(fICAAndCIFStatusExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FixedDepositFicaCheckExtendedResponseListener>(0).onSuccess(fICAAndCIFStatusExtendedResponse.apply {
                this.ficaAndCIFStatus = ""
            })
        }

        val observer = mock<Observer<FicaCheckResponse>>()
        fixedDepositViewModelSpy.ficaCheckResponse.observeForever(observer)

        overdraftMockService.fetchFICAAndCIFStatus(fICAAndCIFStatusExtendedResponseListener)
        verify(overdraftMockService).fetchFICAAndCIFStatus(capture(ficaCheckCaptor))

        Assert.assertEquals(ficaCheckCaptor.value, fICAAndCIFStatusExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.ficaCheckResponse.value?.ficaAndCIFStatus)
        verifyNoMoreInteractions(overdraftMockService)
    }

    @Test
    fun shouldReturnUpdateClientAgreementDetailsWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val clientAgreementDetailsExtendedResponseListener = UpdatePersonalClientAgreementExtendedResponseListener(testSubject)
        val clientAgreementDetailsExtendedResponse = TransactionResponse()
        val successMessage = "Success"

        Mockito.`when`(beneficiariesMockService.updateClientAgreementDetails(clientAgreementDetailsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<UpdatePersonalClientAgreementExtendedResponseListener>(0).onSuccess(clientAgreementDetailsExtendedResponse.apply {
                transactionStatus = successMessage
                transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<TransactionResponse>>()
        fixedDepositViewModelSpy.successResponse.observeForever(observer)

        beneficiariesMockService.updateClientAgreementDetails(clientAgreementDetailsExtendedResponseListener)
        verify(beneficiariesMockService).updateClientAgreementDetails(capture(personalClientAgreementCaptor))

        Assert.assertEquals(personalClientAgreementCaptor.value, clientAgreementDetailsExtendedResponseListener)
        Assert.assertEquals(fixedDepositViewModelSpy.successResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(fixedDepositViewModelSpy.successResponse.value?.transactionStatus, successMessage)

        verifyNoMoreInteractions(beneficiariesMockService)
    }

    @Test
    fun shouldReturnFetchClientAgreementDetailsWhenServiceCallComplete() {
        val fixedDepositViewModelSpy = spy(testSubject)
        val clientAgreementDetailsExtendedResponseListener = FetchClientAgreementDetailsExtendedResponseListener(testSubject)
        val clientAgreementDetailsExtendedResponse = ClientAgreementDetails()
        val successMessage = "Success"

        Mockito.`when`(beneficiariesMockService.fetchClientAgreementDetails(clientAgreementDetailsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<FetchClientAgreementDetailsExtendedResponseListener>(0).onSuccess(clientAgreementDetailsExtendedResponse.apply {
                transactionStatus = successMessage
                transactionMessage = successMessage
                this.clientAgreementAccepted = ""
            })
        }

        val observer = mock<Observer<TransactionResponse>>()
        fixedDepositViewModelSpy.successResponse.observeForever(observer)

        beneficiariesMockService.fetchClientAgreementDetails(clientAgreementDetailsExtendedResponseListener)
        verify(beneficiariesMockService).fetchClientAgreementDetails(capture(clientAgreementDetailsCaptor))

        Assert.assertEquals(clientAgreementDetailsCaptor.value, clientAgreementDetailsExtendedResponseListener)
        Assert.assertNotNull(fixedDepositViewModelSpy.successResponse.value)
        Assert.assertEquals(fixedDepositViewModelSpy.successResponse.value?.transactionMessage, successMessage)
        Assert.assertEquals(fixedDepositViewModelSpy.successResponse.value?.transactionStatus, successMessage)

        verifyNoMoreInteractions(beneficiariesMockService)
    }
}