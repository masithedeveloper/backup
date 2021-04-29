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
import com.fasterxml.jackson.annotation.JsonProperty

class UnitTrustFund() : KParcelable {
    var fundCode: String? = ""
    var fundName: String? = ""
    var fundCategory: String? = ""
    var fundBalance: String? = ""
    @JsonProperty("fundAvlBalance")
    var fundAvailableBalance: String? = ""
    var fundUnits: String? = ""
    @JsonProperty("fundAvlUnits")
    var fundAvailablelUnits: String? = ""
    var minLumpSumAmount: String? = ""
    var minDebitOrderAmount: String? = ""
    var riskProfile: String? = ""
    var fundObjective: String? = ""
    var fundPdfUrl: String? = ""
    var fundRisk: String? = ""
    var fundTerm: String? = ""
    var fundSuitableFor: String? = ""
    var fundCore: String? = ""

    constructor(parcel: Parcel) : this() {
        fundCode = parcel.readString()
        fundName = parcel.readString()
        fundCategory = parcel.readString()
        fundBalance = parcel.readString()
        fundAvailableBalance = parcel.readString()
        fundUnits = parcel.readString()
        fundAvailablelUnits = parcel.readString()
        minLumpSumAmount = parcel.readString()
        minDebitOrderAmount = parcel.readString()
        riskProfile = parcel.readString()
        fundObjective = parcel.readString()
        fundPdfUrl = parcel.readString()
        fundRisk = parcel.readString()
        fundTerm = parcel.readString()
        fundSuitableFor = parcel.readString()
        fundCore = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fundCode)
        parcel.writeString(fundName)
        parcel.writeString(fundCategory)
        parcel.writeString(fundBalance)
        parcel.writeString(fundAvailableBalance)
        parcel.writeString(fundUnits)
        parcel.writeString(fundAvailablelUnits)
        parcel.writeString(minLumpSumAmount)
        parcel.writeString(minDebitOrderAmount)
        parcel.writeString(riskProfile)
        parcel.writeString(fundObjective)
        parcel.writeString(fundPdfUrl)
        parcel.writeString(fundRisk)
        parcel.writeString(fundTerm)
        parcel.writeString(fundSuitableFor)
        parcel.writeString(fundCore)
    }

    companion object CREATOR : Parcelable.Creator<UnitTrustFund> {
        override fun createFromParcel(parcel: Parcel): UnitTrustFund {
            return UnitTrustFund(parcel)
        }

        override fun newArray(size: Int): Array<UnitTrustFund?> {
            return arrayOfNulls(size)
        }
    }
}