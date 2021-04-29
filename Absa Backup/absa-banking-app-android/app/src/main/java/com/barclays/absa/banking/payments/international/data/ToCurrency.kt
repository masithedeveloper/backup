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

class ToCurrency() : KParcelable, BaseModel {
    var toCurrencyCode: String? = ""
    var toCurrencyDescription: String? = ""

    constructor(parcel: Parcel) : this() {
        toCurrencyCode = parcel.readString()
        toCurrencyDescription = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(toCurrencyCode)
        parcel.writeString(toCurrencyDescription)
    }

    companion object CREATOR : Parcelable.Creator<ToCurrency> {
        override fun createFromParcel(parcel: Parcel): ToCurrency {
            return ToCurrency(parcel)
        }

        override fun newArray(size: Int): Array<ToCurrency?> {
            return arrayOfNulls(size)
        }
    }
}