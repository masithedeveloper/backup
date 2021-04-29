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

package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import styleguide.forms.SelectorInterface

class LinkedAccount() : KParcelable, SelectorInterface, BaseModel {

    var accountBalance: String? = ""
    var availableBalance: String? = ""
    var existingOverdraftLimit: String? = ""
    @JsonProperty("unclearedAmt")
    var unclearedAmount: String? = ""
    var accountType: String? = ""
    var accountNumber: String? = ""
    @JsonProperty("acctAccessBits")
    var accountAccessBits: String? = ""
    @JsonProperty("acctAccessTypeBits")
    var accountAccessTypeBits: String? = ""
    var accountHolder: String? = ""
    var bankName: String? = ""
    @JsonProperty("bankCode")
    var branchCode: String? = ""
    @JsonProperty("desc")
    var description: String? = ""

    constructor(parcel: Parcel) : this() {
        accountBalance = parcel.readString()
        availableBalance = parcel.readString()
        existingOverdraftLimit = parcel.readString()
        unclearedAmount = parcel.readString()
        accountType = parcel.readString()
        accountNumber = parcel.readString()
        accountAccessBits = parcel.readString()
        accountAccessTypeBits = parcel.readString()
        accountHolder = parcel.readString()
        bankName = parcel.readString()
        branchCode = parcel.readString()
        description = parcel.readString()
    }

    override val displayValue: String?
        get() = "$description - $accountNumber"

    override val displayValueLine2: String?
        get() = ""

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountBalance)
        parcel.writeString(availableBalance)
        parcel.writeString(existingOverdraftLimit)
        parcel.writeString(unclearedAmount)
        parcel.writeString(accountType)
        parcel.writeString(accountNumber)
        parcel.writeString(accountAccessBits)
        parcel.writeString(accountAccessTypeBits)
        parcel.writeString(accountHolder)
        parcel.writeString(bankName)
        parcel.writeString(branchCode)
        parcel.writeString(description)
    }

    companion object CREATOR : Parcelable.Creator<LinkedAccount> {
        override fun createFromParcel(parcel: Parcel): LinkedAccount {
            return LinkedAccount(parcel)
        }

        override fun newArray(size: Int): Array<LinkedAccount?> {
            return arrayOfNulls(size)
        }
    }
}