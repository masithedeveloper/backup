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

class UnitTrustRedemptionAccountDetails(var redemptionAccountNumber: String = "",
                                        var redemptionAccountHolder: String = "",
                                        var redemptionAccountBankName: String = "",
                                        var redemptionAccountBankCode: String = "",
                                        var redemptionAccountType: String = "") : KParcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(redemptionAccountNumber)
        parcel.writeString(redemptionAccountHolder)
        parcel.writeString(redemptionAccountBankName)
        parcel.writeString(redemptionAccountBankCode)
        parcel.writeString(redemptionAccountType)
    }

    companion object CREATOR : Parcelable.Creator<UnitTrustRedemptionAccountDetails> {
        override fun createFromParcel(parcel: Parcel): UnitTrustRedemptionAccountDetails {
            return UnitTrustRedemptionAccountDetails(parcel)
        }

        override fun newArray(size: Int): Array<UnitTrustRedemptionAccountDetails?> {
            return arrayOfNulls(size)
        }
    }
}