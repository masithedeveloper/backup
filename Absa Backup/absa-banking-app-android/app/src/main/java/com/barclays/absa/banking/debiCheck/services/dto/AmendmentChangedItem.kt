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
package com.barclays.absa.banking.debiCheck.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class AmendmentChangedItem() : KParcelable {
    var changedFieldName: String = ""
    var newChangedFieldValue: String = ""
    var oldChangedFieldValue: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { changedFieldName = it }
        parcel.readString()?.let { newChangedFieldValue = it }
        parcel.readString()?.let { oldChangedFieldValue = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(changedFieldName)
        parcel.writeString(newChangedFieldValue)
        parcel.writeString(oldChangedFieldValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AmendmentChangedItem> {
        override fun createFromParcel(parcel: Parcel): AmendmentChangedItem {
            return AmendmentChangedItem(parcel)
        }

        override fun newArray(size: Int): Array<AmendmentChangedItem?> {
            return arrayOfNulls(size)
        }
    }
}