/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.funeralCover.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class RetailAccount() : TransactionResponse(), KParcelable {

    var accountType: String? = ""

    var accountNumber: String? = ""

    @JsonProperty("desc")
    var accountDescription: String? = ""

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountType)
        parcel.writeString(accountNumber)
        parcel.writeString(accountDescription)
    }

    constructor(parcel: Parcel) : this() {
        accountType = parcel.readString()
        accountNumber = parcel.readString()
        accountDescription = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<RetailAccount> {
        override fun createFromParcel(parcel: Parcel): RetailAccount {
            return RetailAccount(parcel)
        }

        override fun newArray(size: Int): Array<RetailAccount?> {
            return arrayOfNulls(size)
        }
    }

}