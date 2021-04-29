/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.CardAccountList
import com.barclays.absa.banking.boundary.model.CardPin
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.card.services.card.CardInteractor
import com.barclays.absa.banking.card.services.card.CardService
import com.barclays.absa.banking.card.services.card.dto.*
import com.barclays.absa.banking.card.ui.CardCache
import com.barclays.absa.banking.card.ui.CardListManageCardExtendedResponseListener
import com.barclays.absa.banking.card.ui.creditCard.hub.extendedresponselisteners.TravelAbroadExtendedResponseListener
import com.barclays.absa.banking.card.ui.pauseCard.PauseCardStateExtendedResponseListener
import com.barclays.absa.banking.card.ui.pauseCard.UpdatePauseCardStateExtendedResponseListener
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class ManageCardViewModel : BaseViewModel() {
    private val manageCardExtendedResponseListener: ExtendedResponseListener<ManageCardResponseObject> by lazy { CardListManageCardExtendedResponseListener(this) }
    private val listOfCardsExtendedResponseListener: ExtendedResponseListener<CardAccountList> by lazy { ListOfCardsExtendedResponseListener(this) }
    private val pinRetrievalExtendedResponseListener: ExtendedResponseListener<CardPin> by lazy { PinRetrievalExtendedResponseListener(this) }
    private val viewCardDetailsExtendedResponseListener: ExtendedResponseListener<VirtualCardDetailsResponse> by lazy { ViewCardDetailsExtendedResponseListener(this) }
    private val updatePauseCardStatusExtendedResponseListener: ExtendedResponseListener<UpdatePauseCard> by lazy { UpdatePauseCardStateExtendedResponseListener(this) }
    private val pauseCardStatusExtendedResponseListener: ExtendedResponseListener<EnquirePauseState> by lazy { PauseCardStateExtendedResponseListener(this) }
    private val updateTravelDatesExtendedResponseListener: TravelAbroadExtendedResponseListener? by lazy { TravelAbroadExtendedResponseListener(this) }

    var cardService = CardInteractor()

    var manageCardResponse = MutableLiveData<ManageCardResponseObject>()
    var cardPin = MutableLiveData<CardPin>()
    var virtualCardDetailsResponse = MutableLiveData<VirtualCardDetailsResponse>()
    var updatePauseCardResponse = MutableLiveData<UpdatePauseCard>()
    var pauseCardStates = MutableLiveData<EnquirePauseState>()
    var travelDates = MutableLiveData<SureCheckResponse>()
    private var cardNumber = ""

    fun getListOfCreditAndDebitCards() {
        cardService.fetchManageCardsData(manageCardExtendedResponseListener)
    }

    fun retrievePin() {
        retrievePin(cardNumber)
    }

    fun retrievePin(cardNumber: String) {
        this.cardNumber = cardNumber
        val cardList = CardCache.getInstance().retrieveCardIndex()
        when {
            !cardList.cardList.isNullOrEmpty() -> cardService.fetchPIN(cardNumber, findCardIndex(cardList, cardNumber), pinRetrievalExtendedResponseListener)
            else -> cardService.fetchLinkedCards(listOfCardsExtendedResponseListener)
        }
    }

    fun retrieveCardPin(cardAccountList: CardAccountList) {
        cardService.fetchPIN(cardNumber, findCardIndex(cardAccountList, cardNumber), pinRetrievalExtendedResponseListener)
    }

    fun retrieveCardDetails() {
        retrieveCardDetails(cardNumber)
    }

    fun retrieveCardDetails(cardNumber: String) {
        this.cardNumber = cardNumber
        cardService.fetchCardDetails(cardNumber, viewCardDetailsExtendedResponseListener)
    }

    private fun findCardIndex(successResponse: CardAccountList, cardNumber: String): String {
        successResponse.cardList.forEach { cardItem ->
            if (cardNumber.equals(cardItem.cardNumber, ignoreCase = true)) {
                return cardItem.index ?: "1"
            }
        }
        return ""
    }

    fun updatePauseCarsStates(pauseCardStates: PauseStates) {
        cardService.updatePauseCardStates(pauseCardStates, updatePauseCardStatusExtendedResponseListener)
    }

    fun fetchPauseCardStates(cardNumber: String) {
        cardService.fetchPauseCardStates(cardNumber, pauseCardStatusExtendedResponseListener)
    }

    fun updateTravelDates(travelUpdateModel: TravelUpdateModel) {
        cardService.updateTravelNotification(travelUpdateModel, updateTravelDatesExtendedResponseListener)
    }
}