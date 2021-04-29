/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international.data

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel

class TransferQuoteDetails() : KParcelable, BaseModel {
    var localCurrency: String? = ""
    var foreignCurrency: String? = ""
    var expectedPayoutAmount: String? = ""
    var destinationCurrencyRate: String? = ""
    var commissionAmountFee: String? = ""
    var totalDue: String? = ""
    var transactionReferenceNumber: String? = ""
    var mtcnCode: String? = ""
    var receiverFirstName: String? = ""
    var receiverSurname: String? = ""
    var streetAddress: String? = ""
    var countryToSendTo: String? = ""
    var valueAddedTax: String? = ""
    var testQuestion: String? = ""
    var testAnswer: String? = ""
    var payOutCurrency: String? = ""
    var gender: String? = ""
    var fromAccountDetails: String? = ""
    var localAmount: String? = ""
    var foreignAmount: String? = ""
    var expectedPayoutCurrency: String? = ""
    var originCountry: String? = ""
    var destinationState: String? = ""
    var destinationCity: String? = ""
    var youSend: String? = ""
    var usdConversionRate: String? = ""
    var reference: String? = ""

    constructor(parcel: Parcel) : this() {
        localCurrency = parcel.readString()
        foreignCurrency = parcel.readString()
        expectedPayoutAmount = parcel.readString()
        destinationCurrencyRate = parcel.readString()
        commissionAmountFee = parcel.readString()
        totalDue = parcel.readString()
        transactionReferenceNumber = parcel.readString()
        mtcnCode = parcel.readString()
        receiverFirstName = parcel.readString()
        receiverSurname = parcel.readString()
        streetAddress = parcel.readString()
        countryToSendTo = parcel.readString()
        valueAddedTax = parcel.readString()
        testQuestion = parcel.readString()
        testAnswer = parcel.readString()
        payOutCurrency = parcel.readString()
        gender = parcel.readString()
        fromAccountDetails = parcel.readString()
        localAmount = parcel.readString()
        foreignAmount = parcel.readString()
        expectedPayoutCurrency = parcel.readString()
        originCountry = parcel.readString()
        destinationState = parcel.readString()
        destinationCity = parcel.readString()
        youSend = parcel.readString()
        usdConversionRate = parcel.readString()
        reference = parcel.readString()
    }

    fun getNameAndSurname(): String {
        return "$receiverFirstName $receiverSurname"
    }

    fun getUSDollarExchangeRate(): String {
        return if (usdConversionRate != null) {
            "%.2f".format(usdConversionRate.toString().toDouble()) + " $localCurrency - 1 " + foreignCurrency
        } else {
            "N/A"
        }
    }

    fun getCurrencyExchangeRate(): String {
        return "%.2f".format(destinationCurrencyRate!!.toDouble()) + " $expectedPayoutCurrency  -  1 USD"
    }

    fun getFormattedDestinationCurrencyRate(): String {
        return "%.2f".format(destinationCurrencyRate!!.toDouble())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(localCurrency)
        parcel.writeString(foreignCurrency)
        parcel.writeString(expectedPayoutAmount)
        parcel.writeString(destinationCurrencyRate)
        parcel.writeString(commissionAmountFee)
        parcel.writeString(totalDue)
        parcel.writeString(transactionReferenceNumber)
        parcel.writeString(mtcnCode)
        parcel.writeString(receiverFirstName)
        parcel.writeString(receiverSurname)
        parcel.writeString(streetAddress)
        parcel.writeString(countryToSendTo)
        parcel.writeString(valueAddedTax)
        parcel.writeString(testQuestion)
        parcel.writeString(testAnswer)
        parcel.writeString(payOutCurrency)
        parcel.writeString(gender)
        parcel.writeString(fromAccountDetails)
        parcel.writeString(localAmount)
        parcel.writeString(foreignAmount)
        parcel.writeString(expectedPayoutCurrency)
        parcel.writeString(originCountry)
        parcel.writeString(destinationState)
        parcel.writeString(destinationCity)
        parcel.writeString(youSend)
        parcel.writeString(usdConversionRate)
        parcel.writeString(reference)
    }

    companion object CREATOR : Parcelable.Creator<TransferQuoteDetails> {
        override fun createFromParcel(parcel: Parcel): TransferQuoteDetails {
            return TransferQuoteDetails(parcel)
        }

        override fun newArray(size: Int): Array<TransferQuoteDetails?> {
            return arrayOfNulls(size)
        }
    }
}