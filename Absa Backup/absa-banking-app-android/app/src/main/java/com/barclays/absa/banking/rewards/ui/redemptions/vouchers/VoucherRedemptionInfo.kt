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
package com.barclays.absa.banking.rewards.ui.redemptions.vouchers

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.KParcelable

data class VoucherRedemptionInfo(var vendorId: String? = "",
                                 var vendorName: String? = "",
                                 var fixedAmount: Amount? = Amount(),
                                 var cellNumber: String? = "") : KParcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Amount::class.java.classLoader),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vendorId)
        parcel.writeString(vendorName)
        parcel.writeParcelable(fixedAmount, flags)
        parcel.writeString(cellNumber)
    }

    companion object CREATOR : Parcelable.Creator<VoucherRedemptionInfo> {
        override fun createFromParcel(parcel: Parcel): VoucherRedemptionInfo {
            return VoucherRedemptionInfo(parcel)
        }

        override fun newArray(size: Int): Array<VoucherRedemptionInfo?> {
            return arrayOfNulls(size)
        }
    }
}