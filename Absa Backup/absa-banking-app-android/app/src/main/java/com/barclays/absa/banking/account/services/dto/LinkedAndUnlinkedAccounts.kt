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
package com.barclays.absa.banking.account.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject

class LinkedAndUnlinkedAccounts() : ResponseObject(), KParcelable {
    var accountList: List<ManageAccounts>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        accountList = parcel.createTypedArrayList(ManageAccounts)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(accountList)
    }

    companion object CREATOR : Parcelable.Creator<LinkedAndUnlinkedAccounts> {
        override fun createFromParcel(parcel: Parcel): LinkedAndUnlinkedAccounts {
            return LinkedAndUnlinkedAccounts(parcel)
        }

        override fun newArray(size: Int): Array<LinkedAndUnlinkedAccounts?> {
            return arrayOfNulls(size)
        }
    }
}
