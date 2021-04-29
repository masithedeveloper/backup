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

data class IncomeDistributionInfo(var incomeAccountsNumber: String? = "",
                                  var incomeAccountsHolderName: String? = "",
                                  var incomeType: String? = "",
                                  var incomeAccountType: String? = "",
                                  var incomeBankCode: String? = ABSA_BRANCH_CODE,
                                  var description: String? = "") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(incomeAccountsNumber)
        parcel.writeString(incomeAccountsHolderName)
        parcel.writeString(incomeType)
        parcel.writeString(incomeAccountType)
        parcel.writeString(incomeBankCode)
        parcel.writeString(description)
    }

    companion object CREATOR : Parcelable.Creator<IncomeDistributionInfo> {
        const val ABSA_BRANCH_CODE = "632005"
        const val ABSA_BRANCH_NAME = "ABSA BANK"
        override fun createFromParcel(parcel: Parcel): IncomeDistributionInfo {
            return IncomeDistributionInfo(parcel)
        }

        override fun newArray(size: Int): Array<IncomeDistributionInfo?> {
            return arrayOfNulls(size)
        }
    }
}
