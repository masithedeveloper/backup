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
package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import styleguide.forms.SelectorInterface

data class BuyMoreUnitsLinkedAccount(var accountNumber: String? = "",
                                     var accountType: String? = "",
                                     var accountHolderName: String? = "",
                                     var bankName: String? = "",
                                     var branchCode: String? = "",
                                     var desc: String? = "") : KParcelable, SelectorInterface {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override val displayValue: String?
        get() = "$desc - $accountNumber"

    override val displayValueLine2: String?
        get() = ""

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountNumber)
        parcel.writeString(accountType)
        parcel.writeString(accountHolderName)
        parcel.writeString(bankName)
        parcel.writeString(branchCode)
        parcel.writeString(desc)
    }

    companion object CREATOR : Parcelable.Creator<BuyMoreUnitsLinkedAccount> {
        override fun createFromParcel(parcel: Parcel): BuyMoreUnitsLinkedAccount {
            return BuyMoreUnitsLinkedAccount(parcel)
        }

        override fun newArray(size: Int): Array<BuyMoreUnitsLinkedAccount?> {
            return arrayOfNulls(size)
        }
    }
}