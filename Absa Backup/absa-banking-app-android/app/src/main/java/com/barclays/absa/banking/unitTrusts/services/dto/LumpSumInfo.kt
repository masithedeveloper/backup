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

package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

data class LumpSumInfo(var accountInfo: LinkedAccount? = null,
                       var amount: String? = "",
                       var indicator: String? = "N") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(LinkedAccount::class.java.classLoader),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(accountInfo, flags)
        parcel.writeString(amount)
        parcel.writeString(indicator)
    }

    companion object CREATOR : Parcelable.Creator<LumpSumInfo> {
        override fun createFromParcel(parcel: Parcel): LumpSumInfo {
            return LumpSumInfo(parcel)
        }

        override fun newArray(size: Int): Array<LumpSumInfo?> {
            return arrayOfNulls(size)
        }
    }
}

