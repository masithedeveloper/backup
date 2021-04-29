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

data class TaxInfo(var isRegisteredForSATax: String? = "",
                   var saTaxNumber: String? = "",
                   var reasonNotGivenForSATax: String? = "",
                   var isRegisteredForForeignTax: String? = "",
                   var foreignTaxNumber: String? = "",
                   var reasonNotGivenForForeignTax: String? = "",
                   var foreignTaxCountry: String? = "") : KParcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(isRegisteredForSATax)
        parcel.writeString(saTaxNumber)
        parcel.writeString(reasonNotGivenForSATax)
        parcel.writeString(isRegisteredForForeignTax)
        parcel.writeString(foreignTaxNumber)
        parcel.writeString(reasonNotGivenForForeignTax)
        parcel.writeString(foreignTaxCountry)
    }

    companion object CREATOR : Parcelable.Creator<TaxInfo> {
        override fun createFromParcel(parcel: Parcel): TaxInfo {
            return TaxInfo(parcel)
        }

        override fun newArray(size: Int): Array<TaxInfo?> {
            return arrayOfNulls(size)
        }
    }
}