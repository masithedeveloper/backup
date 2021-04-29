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

data class RedeemAccountInfo(var redeemAccountsNumber: String? = "",
                             var redeemAccountsHolderName: String? = "",
                             var redeemBankName: String? = IncomeDistributionInfo.ABSA_BRANCH_NAME,
                             var redeemBankCode: String? = IncomeDistributionInfo.ABSA_BRANCH_CODE,
                             var redeemAccountType: String? = "") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(redeemAccountsNumber)
        parcel.writeString(redeemAccountsHolderName)
        parcel.writeString(redeemBankName)
        parcel.writeString(redeemBankCode)
        parcel.writeString(redeemAccountType)
    }

    companion object CREATOR : Parcelable.Creator<RedeemAccountInfo> {
        override fun createFromParcel(parcel: Parcel): RedeemAccountInfo {
            return RedeemAccountInfo(parcel)
        }

        override fun newArray(size: Int): Array<RedeemAccountInfo?> {
            return arrayOfNulls(size)
        }
    }
}
