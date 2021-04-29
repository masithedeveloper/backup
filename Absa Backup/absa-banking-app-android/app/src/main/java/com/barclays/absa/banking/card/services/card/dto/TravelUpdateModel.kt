/*
 *
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.card.services.card.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class TravelUpdateModel() : KParcelable {

    var cardNumber: String? = ""
    var actionSelected: String? = ""
    var referralStartDate: String? = ""
    var referralEndDate: String? = ""

    constructor(parcel: Parcel) : this() {
        cardNumber = parcel.readString()
        actionSelected = parcel.readString()
        referralStartDate = parcel.readString()
        referralEndDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardNumber)
        parcel.writeString(actionSelected)
        parcel.writeString(referralStartDate)
        parcel.writeString(referralEndDate)
    }

    companion object CREATOR : Parcelable.Creator<TravelUpdateModel> {
        override fun createFromParcel(parcel: Parcel): TravelUpdateModel {
            return TravelUpdateModel(parcel)
        }

        override fun newArray(size: Int): Array<TravelUpdateModel?> {
            return arrayOfNulls(size)
        }
    }
}