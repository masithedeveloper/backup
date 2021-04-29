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
import com.barclays.absa.banking.shared.BaseModel

class ManageAccounts() : KParcelable, BaseModel {
    var accountNumber: String? = ""
    var accountType: String? = ""
    var accountName: String? = ""
    var accountLinked: Boolean? = true
    var accessAccount: Boolean? = true
    var linkingOrUnlinkingPossible: Boolean? = true

    constructor(parcel: Parcel) : this() {
        accountNumber = parcel.readString()
        accountType = parcel.readString()
        accountName = parcel.readString()
        accountLinked = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        accessAccount = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        linkingOrUnlinkingPossible = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountNumber)
        parcel.writeString(accountType)
        parcel.writeString(accountName)
        parcel.writeValue(accountLinked)
        parcel.writeValue(accessAccount)
        parcel.writeValue(linkingOrUnlinkingPossible)
    }

    companion object CREATOR : Parcelable.Creator<ManageAccounts> {
        override fun createFromParcel(parcel: Parcel): ManageAccounts {
            return ManageAccounts(parcel)
        }

        override fun newArray(size: Int): Array<ManageAccounts?> {
            return arrayOfNulls(size)
        }
    }

    class LinkAndUnlinkRequestStates() : KParcelable {
        var newUnlinkedAccounts: String? = ""
        var newLinkedAccounts: String? = ""
        var newUnlinkedAccountTypes: String? = ""
        var newLinkedAccountTypes: String? = ""

        constructor(parcel: Parcel) : this() {
            newUnlinkedAccounts = parcel.readString()
            newLinkedAccounts = parcel.readString()
            newUnlinkedAccountTypes = parcel.readString()
            newLinkedAccountTypes = parcel.readString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(newUnlinkedAccounts)
            parcel.writeString(newLinkedAccounts)
            parcel.writeString(newUnlinkedAccountTypes)
            parcel.writeString(newLinkedAccountTypes)
        }

        companion object CREATOR : Parcelable.Creator<LinkAndUnlinkRequestStates> {
            override fun createFromParcel(parcel: Parcel): LinkAndUnlinkRequestStates {
                return LinkAndUnlinkRequestStates(parcel)
            }

            override fun newArray(size: Int): Array<LinkAndUnlinkRequestStates?> {
                return arrayOfNulls(size)
            }
        }
    }

    class ShortAccountList {
        var accountNumber: String? = ""
        var accountLinked: Boolean? = true
    }
}