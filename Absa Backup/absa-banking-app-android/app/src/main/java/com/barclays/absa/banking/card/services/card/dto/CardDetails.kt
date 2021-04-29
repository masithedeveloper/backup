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
package com.barclays.absa.banking.card.services.card.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class CardDetails() : KParcelable {
    var cardNumber = ""
    var encryptedCardDetails = ""
    var symmetricKey = ""
    var cvv = ""
    var expiryDate = ""
    var cardType = ""

    constructor(parcel: Parcel) : this() {
        cardNumber = parcel.readString() ?: ""
        encryptedCardDetails = parcel.readString() ?: ""
        symmetricKey = parcel.readString() ?: ""
        cvv = parcel.readString() ?: ""
        expiryDate = parcel.readString() ?: ""
        cardType = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardNumber)
        parcel.writeString(encryptedCardDetails)
        parcel.writeString(symmetricKey)
        parcel.writeString(cvv)
        parcel.writeString(expiryDate)
        parcel.writeString(cardType)
    }

    companion object CREATOR : Parcelable.Creator<CardDetails> {
        override fun createFromParcel(parcel: Parcel): CardDetails {
            return CardDetails(parcel)
        }

        override fun newArray(size: Int): Array<CardDetails?> {
            return arrayOfNulls(size)
        }
    }
}