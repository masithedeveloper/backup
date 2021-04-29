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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Transaction
import com.barclays.absa.banking.boundary.model.creditCardInsurance.LifeInsurance
import com.barclays.absa.banking.card.services.card.dto.CardLimits
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject
import com.barclays.absa.banking.card.services.card.dto.PauseStates
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel
import com.barclays.absa.banking.card.ui.TransactionHistoryComparison
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import kotlin.collections.ArrayList

class CreditCardResponseObject() : ResponseObject(), KParcelable {

    var travelNotification: TravelUpdateModel? = null

    @JsonProperty("creditCardAccountDTO")
    var creditCardAccountOffers: CreditCardOffers? = null

    var creditCardApplyStatusEnum: String? = ""

    @JsonProperty("account")
    var creditCardAccount: CreditCardAccount? = null

    var cardNumber: String? = ""

    var cardType: String? = ""

    var cashWithdrawalLimit: String? = ""

    var fromDate: String? = ""

    var toDate: String? = ""

    @JsonProperty("posLimit")
    var pointOfSaleLimit: String? = ""

    var cashWithdrawalMaxLimit: String? = ""

    @JsonProperty("posMaxLimit")
    var pointOfSaleMaxLimit: String? = ""

    var accountActivityListClearedTrue: List<Transaction>? = ArrayList()

    var accountActivityListClearedFalse: List<Transaction>? = ArrayList()

    var enquirePauseCardRespDTO: PauseStates? = null

    var lifeInsurance: LifeInsurance? = null

    var incomeAndExpenseData: IncomeAndExpensesResponse? = IncomeAndExpensesResponse()

    var displayCreditCardGadget: CreditCardGadget? = CreditCardGadget()

    constructor(parcel: Parcel) : this() {
        travelNotification = parcel.readParcelable(TravelUpdateModel::class.java.classLoader)
        creditCardAccountOffers = parcel.readParcelable(CreditCardOffers::class.java.classLoader)
        creditCardApplyStatusEnum = parcel.readString()
        creditCardAccount = parcel.readParcelable(CreditCardAccount::class.java.classLoader)
        cardNumber = parcel.readString()
        cardType = parcel.readString()
        cashWithdrawalLimit = parcel.readString()
        fromDate = parcel.readString()
        toDate = parcel.readString()
        pointOfSaleLimit = parcel.readString()
        cashWithdrawalMaxLimit = parcel.readString()
        pointOfSaleMaxLimit = parcel.readString()
        enquirePauseCardRespDTO = parcel.readParcelable(PauseStates::class.java.classLoader)
        lifeInsurance = parcel.readParcelable(LifeInsurance::class.java.classLoader)
        incomeAndExpenseData = parcel.readParcelable(IncomeAndExpensesResponse::class.java.classLoader)
        displayCreditCardGadget = parcel.readParcelable(CreditCardGadget::class.java.classLoader)
    }

    fun buildManageObject(): ManageCardResponseObject.ManageCardItem {
        val cardItem: ManageCardResponseObject.ManageCardItem = ManageCardResponseObject.ManageCardItem()
        cardItem.pauseStates = enquirePauseCardRespDTO
        if (enquirePauseCardRespDTO?.cardNumber == null) {
            cardItem.pauseStates?.cardNumber = cardNumber
        }
        val cardLimits = CardLimits()
        cardLimits.cashWithdrawalMaxLimit = cashWithdrawalMaxLimit
        cardLimits.posMaxLimit = pointOfSaleMaxLimit
        cardLimits.posLimit = pointOfSaleLimit
        cardLimits.cardNumber = cardNumber
        cardLimits.cashWithdrawalLimit = cashWithdrawalLimit
        cardLimits.cashWithdrawalMaxLimit = cashWithdrawalMaxLimit
        cardLimits.cardType = cardType
        cardItem.cardLimit = cardLimits
        cardItem.vclData = buildVCLParcelableModel()
        cardItem.cardOffers = creditCardAccountOffers
        cardItem.currentCreditCardLimit = creditCardAccount?.creditLimit
        cardItem.travelAbroad = travelNotification
        return cardItem
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

    fun buildListOfTransactions(): List<Transaction> {
        val transactions: MutableList<Transaction> = ArrayList()
        if (accountActivityListClearedFalse != null && accountActivityListClearedFalse!!.isNotEmpty()) {
            transactions.addAll(accountActivityListClearedFalse!!)
        }
        if (accountActivityListClearedTrue != null && accountActivityListClearedTrue!!.isNotEmpty()) {
            for (item: Transaction in accountActivityListClearedTrue!!) {
                item.isUnclearedTransaction = true
                transactions.add(item)
            }
        }
        Collections.sort(transactions, TransactionHistoryComparison())
        return transactions
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(travelNotification, flags)
        parcel.writeParcelable(creditCardAccountOffers, flags)
        parcel.writeString(creditCardApplyStatusEnum)
        parcel.writeParcelable(creditCardAccount, flags)
        parcel.writeString(cardNumber)
        parcel.writeString(cardType)
        parcel.writeString(cashWithdrawalLimit)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeString(pointOfSaleLimit)
        parcel.writeString(cashWithdrawalMaxLimit)
        parcel.writeString(pointOfSaleMaxLimit)
        parcel.writeParcelable(enquirePauseCardRespDTO, flags)
        parcel.writeParcelable(lifeInsurance, flags)
        parcel.writeParcelable(incomeAndExpenseData, flags)
        parcel.writeParcelable(displayCreditCardGadget, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardResponseObject> {
            override fun createFromParcel(parcel: Parcel): CreditCardResponseObject {
                return CreditCardResponseObject(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardResponseObject?> {
                return arrayOfNulls(size)
            }
        }
    }
}
