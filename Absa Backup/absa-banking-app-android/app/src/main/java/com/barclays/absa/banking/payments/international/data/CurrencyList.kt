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

class CurrencyList() : KParcelable, BaseModel {
    var currencyCode: String? = ""
    var currencyDescription: String? = ""

    constructor(parcel: Parcel) : this() {
        currencyCode = parcel.readString()
        currencyDescription = parcel.readString()
    }

    fun getCurrencyMenuItem(): String {
        return "$currencyDescription ($currencyCode)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(currencyCode)
        parcel.writeString(currencyDescription)
    }

    companion object CREATOR : Parcelable.Creator<CurrencyList> {
        override fun createFromParcel(parcel: Parcel): CurrencyList {
            return CurrencyList(parcel)
        }

        override fun newArray(size: Int): Array<CurrencyList?> {
            return arrayOfNulls(size)
        }
    }
}