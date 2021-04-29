/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.flexiFuneral.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class FlexiFuneralData() : KParcelable {
    var disableFlexi: String = ""
    var validAge: String = ""
    var validAgeMin: String = ""
    var validAgeMax: String = ""
    var findOutMorePdfUrl: String = ""
    var termsAndConditionsPdfUrl: String = ""
    var lowestPremium: String = ""
    var casaReference: String = ""

    constructor(parcel: Parcel) : this() {
        disableFlexi = parcel.readString().toString()
        validAge = parcel.readString().toString()
        validAgeMin = parcel.readString().toString()
        validAgeMax = parcel.readString().toString()
        findOutMorePdfUrl = parcel.readString().toString()
        termsAndConditionsPdfUrl = parcel.readString().toString()
        lowestPremium = parcel.readString().toString()
        casaReference = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(disableFlexi)
        parcel.writeString(validAge)
        parcel.writeString(validAgeMin)
        parcel.writeString(validAgeMax)
        parcel.writeString(findOutMorePdfUrl)
        parcel.writeString(termsAndConditionsPdfUrl)
        parcel.writeString(lowestPremium)
        parcel.writeString(casaReference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlexiFuneralData> {
        override fun createFromParcel(parcel: Parcel): FlexiFuneralData {
            return FlexiFuneralData(parcel)
        }

        override fun newArray(size: Int): Array<FlexiFuneralData?> {
            return arrayOfNulls(size)
        }
    }
}