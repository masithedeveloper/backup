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

package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class UnitTrustData() : KParcelable {
    var hasUnitTrustAccount: Boolean? = true
    var allFundsMinLumpSumAmt: String? = ""
    var allFundsMinDebitOrderAmt: String? = ""
    var validateClientStatus: Boolean? = true

    constructor(parcel: Parcel) : this() {
        hasUnitTrustAccount = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        allFundsMinLumpSumAmt = parcel.readString()
        allFundsMinDebitOrderAmt = parcel.readString()
        validateClientStatus = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(hasUnitTrustAccount)
        parcel.writeString(allFundsMinLumpSumAmt)
        parcel.writeString(allFundsMinDebitOrderAmt)
        parcel.writeValue(validateClientStatus)
    }

    companion object CREATOR : Parcelable.Creator<UnitTrustData> {
        override fun createFromParcel(parcel: Parcel): UnitTrustData {
            return UnitTrustData(parcel)
        }

        override fun newArray(size: Int): Array<UnitTrustData?> {
            return arrayOfNulls(size)
        }
    }
}