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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import java.io.Serializable

class BureauDebtInstalments() : Serializable, KParcelable {
    var bureauCategory: String? = null
    var bureauCategoryAmount: String? = "0.0"

    constructor(parcel: Parcel) : this() {
        bureauCategory = parcel.readString()
        bureauCategoryAmount = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bureauCategory)
        parcel.writeString(bureauCategoryAmount)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BureauDebtInstalments> {
            override fun createFromParcel(parcel: Parcel): BureauDebtInstalments {
                return BureauDebtInstalments(parcel)
            }

            override fun newArray(size: Int): Array<BureauDebtInstalments?> {
                return arrayOfNulls(size)
            }
        }
    }
}