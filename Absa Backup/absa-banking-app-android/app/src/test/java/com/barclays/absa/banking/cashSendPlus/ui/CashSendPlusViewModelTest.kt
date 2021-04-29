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
package com.barclays.absa.banking.cashSendPlus.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.cashSend.services.CashSendInteractor
import com.barclays.absa.banking.cashSendPlus.services.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class CashSendPlusViewModelTest : DaggerTest() {
    private var testSubject = CashSendPlusViewModel()
    private var cashSendPlusService = mock<CashSendPlusInteractor>()
    private var cashSendService = mock<CashSendInteractor>()
    private val beneficiariesService = mock<BeneficiariesInteractor>()

    @Captor
    private lateinit var cashSendPlusRegistrationCaptor: ArgumentCaptor<CashSendPlusRegistrationResponseListener>

    @Captor
    private lateinit var updateCashSendPlusLimitCaptor: ArgumentCaptor<UpdateCashSendPlusLimitResponseListener>

    @Captor
    private lateinit var cancelCashSendPlusRegistrationCaptor: ArgumentCaptor<CancelCashSendPlusRegistrationResponseListener>

    @Captor
    private lateinit var checkCashSendPlusRegistrationStatusCaptor: ArgumentCaptor<CheckCashSendPlusRegistrationStatusResponseListener>

    @Captor
    private lateinit var updateCustomerAgreementDetailsCaptor: ArgumentCaptor<UpdateCustomerAgreementDetailsResponseListener>

    @Captor
    private lateinit var sendCashSendPlusValidateSendMultipleCaptor: ArgumentCaptor<ValidateCashSendPlusSendMultipleResponseListener>

    @Captor
    private lateinit var sendCashSendPlusSendMultipleCaptor: ArgumentCaptor<CashSendPlusSendMultipleResponseListener>

    @Captor
    private lateinit var fetchBeneficiaryListCaptor: ArgumentCaptor<CashSendPlusBeneficiaryListResponseListener>

    @Captor
    private lateinit var fetchEncryptedPinCaptor: ArgumentCaptor<CashSendPlusPinEncryptionResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.apply {
            cashSendPlusInteractor = cashSendPlusService
            cashSendInteractor = cashSendService
            beneficiariesInteractor = beneficiariesService
        }
    }

    @Test
    fun shouldReturnSendCashSendPlusRegistrationWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusResponseListener = CashSendPlusRegistrationResponseListener(testSubject)
        val cashSendPlusResponse = CashSendPlusRegistrationResponse()
        val cashSendPlusLimitAmount = "5000"
        val cashSendPlusEmailAddress = "test@absa.co.za"
        val successMessage = "Success"

        Mockito.`when`(cashSendPlusService.sendCashSendPlusRegistration(cashSendPlusLimitAmount, cashSendPlusEmailAddress, cashSendPlusResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CashSendPlusRegistrationResponseListener>(2).onSuccess(cashSendPlusResponse.apply {
                this.registerForCashSendPlusDTO = CashSendPlusResponseData()
                this.transactionStatus = successMessage
                this.transactionMessage = successMessage
            })
        }

        val observer = mock<Observer<CashSendPlusRegistrationResponse>>()
        cashSendPlusViewModelSpy.cashSendPlusRegistrationResponse.observeForever(observer)

        cashSendPlusService.sendCashSendPlusRegistration(cashSendPlusLimitAmount, cashSendPlusEmailAddress, cashSendPlusResponseListener)
        verify(cashSendPlusService).sendCashSendPlusRegistration(any(), any(), capture(cashSendPlusRegistrationCaptor))

        Assert.assertEquals(cashSendPlusRegistrationCaptor.value, cashSendPlusResponseListener)
        Assert.assertEquals(cashSendPlusViewModelSpy.cashSendPlusRegistrationResponse.value?.transactionStatus, successMessage)
        Assert.assertEquals(cashSendPlusViewModelSpy.cashSendPlusRegistrationResponse.value?.transactionMessage, successMessage)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusRegistrationResponse.value?.registerForCashSendPlusDTO)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnSendUpdateCashSendPlusLimitWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusResponseListener = UpdateCashSendPlusLimitResponseListener(testSubject)
        val cashSendPlusResponse = UpdateCashSendPlusLimitResponse()
        val newCashSendPlusLimitAmount = "10000"
        val currentCashSendPlusLimitAmount = "5000"

        Mockito.`when`(cashSendPlusService.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, currentCashSendPlusLimitAmount, cashSendPlusResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<UpdateCashSendPlusLimitResponseListener>(2).onSuccess(cashSendPlusResponse.apply {
                this.cashSendPlusLimit = CashSendPlusResponseData().apply {
                    cashSendPlusLimitAmount = newCashSendPlusLimitAmount
                }
            })
        }

        val observer = mock<Observer<UpdateCashSendPlusLimitResponse>>()
        cashSendPlusViewModelSpy.updateCashSendPlusLimitResponse.observeForever(observer)

        cashSendPlusService.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, currentCashSendPlusLimitAmount, cashSendPlusResponseListener)
        verify(cashSendPlusService).sendUpdateCashSendPlusLimit(any(), any(), capture(updateCashSendPlusLimitCaptor))

        Assert.assertEquals(updateCashSendPlusLimitCaptor.value, cashSendPlusResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.updateCashSendPlusLimitResponse.value?.cashSendPlusLimit)
        Assert.assertEquals(cashSendPlusViewModelSpy.updateCashSendPlusLimitResponse.value?.cashSendPlusLimit?.cashSendPlusLimitAmount, newCashSendPlusLimitAmount)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnSendCashSendPlusRegistrationCancellationWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusResponseListener = CancelCashSendPlusRegistrationResponseListener(testSubject)
        val cashSendPlusResponse = CancelCashSendPlusRegistrationResponse()

        Mockito.`when`(cashSendPlusService.sendCashSendPlusRegistrationCancellation(cashSendPlusResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CancelCashSendPlusRegistrationResponseListener>(0).onSuccess(cashSendPlusResponse.apply {
                this.cashSendPlusResponseData = CashSendPlusResponseData()
            })
        }

        val observer = mock<Observer<CancelCashSendPlusRegistrationResponse>>()
        cashSendPlusViewModelSpy.cancelCashSendPlusRegistrationResponse.observeForever(observer)

        cashSendPlusService.sendCashSendPlusRegistrationCancellation(cashSendPlusResponseListener)
        verify(cashSendPlusService).sendCashSendPlusRegistrationCancellation(capture(cancelCashSendPlusRegistrationCaptor))

        Assert.assertEquals(cancelCashSendPlusRegistrationCaptor.value, cashSendPlusResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cancelCashSendPlusRegistrationResponse.value?.cashSendPlusResponseData)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnSendCheckCashSendPlusRegistrationWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusResponseListener = CheckCashSendPlusRegistrationStatusResponseListener(testSubject)
        val cashSendPlusResponse = CheckCashSendPlusRegistrationStatusResponse()

        Mockito.`when`(cashSendPlusService.sendCheckCashSendPlusRegistration(cashSendPlusResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CheckCashSendPlusRegistrationStatusResponseListener>(0).onSuccess(cashSendPlusResponse.apply {
                this.cashSendPlusResponseData = CashSendPlusResponseData()
            })
        }

        val observer = mock<Observer<CheckCashSendPlusRegistrationStatusResponse>>()
        cashSendPlusViewModelSpy.checkCashSendPlusRegistrationStatusResponse.observeForever(observer)

        cashSendPlusService.sendCheckCashSendPlusRegistration(cashSendPlusResponseListener)
        verify(cashSendPlusService).sendCheckCashSendPlusRegistration(capture(checkCashSendPlusRegistrationStatusCaptor))

        Assert.assertEquals(checkCashSendPlusRegistrationStatusCaptor.value, cashSendPlusResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.checkCashSendPlusRegistrationStatusResponse.value?.cashSendPlusResponseData)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnUpdateClientAgreementDetailsWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusResponseListener = UpdateCustomerAgreementDetailsResponseListener(testSubject)
        val cashSendPlusResponse = TransactionResponse()

        Mockito.`when`(cashSendPlusService.updateClientAgreementDetails(cashSendPlusResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<UpdateCustomerAgreementDetailsResponseListener>(0).onSuccess(cashSendPlusResponse)
        }

        val observer = mock<Observer<TransactionResponse>>()
        cashSendPlusViewModelSpy.updateCustomerAgreementDetailsResponse.observeForever(observer)

        cashSendPlusService.updateClientAgreementDetails(cashSendPlusResponseListener)
        verify(cashSendPlusService).updateClientAgreementDetails(capture(updateCustomerAgreementDetailsCaptor))

        Assert.assertEquals(updateCustomerAgreementDetailsCaptor.value, cashSendPlusResponseListener)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnSendCheckCashSendPlusValidateSendMultipleWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusValidateSendMultipleResponseListener = ValidateCashSendPlusSendMultipleResponseListener(testSubject)
        val cashSendPlusSendMultipleRequestDataModel = CashSendPlusSendMultipleRequestDataModel()
        val validateCashSendPlusSendMultipleResponse = CashSendPlusSendMultipleResponse()
        val cashSendDetails = mutableListOf<CashSendPlusSendMultipleResponse.CashSendPlusSendMultipleDetails>()

        Mockito.`when`(cashSendPlusService.sendCheckCashSendPlusValidateSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), cashSendPlusValidateSendMultipleResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ValidateCashSendPlusSendMultipleResponseListener>(1).onSuccess(validateCashSendPlusSendMultipleResponse.apply {
                this.cashSendDetails = cashSendDetails
            })
        }

        val observer = mock<Observer<CashSendPlusSendMultipleResponse>>()
        cashSendPlusViewModelSpy.validateCashSendPlusSendMultipleResponse.observeForever(observer)

        cashSendPlusService.sendCheckCashSendPlusValidateSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), cashSendPlusValidateSendMultipleResponseListener)
        verify(cashSendPlusService).sendCheckCashSendPlusValidateSendMultiple(any(), capture(sendCashSendPlusValidateSendMultipleCaptor))

        Assert.assertEquals(sendCashSendPlusValidateSendMultipleCaptor.value, cashSendPlusValidateSendMultipleResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.validateCashSendPlusSendMultipleResponse.value?.cashSendDetails)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnSendCheckCashSendPlusSendMultipleWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusSendMultipleResponseListener = CashSendPlusSendMultipleResponseListener(testSubject)
        val cashSendPlusSendMultipleResponse = CashSendPlusSendMultipleResponse()
        val cashSendPlusBeneficiaries = Gson().toJson(CashSendPlusSendMultipleRequestDataModel())
        val cashSendDetails = mutableListOf<CashSendPlusSendMultipleResponse.CashSendPlusSendMultipleDetails>()

        Mockito.`when`(cashSendPlusService.sendCheckCashSendPlusSendMultiple(cashSendPlusBeneficiaries, cashSendPlusSendMultipleResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CashSendPlusSendMultipleResponseListener>(1).onSuccess(cashSendPlusSendMultipleResponse.apply {
                this.cashSendDetails = cashSendDetails
            })
        }

        val observer = mock<Observer<CashSendPlusSendMultipleResponse>>()
        cashSendPlusViewModelSpy.cashSendPlusSendMultipleResponse.observeForever(observer)

        cashSendPlusService.sendCheckCashSendPlusSendMultiple(cashSendPlusBeneficiaries, cashSendPlusSendMultipleResponseListener)
        verify(cashSendPlusService).sendCheckCashSendPlusSendMultiple(any(), capture(sendCashSendPlusSendMultipleCaptor))

        Assert.assertEquals(sendCashSendPlusSendMultipleCaptor.value, cashSendPlusSendMultipleResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusSendMultipleResponse.value?.cashSendDetails)

        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldReturnBeneficiaryListWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusBeneficiaryListExtendedResponseListener = CashSendPlusBeneficiaryListResponseListener(testSubject)
        val cashSendPlusBeneficiaryListResponse = BeneficiaryListObject()
        val cashSend = "CashSend"
        val cashSendBeneficiaryList = listOf<BeneficiaryObject>()

        Mockito.`when`(beneficiariesService.fetchBeneficiaryList(cashSend, cashSendPlusBeneficiaryListExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CashSendPlusBeneficiaryListResponseListener>(1).onSuccess(cashSendPlusBeneficiaryListResponse.apply {
                this.cashsendBeneficiaryList = cashSendBeneficiaryList
            })
        }

        val observer = mock<Observer<BeneficiaryListObject>>()
        cashSendPlusViewModelSpy.cashSendPlusBeneficiaryListResponse.observeForever(observer)

        beneficiariesService.fetchBeneficiaryList(cashSend, cashSendPlusBeneficiaryListExtendedResponseListener)
        verify(beneficiariesService).fetchBeneficiaryList(any(), capture(fetchBeneficiaryListCaptor))

        Assert.assertEquals(fetchBeneficiaryListCaptor.value, cashSendPlusBeneficiaryListExtendedResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusBeneficiaryListResponse.value?.cashsendBeneficiaryList)

        verifyNoMoreInteractions(beneficiariesService)
    }

    @Test
    fun shouldReturnRequestCashSendPinEncryptionWhenServiceCallComplete() {
        val cashSendPlusViewModelSpy = spy(testSubject)
        val cashSendPlusPinEncryptionResponseListener = CashSendPlusPinEncryptionResponseListener(testSubject)
        val cashSendPlusPinEncryptionResponse = PINObject()
        val accessPin = "EAB39933A7B4F2E8"
        val mapId = "INSS"
        val virtualSessionId = "20000021"

        Mockito.`when`(cashSendService.requestCashSendPinEncryption(accessPin, cashSendPlusPinEncryptionResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CashSendPlusPinEncryptionResponseListener>(1).onSuccess(cashSendPlusPinEncryptionResponse.apply {
                this.accessPin = accessPin
                this.mapId = mapId
                this.virtualSessionId = virtualSessionId
            })
        }

        val observer = mock<Observer<PINObject>>()
        cashSendPlusViewModelSpy.cashSendPlusPinEncryptionResponse.observeForever(observer)

        cashSendService.requestCashSendPinEncryption(accessPin, cashSendPlusPinEncryptionResponseListener)
        verify(cashSendService).requestCashSendPinEncryption(any(), capture(fetchEncryptedPinCaptor))

        Assert.assertEquals(fetchEncryptedPinCaptor.value, cashSendPlusPinEncryptionResponseListener)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusPinEncryptionResponse.value)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusPinEncryptionResponse.value?.accessPin)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusPinEncryptionResponse.value?.mapId)
        Assert.assertNotNull(cashSendPlusViewModelSpy.cashSendPlusPinEncryptionResponse.value?.virtualSessionId)

        verifyNoMoreInteractions(cashSendService)
    }

    @Test
    fun shouldCallSendCashSendPlusRegistrationInteractorWhenSendCashSendPlusRegistrationFunctionIsCalled() {
        val cashSendPlusLimitAmount = "5000"
        val cashSendPlusEmailAddress = "test@absa.co.za"

        testSubject.sendCashSendPlusRegistration(cashSendPlusLimitAmount, cashSendPlusEmailAddress)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CashSendPlusRegistrationResponse>>(testSubject, "cashSendPlusRegistrationExtendedResponseListener")
        verify(cashSendPlusService).sendCashSendPlusRegistration(cashSendPlusLimitAmount, cashSendPlusEmailAddress, extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallSendUpdateCashSendPlusLimitInteractorWhenSendUpdateCashSendPlusLimitFunctionIsCalled() {
        val newCashSendPlusLimitAmount = "10000"
        val currentCashSendPlusLimitAmount = "5000"

        testSubject.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, currentCashSendPlusLimitAmount)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<UpdateCashSendPlusLimitResponse>>(testSubject, "updateCashSendPlusLimitExtendedResponseListener")
        verify(cashSendPlusService).sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, currentCashSendPlusLimitAmount, extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallSendCashSendPlusRegistrationCancellationLimitInteractorWhenSendCashSendPlusRegistrationCancellationFunctionIsCalled() {
        testSubject.sendCashSendPlusRegistrationCancellation()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CancelCashSendPlusRegistrationResponse>>(testSubject, "cancelCashSendPlusRegistrationExtendedResponseListener")
        verify(cashSendPlusService).sendCashSendPlusRegistrationCancellation(extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallSendCheckCashSendPlusRegistrationInteractorWhenSendCheckCashSendPlusRegistrationFunctionIsCalled() {
        testSubject.sendCheckCashSendPlusRegistration()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse>>(testSubject, "checkCashSendPlusRegistrationStatusExtendedResponseListener")
        verify(cashSendPlusService).sendCheckCashSendPlusRegistration(extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallUpdateClientAgreementDetailsInteractorWhenUpdateClientAgreementDetailsFunctionIsCalled() {
        testSubject.updateClientAgreementDetails()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ExtendedResponseListener<TransactionResponse>>(testSubject, "updateClientAgreementDetailsExtendedResponseListener")
        verify(cashSendPlusService).updateClientAgreementDetails(extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallSendCheckCashSendPlusValidateSendMultipleCashSendPlusInteractorWhenSendCashSendPlusValidateSendMultipleFunctionIsCalled() {
        val cashSendPlusSendMultipleRequestDataModel = CashSendPlusSendMultipleRequestDataModel()

        testSubject.sendCashSendPlusValidateSendMultiple(cashSendPlusSendMultipleRequestDataModel)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<ValidateCashSendPlusSendMultipleResponseListener>(testSubject, "cashSendPlusValidateSendMultipleResponseListener")
        verify(cashSendPlusService).sendCheckCashSendPlusValidateSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallSendCheckCashSendPlusSendMultipleCashSendPlusInteractorWhenSendCashSendPlusSendMultipleFunctionIsCalled() {
        val cashSendPlusSendMultipleRequestDataModel = CashSendPlusSendMultipleRequestDataModel()

        testSubject.sendCashSendPlusSendMultiple(cashSendPlusSendMultipleRequestDataModel)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<CashSendPlusSendMultipleResponseListener>(testSubject, "cashSendPlusSendMultipleResponseListener")
        verify(cashSendPlusService).sendCheckCashSendPlusSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendPlusService)
    }

    @Test
    fun shouldCallFetchBeneficiaryListBeneficiariesInteractorWhenFetchBeneficiaryListFunctionIsCalled() {
        testSubject.fetchBeneficiaryList()
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<CashSendPlusBeneficiaryListResponseListener>(testSubject, "cashSendPlusBeneficiaryListExtendedResponseListener")
        verify(beneficiariesService).fetchBeneficiaryList("CashSend", extendedResponseListenerReflection)
        verifyNoMoreInteractions(beneficiariesService)
    }

    @Test
    fun shouldCallFetchEncryptedPinCashSendInteractorWhenFetchEncryptedPinFunctionIsCalled() {
        val accessPin = "EAB39933A7B4F2E8"

        testSubject.fetchEncryptedPin(accessPin)
        val extendedResponseListenerReflection = ViewModelReflectionUtil.viewModelReflectionUtil<CashSendPlusPinEncryptionResponseListener>(testSubject, "cashSendPlusPinEncryptionResponseListener")
        verify(cashSendService).requestCashSendPinEncryption(accessPin, extendedResponseListenerReflection)
        verifyNoMoreInteractions(cashSendService)
    }
}