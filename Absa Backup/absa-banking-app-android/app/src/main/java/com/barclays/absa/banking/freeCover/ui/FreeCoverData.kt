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
package com.barclays.absa.banking.freeCover.ui

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class FreeCoverData() : KParcelable {
    var status: String = ""
    var appliedCount: String = ""
    var disableFreeCover: String = ""

    @JsonProperty("findoutMoreURL")
    var findOutMoreURL: String = ""
    var termsAndConditionsURL: String = ""

    constructor(parcel: Parcel) : this() {
        status = parcel.readString().toString()
        appliedCount = parcel.readString().toString()
        disableFreeCover = parcel.readString().toString()
        findOutMoreURL = parcel.readString().toString()
        termsAndConditionsURL = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(status)
            writeString(appliedCount)
            writeString(disableFreeCover)
            writeString(findOutMoreURL)
            writeString(termsAndConditionsURL)
        }
    }

    companion object CREATOR : Parcelable.Creator<FreeCoverData> {
        override fun createFromParcel(parcel: Parcel): FreeCoverData = FreeCoverData(parcel)
        override fun newArray(size: Int): Array<FreeCoverData?> = arrayOfNulls(size)
    }
}