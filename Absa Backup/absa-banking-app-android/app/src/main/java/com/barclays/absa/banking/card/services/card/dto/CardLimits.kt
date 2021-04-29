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
import com.barclays.absa.banking.framework.KParcelable

class CardLimits() : KParcelable {
    var cardNumber: String? = ""
    var cardType: String? = ""
    var cashWithdrawalLimit: String? = ""
    var posLimit: String? = ""
    var cashWithdrawalMaxLimit: String? = ""
    var posMaxLimit: String? = ""
    var closeSubReason: String? = ""

    constructor(parcel: Parcel) : this() {
        cardNumber = parcel.readString()
        cardType = parcel.readString()
        cashWithdrawalLimit = parcel.readString()
        posLimit = parcel.readString()
        cashWithdrawalMaxLimit = parcel.readString()
        posMaxLimit = parcel.readString()
        closeSubReason = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardNumber)
        parcel.writeString(cardType)
        parcel.writeString(cashWithdrawalLimit)
        parcel.writeString(posLimit)
        parcel.writeString(cashWithdrawalMaxLimit)
        parcel.writeString(posMaxLimit)
        parcel.writeString(closeSubReason)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CardLimits> {
            override fun createFromParcel(parcel: Parcel): CardLimits {
                return CardLimits(parcel)
            }

            override fun newArray(size: Int): Array<CardLimits?> {
                return arrayOfNulls(size)
            }
        }
    }
}
