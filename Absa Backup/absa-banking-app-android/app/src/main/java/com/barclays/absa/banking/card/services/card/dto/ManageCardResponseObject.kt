/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.services.card.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.Card
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails
import com.barclays.absa.banking.card.services.card.dto.creditCard.*
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class ManageCardResponseObject() : ResponseObject(), KParcelable {

    var accounts: List<CreditCardAccount>? = ArrayList()

    var cliLimitsAndIncomeExpenseDetails: CLIIncomeData? = CLIIncomeData()
    var travelNotifications: List<TravelUpdateModel>? = ArrayList()

    @JsonProperty("retrieveCardATMandPosLimitsRespDTOs")
    var cardLimits: List<CardLimits>? = ArrayList()

    @JsonProperty("enquirePauseCardRespDTOs")
    var pauseStates: List<PauseStates>? = ArrayList()

    var displayCreditCardGadget: CreditCardGadget? = CreditCardGadget()

    constructor(parcel: Parcel) : this() {
        cliLimitsAndIncomeExpenseDetails = parcel.readParcelable(CLIIncomeData::class.java.classLoader)
        travelNotifications = parcel.createTypedArrayList(TravelUpdateModel.CREATOR)
        cardLimits = parcel.createTypedArrayList(CardLimits.CREATOR)
        pauseStates = parcel.createTypedArrayList(PauseStates.CREATOR)
        displayCreditCardGadget = parcel.readParcelable(CreditCardGadget::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cliLimitsAndIncomeExpenseDetails, flags)
        parcel.writeTypedList(travelNotifications)
        parcel.writeTypedList(cardLimits)
        parcel.writeTypedList(pauseStates)
        parcel.writeParcelable(displayCreditCardGadget, flags)
    }

    fun getCardLimits(cardNumber: String): CardLimits {
        for (item in cardLimits!!) {
            if (item.cardNumber.equals(cardNumber)) {
                return item
            }
        }
        return CardLimits()
    }

    fun getCardPauseStates(cardNumber: String): PauseStates {
        for (item in pauseStates!!) {
            @Suppress("SENSELESS_COMPARISON")
            if (item != null && cardNumber.equals(item.cardNumber)) {
                return item
            }
        }
        return PauseStates()
    }

    fun getCreditCardAccountInformation(cardNumber: String): CreditCardAccount {
        for (item in accounts!!) {
            @Suppress("SENSELESS_COMPARISON")
            if (item != null && cardNumber.equals(item.accountNo)) {
                return item
            }
        }
        return CreditCardAccount()
    }

    fun getCardOffer(cardNumber: String): CreditCardOffers {
        val cardLimitItem = CreditCardOffers()

        if (cliLimitsAndIncomeExpenseDetails != null && cliLimitsAndIncomeExpenseDetails?.creditCardAccountsList != null) {
            for (item in cliLimitsAndIncomeExpenseDetails?.creditCardAccountsList!!) {
                if (item.creditCardAccountnumbers.equals(cardNumber)) {
                    cardLimitItem.creditCardAccountnumbers = item.creditCardAccountnumbers
                    cardLimitItem.creditLimitIncreaseAmtForCLI = item.creditLimitIncreaseAmtForCLI
                }
            }
        }

        if (accounts != null) {
            for (accountItem in accounts!!) {
                if (cardNumber.equals(accountItem.accountNo)) {
                    cardLimitItem.existingCreditLimitOfCreditCard = accountItem.creditLimit
                }
            }
        }

        if (cliLimitsAndIncomeExpenseDetails != null) {
            cardLimitItem.offerIndicator = cliLimitsAndIncomeExpenseDetails?.creditCardApplyStatusEnum
        }

        return cardLimitItem
    }

    fun getTravelOffer(cardNumber: String): TravelUpdateModel {
        for (item in travelNotifications!!) {
            if (item.cardNumber.equals(cardNumber)) {
                return item
            }
        }
        return TravelUpdateModel()
    }

    fun getCardItem(cardNumber: String): ManageCardItem {
        val cardItem = ManageCardItem()
        cardItem.cardLimit = getCardLimits(cardNumber)
        cardItem.pauseStates = getCardPauseStates(cardNumber)
        cardItem.pauseStates?.cardNumber = cardNumber
        cardItem.cardOffers = getCardOffer(cardNumber)
        cardItem.vclData = buildVCLParcelableModel()
        cardItem.currentCreditCardLimit = getCreditCardAccountInformation(cardNumber).creditLimit
        cardItem.travelAbroad = getTravelOffer(cardNumber)
        return cardItem
    }

    fun listOfCards(): List<Card> {
        val cards: MutableList<Card> = ArrayList()
        cardLimits?.forEach { item ->
            Card().apply {
                cardNumber = item.cardNumber
                cardType = item.cardType
                cards.add(this)
            }
        }
        return cards
    }


    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ManageCardResponseObject> {
            override fun createFromParcel(parcel: Parcel): ManageCardResponseObject {
                return ManageCardResponseObject(parcel)
            }

            override fun newArray(size: Int): Array<ManageCardResponseObject?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun buildVCLParcelableModel(): VCLParcelableModel {
        val vclParcelableModel = VCLParcelableModel()
        vclParcelableModel.populateInstalmentAmounts()
        vclParcelableModel.creditCardVCLGadget = displayCreditCardGadget
        vclParcelableModel.isFreshBureauDataAvailable = displayCreditCardGadget?.isBureauDataAvailableFromCams
        vclParcelableModel.creditCardNumber = displayCreditCardGadget?.creditCardNumber
        vclParcelableModel.currentCreditLimit = displayCreditCardGadget?.existingCreditCardLimit
        vclParcelableModel.newCreditLimitAmount = displayCreditCardGadget?.creditLimitIncreaseAmount
        vclParcelableModel.incomeAndExpense = displayCreditCardGadget?.incomeAndExpenses
        return vclParcelableModel
    }

    class ManageCardItem() : KParcelable, Serializable {
        var cardLimit: CardLimits? = CardLimits()
        var pauseStates: PauseStates? = PauseStates()
        var cardOffers: CreditCardOffers? = CreditCardOffers()
        var vclData: VCLParcelableModel? = VCLParcelableModel()
        var travelAbroad: TravelUpdateModel? = TravelUpdateModel()
        var currentCreditCardLimit: String? = ""

        fun convertItemToCardLimitDetails(): ManageCardLimitDetails {
            val cardDetails = ManageCardLimitDetails()
            cardDetails.cardNumber = cardLimit?.cardNumber
            cardDetails.cardType = cardLimit?.cardType
            cardDetails.atmCurrentLimit = Amount(cardLimit?.cashWithdrawalLimit.toString())
            cardDetails.posCurrentLimit = Amount(cardLimit?.posLimit.toString())
            cardDetails.posMaxLimit = if ("CC".equals(cardDetails.cardType, ignoreCase = true)) Amount("0") else Amount(cardLimit?.posMaxLimit.toString())
            cardDetails.atmMaxLimit = Amount(cardLimit?.cashWithdrawalMaxLimit.toString())
            return cardDetails
        }

        constructor(parcel: Parcel) : this() {
            cardLimit = parcel.readParcelable(CardLimits::class.java.classLoader)
            pauseStates = parcel.readParcelable(PauseStates::class.java.classLoader)
            cardOffers = parcel.readParcelable(CreditCardOffers::class.java.classLoader)
            vclData = parcel.readParcelable(VCLParcelableModel::class.java.classLoader)
            travelAbroad = parcel.readParcelable(TravelUpdateModel::class.java.classLoader)
            currentCreditCardLimit = parcel.readString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(cardLimit, flags)
            parcel.writeParcelable(pauseStates, flags)
            parcel.writeParcelable(cardOffers, flags)
            parcel.writeParcelable(vclData, flags)
            parcel.writeParcelable(travelAbroad, flags)
            parcel.writeString(currentCreditCardLimit)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<ManageCardItem> {
                override fun createFromParcel(parcel: Parcel): ManageCardItem {
                    return ManageCardItem(parcel)
                }

                override fun newArray(size: Int): Array<ManageCardItem?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    class CLIIncomeData() : KParcelable {
        var creditCardApplyStatusEnum: String? = ""
        var creditCardAccountsList: List<CreditCardOffers>? = ArrayList()
        var incomeAndExpenseData: IncomeAndExpensesResponse? = IncomeAndExpensesResponse()

        constructor(parcel: Parcel) : this() {
            creditCardApplyStatusEnum = parcel.readString()
            creditCardAccountsList = parcel.createTypedArrayList(CreditCardOffers.CREATOR)
            incomeAndExpenseData = parcel.readParcelable(IncomeAndExpensesResponse::class.java.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(creditCardApplyStatusEnum)
            parcel.writeTypedList(creditCardAccountsList)
            parcel.writeParcelable(incomeAndExpenseData, flags)
        }

        companion object CREATOR : Parcelable.Creator<CLIIncomeData> {
            override fun createFromParcel(parcel: Parcel): CLIIncomeData {
                return CLIIncomeData(parcel)
            }

            override fun newArray(size: Int): Array<CLIIncomeData?> {
                return arrayOfNulls(size)
            }
        }
    }
}
