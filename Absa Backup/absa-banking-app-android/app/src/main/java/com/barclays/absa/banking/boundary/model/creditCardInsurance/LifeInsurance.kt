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
package com.barclays.absa.banking.boundary.model.creditCardInsurance

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable

class LifeInsurance() : TransactionResponse(), KParcelable {
    var coverAmount: String? = null
    var outstandingPremiumAmount: String? = null
    var monthlyPremium: String? = null
    var loadingAmount: String? = null
    var sasPremium: String? = null
    var planCounter: String? = null
    var spousePlanCounter: String? = null
    var spouseSumAssured: String? = null
    var spouseBenefitPremium: String? = null
    var firstHHorHO: String? = null
    var totalCoverAmount: String? = null
    var totalPremium: String? = null
    var screenId: String? = null
    var policyExist: String? = null
    var policyNumber: String? = null
    var commencementDate: String? = null
    var rolePlayers: List<RolePlayers>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        coverAmount = parcel.readString()
        outstandingPremiumAmount = parcel.readString()
        monthlyPremium = parcel.readString()
        loadingAmount = parcel.readString()
        sasPremium = parcel.readString()
        planCounter = parcel.readString()
        spousePlanCounter = parcel.readString()
        spouseSumAssured = parcel.readString()
        spouseBenefitPremium = parcel.readString()
        firstHHorHO = parcel.readString()
        totalCoverAmount = parcel.readString()
        totalPremium = parcel.readString()
        screenId = parcel.readString()
        policyExist = parcel.readString()
        policyNumber = parcel.readString()
        commencementDate = parcel.readString()
        rolePlayers = parcel.createTypedArrayList(RolePlayers.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(coverAmount)
        parcel.writeString(outstandingPremiumAmount)
        parcel.writeString(monthlyPremium)
        parcel.writeString(loadingAmount)
        parcel.writeString(sasPremium)
        parcel.writeString(planCounter)
        parcel.writeString(spousePlanCounter)
        parcel.writeString(spouseSumAssured)
        parcel.writeString(spouseBenefitPremium)
        parcel.writeString(firstHHorHO)
        parcel.writeString(totalCoverAmount)
        parcel.writeString(totalPremium)
        parcel.writeString(screenId)
        parcel.writeString(policyExist)
        parcel.writeString(policyNumber)
        parcel.writeString(commencementDate)
        parcel.writeTypedList(rolePlayers)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<LifeInsurance> {
            override fun createFromParcel(parcel: Parcel): LifeInsurance {
                return LifeInsurance(parcel)
            }

            override fun newArray(size: Int): Array<LifeInsurance?> {
                return arrayOfNulls(size)
            }
        }
    }
}
