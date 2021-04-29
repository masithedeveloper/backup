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

class InternationalCountryList() : KParcelable, BaseModel {
    var countryCode: String? = ""
    var countryDescription: String? = ""
    var requiresSecurityQuestion: String? = ""

    constructor(parcel: Parcel) : this() {
        countryCode = parcel.readString()
        countryDescription = parcel.readString()
        requiresSecurityQuestion = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(countryCode)
        parcel.writeString(countryDescription)
        parcel.writeString(requiresSecurityQuestion)
    }

    companion object CREATOR : Parcelable.Creator<InternationalCountryList> {
        override fun createFromParcel(parcel: Parcel): InternationalCountryList {
            return InternationalCountryList(parcel)
        }

        override fun newArray(size: Int): Array<InternationalCountryList?> {
            return arrayOfNulls(size)
        }
    }
}