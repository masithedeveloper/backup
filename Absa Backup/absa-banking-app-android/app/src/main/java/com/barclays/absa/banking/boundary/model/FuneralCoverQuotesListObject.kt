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
package com.barclays.absa.banking.boundary.model

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class FuneralCoverQuotesListObject() : ResponseObject(), KParcelable, Comparable<FuneralCoverQuotesListObject> {

    @JsonProperty("monthlyPremium")
    var premiumAmount: String? = ""
    var coverAmount: String? = ""
    var planCode: String? = ""
    var referenceNumber: String? = ""

    var spouseBenefitPremium: String? = ""
    @JsonProperty("spouseSumAssured")
    var spouseCoverAmount: String? = ""
    var spousePlanCode: String? = ""
    var selfCoverAmount: String? = ""
    var selfPremium: String? = ""

    constructor(parcel: Parcel) : this() {
        premiumAmount = parcel.readString()
        coverAmount = parcel.readString()
        planCode = parcel.readString()
        referenceNumber = parcel.readString()
        spouseBenefitPremium = parcel.readString()
        spouseCoverAmount = parcel.readString()
        spousePlanCode = parcel.readString()
        selfCoverAmount = parcel.readString()
        selfPremium = parcel.readString()
    }

    override fun compareTo(other: FuneralCoverQuotesListObject): Int {
        try {
            val premiumAmount1 = java.lang.Double.parseDouble(this.premiumAmount!!)
            val premiumAmount2 = java.lang.Double.parseDouble(other.premiumAmount!!)
            return if (premiumAmount1 < premiumAmount2) 1 else -1
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(premiumAmount)
        parcel.writeString(coverAmount)
        parcel.writeString(planCode)
        parcel.writeString(referenceNumber)
        parcel.writeString(spouseBenefitPremium)
        parcel.writeString(spouseCoverAmount)
        parcel.writeString(spousePlanCode)
        parcel.writeString(selfCoverAmount)
        parcel.writeString(selfPremium)
    }

    companion object CREATOR : Parcelable.Creator<FuneralCoverQuotesListObject> {
        override fun createFromParcel(parcel: Parcel): FuneralCoverQuotesListObject {
            return FuneralCoverQuotesListObject(parcel)
        }

        override fun newArray(size: Int): Array<FuneralCoverQuotesListObject?> {
            return arrayOfNulls(size)
        }
    }
}
