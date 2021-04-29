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
package com.barclays.absa.banking.boundary.model

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject

class ReorderAccountObject() : ResponseObject(), KParcelable {

    var txnStatus: String? = ""

    constructor(parcel: Parcel) : this() {
        txnStatus = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(txnStatus)
    }

    companion object CREATOR : Parcelable.Creator<ReorderAccountObject> {
        override fun createFromParcel(parcel: Parcel): ReorderAccountObject {
            return ReorderAccountObject(parcel)
        }

        override fun newArray(size: Int): Array<ReorderAccountObject?> {
            return arrayOfNulls(size)
        }
    }
}
