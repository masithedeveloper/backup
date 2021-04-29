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

class CardPackage() : KParcelable {
    var packages: List<Packages> = mutableListOf()
    var packageName: String = ""
    var minimumIncome: String = ""
    var description: String = ""
    var monthlyFee: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.apply {
            packages = createTypedArrayList(Packages.CREATOR) ?: mutableListOf()
            packageName = readString() ?: ""
            minimumIncome = readString() ?: ""
            description = readString() ?: ""
            monthlyFee = readString() ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeTypedList(packages)
            writeString(packageName)
            writeString(minimumIncome)
            writeString(description)
            writeString(monthlyFee)
        }
    }

    companion object CREATOR : Parcelable.Creator<CardPackage> {
        override fun createFromParcel(parcel: Parcel): CardPackage = CardPackage(parcel)
        override fun newArray(size: Int): Array<CardPackage?> = arrayOfNulls(size)
    }
}
