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
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.KParcelable

class ManageAccountsResponse() : SureCheckResponse(), KParcelable {

    var accountList: List<AccountFailure>? = listOf()
    var linkingSuccessful: Boolean? = true
    var reorderSuccessful: Boolean? = true

    constructor(parcel: Parcel) : this() {
        accountList = parcel.createTypedArrayList(AccountFailure)
        linkingSuccessful = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        reorderSuccessful = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    class AccountFailure() : KParcelable {
        var accountNumber: String? = ""
        var accountType: String? = ""
        var accountName: String? = ""
        var action: String? = ""
        var defaultDescription: String? = ""
        var returnCode: String? = ""

        constructor(parcel: Parcel) : this() {
            accountNumber = parcel.readString()
            accountType = parcel.readString()
            accountName = parcel.readString()
            action = parcel.readString()
            defaultDescription = parcel.readString()
            returnCode = parcel.readString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(accountNumber)
            parcel.writeString(accountType)
            parcel.writeString(accountName)
            parcel.writeString(action)
            parcel.writeString(defaultDescription)
            parcel.writeString(returnCode)
        }

        companion object CREATOR : Parcelable.Creator<AccountFailure> {
            override fun createFromParcel(parcel: Parcel): AccountFailure {
                return AccountFailure(parcel)
            }

            override fun newArray(size: Int): Array<AccountFailure?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(accountList)
        parcel.writeValue(linkingSuccessful)
        parcel.writeValue(reorderSuccessful)
    }

    companion object CREATOR : Parcelable.Creator<ManageAccountsResponse> {
        override fun createFromParcel(parcel: Parcel): ManageAccountsResponse {
            return ManageAccountsResponse(parcel)
        }

        override fun newArray(size: Int): Array<ManageAccountsResponse?> {
            return arrayOfNulls(size)
        }
    }
}