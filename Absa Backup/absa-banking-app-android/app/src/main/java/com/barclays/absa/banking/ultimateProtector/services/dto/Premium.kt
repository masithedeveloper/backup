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
package com.barclays.absa.banking.ultimateProtector.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class Premium() : KParcelable {

    var coverAmount: String? = ""
    var predictivePremium: String? = ""
    var serviceFee: String? = ""
    var totalPremium: String? = ""

    constructor(parcel: Parcel) : this() {
        coverAmount = parcel.readString()
        predictivePremium = parcel.readString()
        serviceFee = parcel.readString()
        totalPremium = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(coverAmount)
        parcel.writeString(predictivePremium)
        parcel.writeString(serviceFee)
        parcel.writeString(totalPremium)
    }

    companion object CREATOR : Parcelable.Creator<Premium> {
        override fun createFromParcel(parcel: Parcel): Premium {
            return Premium(parcel)
        }

        override fun newArray(size: Int): Array<Premium?> {
            return arrayOfNulls(size)
        }
    }
}