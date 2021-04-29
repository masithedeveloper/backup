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
package lotto.jackpot

import android.os.Parcel
import android.os.Parcelable

class JackpotInfo() : Parcelable {
    var drawType = ""
    var jackpot = ""
    var jackpotType = ""

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { drawType = it }
        parcel.readString()?.let { jackpot = it }
        parcel.readString()?.let { jackpotType = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(drawType)
        parcel.writeString(jackpot)
        parcel.writeString(jackpotType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JackpotInfo> {
        override fun createFromParcel(parcel: Parcel): JackpotInfo {
            return JackpotInfo(parcel)
        }

        override fun newArray(size: Int): Array<JackpotInfo?> {
            return arrayOfNulls(size)
        }
    }
}