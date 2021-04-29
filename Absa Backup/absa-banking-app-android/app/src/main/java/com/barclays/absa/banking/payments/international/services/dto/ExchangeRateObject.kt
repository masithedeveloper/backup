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

package com.barclays.absa.banking.payments.international.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class ExchangeRateObject() : KParcelable {
    var estimatedWesternUnionRate: String? = ""
    var estimatedSwiftRate: String? = ""

    constructor(parcel: Parcel) : this() {
        estimatedWesternUnionRate = parcel.readString()
        estimatedSwiftRate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(estimatedWesternUnionRate)
        parcel.writeString(estimatedSwiftRate)
    }

    companion object CREATOR : Parcelable.Creator<ExchangeRateObject> {
        override fun createFromParcel(parcel: Parcel): ExchangeRateObject {
            return ExchangeRateObject(parcel)
        }

        override fun newArray(size: Int): Array<ExchangeRateObject?> {
            return arrayOfNulls(size)
        }
    }
}