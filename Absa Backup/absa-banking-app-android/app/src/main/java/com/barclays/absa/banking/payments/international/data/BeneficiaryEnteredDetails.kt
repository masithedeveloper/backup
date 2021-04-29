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
import com.barclays.absa.utils.TextFormatUtils

class BeneficiaryEnteredDetails() : KParcelable {
    var beneficiaryNames: String? = ""
    var beneficiarySurname: String? = ""
    var beneficiaryGender: String? = ""
    var beneficiaryCitizenship: String? = ""
    var paymentAddress: String? = ""
    var paymentCountry: String? = ""
    var paymentState: String? = ""
    var paymentCity: String? = ""
    var paymentQuestion: String? = ""
    var paymentAnswer: String? = ""
    var paymentDestinationCountryCode: String? = ""
    var currencyToPay: String? = ""
    var paymentAmount: String? = ""
    var fromAccountNumber: String? = ""
    var fromAccountBalance: String? = ""
    var beneficiaryId: String? = ""
    var fromAccountDescription: String? = ""
    var fromAccountType: String? = ""
    var destinationCurrency: String? = ""
    var sendCurrency: String? = ""

    fun fromAccountDetails(): String = TextFormatUtils.formatAccountNumberAndDescription(fromAccountDescription, fromAccountNumber)

    fun shortName(): String {
        return beneficiaryNames?.substring(0, if (beneficiaryNames?.length!! <= 6) beneficiaryNames?.length!! else 6).toString() +
                beneficiarySurname?.substring(0, if (beneficiarySurname?.length!! <= 6) beneficiarySurname?.length!! else 6).toString().replace("\\s".toRegex(), "") +
                (100..999).random().toString()
    }

    fun formattedFirstName(): String {
        return "$beneficiaryNames $beneficiarySurname"
    }

    fun beneficiaryResidentialStatus(): String {
        return if (beneficiaryCitizenship == "SA resident temporarily abroad") {
            "RES ACCOUNT ABROAD"
        } else {
            "NON RESIDENT OTHER"
        }
    }

    constructor(parcel: Parcel) : this() {
        beneficiaryNames = parcel.readString()
        beneficiarySurname = parcel.readString()
        beneficiaryGender = parcel.readString()
        beneficiaryCitizenship = parcel.readString()
        paymentAddress = parcel.readString()
        paymentCountry = parcel.readString()
        paymentState = parcel.readString()
        paymentCity = parcel.readString()
        paymentQuestion = parcel.readString()
        paymentAnswer = parcel.readString()
        paymentDestinationCountryCode = parcel.readString()
        currencyToPay = parcel.readString()
        paymentAmount = parcel.readString()
        fromAccountNumber = parcel.readString()
        fromAccountBalance = parcel.readString()
        beneficiaryId = parcel.readString()
        fromAccountDescription = parcel.readString()
        fromAccountType = parcel.readString()
        destinationCurrency = parcel.readString()
        sendCurrency = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(beneficiaryNames)
        parcel.writeString(beneficiarySurname)
        parcel.writeString(beneficiaryGender)
        parcel.writeString(beneficiaryCitizenship)
        parcel.writeString(paymentAddress)
        parcel.writeString(paymentCountry)
        parcel.writeString(paymentState)
        parcel.writeString(paymentCity)
        parcel.writeString(paymentQuestion)
        parcel.writeString(paymentAnswer)
        parcel.writeString(paymentDestinationCountryCode)
        parcel.writeString(currencyToPay)
        parcel.writeString(paymentAmount)
        parcel.writeString(fromAccountNumber)
        parcel.writeString(fromAccountBalance)
        parcel.writeString(beneficiaryId)
        parcel.writeString(fromAccountDescription)
        parcel.writeString(fromAccountType)
        parcel.writeString(destinationCurrency)
        parcel.writeString(sendCurrency)
    }

    companion object CREATOR : Parcelable.Creator<BeneficiaryEnteredDetails> {
        override fun createFromParcel(parcel: Parcel): BeneficiaryEnteredDetails {
            return BeneficiaryEnteredDetails(parcel)
        }

        override fun newArray(size: Int): Array<BeneficiaryEnteredDetails?> {
            return arrayOfNulls(size)
        }
    }
}