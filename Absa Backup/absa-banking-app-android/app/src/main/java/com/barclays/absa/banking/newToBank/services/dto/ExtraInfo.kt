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

package com.barclays.absa.banking.newToBank.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class ExtraInfo() : KParcelable {
    var title: String = ""
    var bulletPoints: List<String> = mutableListOf()

    constructor(parcel: Parcel) : this() {
        parcel.apply {
            title = readString() ?: ""
            bulletPoints = createStringArrayList() ?: mutableListOf()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(title)
            writeStringList(bulletPoints)
        }
    }

    companion object CREATOR : Parcelable.Creator<ExtraInfo> {
        override fun createFromParcel(parcel: Parcel): ExtraInfo = ExtraInfo(parcel)
        override fun newArray(size: Int): Array<ExtraInfo?> = arrayOfNulls(size)
    }
}