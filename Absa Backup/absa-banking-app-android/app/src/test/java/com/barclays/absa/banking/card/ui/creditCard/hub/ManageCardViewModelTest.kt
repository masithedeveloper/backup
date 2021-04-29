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

package com.barclays.absa.banking.card.ui.creditCard.hub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.barclays.absa.banking.boundary.model.CardAccountList
import com.barclays.absa.banking.boundary.model.CardPin
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.card.services.card.CardInteractor
import com.barclays.absa.banking.card.services.card.dto.*
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardAccount
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardGadget
import com.barclays.absa.banking.card.ui.CardListManageCardExtendedResponseListener
import com.barclays.absa.banking.card.ui.creditCard.hub.extendedresponselisteners.TravelAbroadExtendedResponseListener
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil
import com.barclays.absa.banking.card.ui.pauseCard.PauseCardStateExtendedResponseListener
import com.barclays.absa.banking.card.ui.pauseCard.UpdatePauseCardStateExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class ManageCardViewModelTest {

    private var testSubject = ManageCardViewModel()
    private var cardMockService = mock<CardInteractor>()

    @Captor
    private lateinit var getListOfCreditAndDebitCardsCaptor: ArgumentCaptor<CardListManageCardExtendedResponseListener>

    @Captor
    private lateinit var retrievePinCaptor: ArgumentCaptor<PinRetrievalExtendedResponseListener>

    @Captor
    private lateinit var retrieveCardDetailsCaptor: ArgumentCaptor<ViewCardDetailsExtendedResponseListener>

    @Captor
    private lateinit var updatePauseCarsStatesCaptor: ArgumentCaptor<UpdatePauseCardStateExtendedResponseListener>

    @Captor
    private lateinit var fetchPauseCardStatesCaptor: ArgumentCaptor<PauseCardStateExtendedResponseListener>

    @Captor
    private lateinit var updateTravelDatesCaptor: ArgumentCaptor<TravelAbroadExtendedResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.cardService = cardMockService
    }

    @Test
    fun shouldCallFetchManageCardsDataCardServiceWhenGetListOfCreditAndDebitCardsFunctionIsCalled() {
        testSubject.getListOfCreditAndDebitCards()
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<CardListManageCardExtendedResponseListener>(testSubject, "manageCardExtendedResponseListener")
        verify(cardMockService).fetchManageCardsData(extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallFetchCardDetailsCardServiceWhenRetrieveCardDetailsFunctionIsCalled() {
        val cardNumber = "4483870000032817"

        testSubject.retrieveCardDetails(cardNumber)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<ViewCardDetailsExtendedResponseListener>(testSubject, "viewCardDetailsExtendedResponseListener")
        verify(cardMockService).fetchCardDetails(cardNumber, extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallFetchPINCardServiceWhenRetrieveCardPinFunctionIsCalled() {
        val cardAccountList = CardAccountList()
        val cardIndex = ""
        val cardNumber = ""

        testSubject.retrieveCardPin(cardAccountList)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<PinRetrievalExtendedResponseListener>(testSubject, "pinRetrievalExtendedResponseListener")
        verify(cardMockService).fetchPIN(cardNumber, cardIndex, extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallUpdatePauseCardStatesCardServiceWhenUpdatePauseCarsStatesFunctionIsCalled() {
        val pauseCardStates = PauseStates()

        testSubject.updatePauseCarsStates(pauseCardStates)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<UpdatePauseCardStateExtendedResponseListener>(testSubject, "updatePauseCardStatusExtendedResponseListener")
        verify(cardMockService).updatePauseCardStates(pauseCardStates, extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallFetchPauseCardStatesCardServiceWhenFetchPauseCardStatesFunctionIsCalled() {
        val cardNumber = "4483870000032817"

        testSubject.fetchPauseCardStates(cardNumber)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<PauseCardStateExtendedResponseListener>(testSubject, "pauseCardStatusExtendedResponseListener")
        verify(cardMockService).fetchPauseCardStates(cardNumber, extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallUpdateTravelNotificationCardServiceWhenUpdateTravelDatesFunctionIsCalled() {
        val travelUpdateModel = TravelUpdateModel()

        testSubject.updateTravelDates(travelUpdateModel)
        val extendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<TravelAbroadExtendedResponseListener>(testSubject, "updateTravelDatesExtendedResponseListener")
        verify(cardMockService).updateTravelNotification(travelUpdateModel, extendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldCallInteractorFetchLinkedCardsWhenRetrievePinFunctionIsCalled() {
        val cardNumber = "1239875554"

        testSubject.retrievePin(cardNumber)
        val listOfCardsExtendedResponseListener = ViewModelReflectionUtil.viewModelReflectionUtil<ListOfCardsExtendedResponseListener>(testSubject, "listOfCardsExtendedResponseListener")
        verify(cardMockService).fetchLinkedCards(listOfCardsExtendedResponseListener)
        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnListOfCreditAndDebitCardsWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val manageCardExtendedResponseListener = CardListManageCardExtendedResponseListener(testSubject)
        val manageCardResponse = ManageCardResponseObject()
        val accounts = listOf<CreditCardAccount>()
        val cardLimits = listOf<CardLimits>()
        val cliLimitsAndIncomeExpenseDetails = ManageCardResponseObject.CLIIncomeData()
        val displayCreditCardGadget = CreditCardGadget()
        val pauseStates = listOf<PauseStates>()
        val travelNotifications = listOf<TravelUpdateModel>()

        Mockito.`when`(cardMockService.fetchManageCardsData(manageCardExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<CardListManageCardExtendedResponseListener>(0).onSuccess(manageCardResponse.apply {
                this.accounts = accounts
                this.cardLimits = cardLimits
                this.cliLimitsAndIncomeExpenseDetails = cliLimitsAndIncomeExpenseDetails
                this.displayCreditCardGadget = displayCreditCardGadget
                this.pauseStates = pauseStates
                this.travelNotifications = travelNotifications
            })
        }

        val observer = mock<Observer<ManageCardResponseObject>>()
        manageCardViewModelSpy.manageCardResponse.observeForever(observer)

        cardMockService.fetchManageCardsData(manageCardExtendedResponseListener)
        verify(cardMockService).fetchManageCardsData(capture(getListOfCreditAndDebitCardsCaptor))

        Assert.assertEquals(manageCardExtendedResponseListener, getListOfCreditAndDebitCardsCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.accounts)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.cardLimits)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.cliLimitsAndIncomeExpenseDetails)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.displayCreditCardGadget)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.pauseStates)
        Assert.assertNotNull(manageCardViewModelSpy.manageCardResponse.value?.travelNotifications)

        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnCardPinWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val pinRetrievalExtendedResponseListener = PinRetrievalExtendedResponseListener(testSubject)
        val cardPinResponse = CardPin()
        val cardNumber = "4483870000032817"
        val cardIndex = "1"
        val cardPinBlock = "401398FFCD8A9FED"
        val pinBlock = "401398FFCD8A9FED"
        val pinReference = "10528A"

        Mockito.`when`(cardMockService.fetchPIN(cardNumber, cardIndex, pinRetrievalExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PinRetrievalExtendedResponseListener>(2).onSuccess(cardPinResponse.apply {
                this.cardNumber = cardNumber
                this.cardPinBlock = cardPinBlock
                this.pinBlock = pinBlock
                this.pinReference = pinReference
            })
        }

        val observer = mock<Observer<CardPin>>()
        manageCardViewModelSpy.cardPin.observeForever(observer)

        cardMockService.fetchPIN(cardNumber, cardIndex, pinRetrievalExtendedResponseListener)
        verify(cardMockService).fetchPIN(any(), any(), capture(retrievePinCaptor))

        Assert.assertEquals(pinRetrievalExtendedResponseListener, retrievePinCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.cardPin.value)
        Assert.assertNotNull(manageCardViewModelSpy.cardPin.value?.cardNumber)
        Assert.assertNotNull(manageCardViewModelSpy.cardPin.value?.cardPinBlock)
        Assert.assertNotNull(manageCardViewModelSpy.cardPin.value?.pinBlock)
        Assert.assertNotNull(manageCardViewModelSpy.cardPin.value?.pinReference)

        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnCardDetailsWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val viewCardDetailsExtendedResponseListener = ViewCardDetailsExtendedResponseListener(testSubject)
        val virtualCardDetailsResponse = VirtualCardDetailsResponse()
        val cardNumber = "4483870000032817"

        Mockito.`when`(cardMockService.fetchCardDetails(cardNumber, viewCardDetailsExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<ViewCardDetailsExtendedResponseListener>(1).onSuccess(virtualCardDetailsResponse.apply {
                this.cardDetails = CardDetails()
            })
        }

        val observer = mock<Observer<VirtualCardDetailsResponse>>()
        manageCardViewModelSpy.virtualCardDetailsResponse.observeForever(observer)

        cardMockService.fetchCardDetails(cardNumber, viewCardDetailsExtendedResponseListener)
        verify(cardMockService).fetchCardDetails(any(), capture(retrieveCardDetailsCaptor))

        Assert.assertEquals(viewCardDetailsExtendedResponseListener, retrieveCardDetailsCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.virtualCardDetailsResponse.value)
        Assert.assertNotNull(manageCardViewModelSpy.virtualCardDetailsResponse.value?.cardDetails)

        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnUpdatePauseCardStatesWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val updatePauseCardStatusExtendedResponseListener = UpdatePauseCardStateExtendedResponseListener(testSubject)
        val updatePauseCardResponse = UpdatePauseCard()
        val pauseCardStates = PauseStates()
        val cardNumber = "4483870000032817"

        Mockito.`when`(cardMockService.updatePauseCardStates(pauseCardStates, updatePauseCardStatusExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<UpdatePauseCardStateExtendedResponseListener>(1).onSuccess(updatePauseCardResponse.apply {
                this.cardNumber = cardNumber
            })
        }

        val observer = mock<Observer<UpdatePauseCard>>()
        manageCardViewModelSpy.updatePauseCardResponse.observeForever(observer)

        cardMockService.updatePauseCardStates(pauseCardStates, updatePauseCardStatusExtendedResponseListener)
        verify(cardMockService).updatePauseCardStates(any(), capture(updatePauseCarsStatesCaptor))

        Assert.assertEquals(updatePauseCardStatusExtendedResponseListener, updatePauseCarsStatesCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.updatePauseCardResponse.value)
        Assert.assertNotNull(manageCardViewModelSpy.updatePauseCardResponse.value?.cardNumber)
        Assert.assertNotNull(manageCardViewModelSpy.updatePauseCardResponse.value?.cardUpdated)
        Assert.assertNotNull(manageCardViewModelSpy.updatePauseCardResponse.value?.authorisationOutstanding)

        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnPauseCardStatesWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val pauseCardStatusExtendedResponseListener = PauseCardStateExtendedResponseListener(testSubject)
        val pauseCardStatesResponse = EnquirePauseState()
        val cardNumber = "4483870000032817"

        Mockito.`when`(cardMockService.fetchPauseCardStates(cardNumber, pauseCardStatusExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<PauseCardStateExtendedResponseListener>(1).onSuccess(pauseCardStatesResponse.apply {
                this.pauseStates = PauseStates()
            })
        }

        val observer = mock<Observer<EnquirePauseState>>()
        manageCardViewModelSpy.pauseCardStates.observeForever(observer)

        cardMockService.fetchPauseCardStates(cardNumber, pauseCardStatusExtendedResponseListener)
        verify(cardMockService).fetchPauseCardStates(any(), capture(fetchPauseCardStatesCaptor))

        Assert.assertEquals(pauseCardStatusExtendedResponseListener, fetchPauseCardStatesCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.pauseCardStates.value)
        Assert.assertNotNull(manageCardViewModelSpy.pauseCardStates.value?.pauseStates)

        verifyNoMoreInteractions(cardMockService)
    }

    @Test
    fun shouldReturnUpdateTravelNotificationWhenServiceCallComplete() {
        val manageCardViewModelSpy = spy(testSubject)
        val updateTravelDatesExtendedResponseListener = TravelAbroadExtendedResponseListener(testSubject)
        val travelDatesResponse = SureCheckResponse()
        val travelUpdateModel = TravelUpdateModel()
        val tvnFlag = "SURECHECKV2Required"
        val sureCheckFlag = "SURECHECKV2Required"
        val referenceNumber = "ODAwMDI0QjIzQTI1MA"
        val email = "A*****@gmail.com"
        val notificationMethod = ""
        val correlationId = "ODAwMDI0QjIzQTI1MA"
        val cellNumber = "******4788"
        val transactionDate = "2017-07-26 16:20:22"

        Mockito.`when`(cardMockService.updateTravelNotification(travelUpdateModel, updateTravelDatesExtendedResponseListener)).thenAnswer {
            return@thenAnswer it.getArgument<TravelAbroadExtendedResponseListener>(1).onSuccess(travelDatesResponse.apply {
                this.tvnFlag = tvnFlag
                this.sureCheckFlag = sureCheckFlag
                this.referenceNumber = referenceNumber
                this.email = email
                this.notificationMethod = notificationMethod
                this.correlationId = correlationId
                this.cellnumber = cellNumber
                this.transactionDate = transactionDate
            })
        }

        val observer = mock<Observer<SureCheckResponse>>()
        manageCardViewModelSpy.travelDates.observeForever(observer)

        cardMockService.updateTravelNotification(travelUpdateModel, updateTravelDatesExtendedResponseListener)
        verify(cardMockService).updateTravelNotification(any(), capture(updateTravelDatesCaptor))

        Assert.assertEquals(updateTravelDatesExtendedResponseListener, updateTravelDatesCaptor.value)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.tvnFlag)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.sureCheckFlag)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.referenceNumber)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.email)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.notificationMethod)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.correlationId)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.cellnumber)
        Assert.assertNotNull(manageCardViewModelSpy.travelDates.value?.transactionDate)

        verifyNoMoreInteractions(cardMockService)
    }
}