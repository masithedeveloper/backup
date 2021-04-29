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

data class UltimateProtectorData(var disableUP: Boolean? = true,
                                 var errorCode: String? = "",
                                 var lowestPremium: String? = "",
                                 var findOutMorePdfUrl: String? = "",
                                 var casaReference: String? = "") : KParcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(disableUP)
        parcel.writeString(errorCode)
        parcel.writeString(lowestPremium)
        parcel.writeString(findOutMorePdfUrl)
        parcel.writeString(casaReference)
    }

    companion object CREATOR : Parcelable.Creator<UltimateProtectorData> {
        override fun createFromParcel(parcel: Parcel): UltimateProtectorData {
            return UltimateProtectorData(parcel)
        }

        override fun newArray(size: Int): Array<UltimateProtectorData?> {
            return arrayOfNulls(size)
        }
    }
}