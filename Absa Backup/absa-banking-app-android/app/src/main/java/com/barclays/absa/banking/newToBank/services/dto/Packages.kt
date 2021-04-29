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
import com.fasterxml.jackson.annotation.JsonProperty

class Packages() : KParcelable {
    @JsonProperty("package")
    var accountPackage: String = ""
    var bulletPoints: List<String> = mutableListOf()
    var extraInfo: ExtraInfo = ExtraInfo()
    var asterix: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.apply {
            accountPackage = readString() ?: ""
            bulletPoints = createStringArrayList() ?: mutableListOf()
            extraInfo = readParcelable(ExtraInfo::class.java.classLoader)!!
            asterix = readString() ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(accountPackage)
            writeStringList(bulletPoints)
            writeParcelable(extraInfo, flags)
            writeString(asterix)
        }
    }

    companion object CREATOR : Parcelable.Creator<Packages> {
        override fun createFromParcel(parcel: Parcel): Packages = Packages(parcel)
        override fun newArray(size: Int): Array<Packages?> = arrayOfNulls(size)
    }
}