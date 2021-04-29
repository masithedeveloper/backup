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
package com.barclays.absa.banking.boundary.model.rewards

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.KParcelable

data class Voucher(val vendorName: String = "",
                   val subscriptionPeriod: String = "",
                   val subscriptionAmount: Amount? = null,
                   val fixedAmount: Amount? = null,
                   val voucherType: String = "",
                   val voucherDisclaimer: String = "") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readParcelable(Amount::class.java.classLoader),
            parcel.readParcelable(Amount::class.java.classLoader),
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vendorName)
        parcel.writeString(subscriptionPeriod)
        parcel.writeParcelable(subscriptionAmount, flags)
        parcel.writeParcelable(fixedAmount, flags)
        parcel.writeString(voucherType)
        parcel.writeString(voucherDisclaimer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Voucher> {
        override fun createFromParcel(parcel: Parcel): Voucher {
            return Voucher(parcel)
        }

        override fun newArray(size: Int): Array<Voucher?> {
            return arrayOfNulls(size)
        }
    }
}