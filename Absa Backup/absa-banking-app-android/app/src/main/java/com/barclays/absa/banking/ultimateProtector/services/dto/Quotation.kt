/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.ultimateProtector.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable

class Quotation() : KParcelable, TransactionResponse() {

    var findOutMorePdfUrl: String? = ""
    var termsAndConditionsPdfUrl: String? = ""
    var benefitPdfUrl: String? = ""
    var sliderPremiums: List<Premium> = emptyList()
    var benefitQualifyText: String? = ""
    var benefitCoverText1: String? = ""
    var benefitCoverText2: String? = ""

    constructor(parcel: Parcel) : this() {
        findOutMorePdfUrl = parcel.readString()
        termsAndConditionsPdfUrl = parcel.readString()
        benefitPdfUrl = parcel.readString()
        sliderPremiums = parcel.createTypedArrayList(Premium) as List<Premium>
        benefitQualifyText = parcel.readString()
        benefitCoverText1 = parcel.readString()
        benefitCoverText2 = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(findOutMorePdfUrl)
        parcel.writeString(termsAndConditionsPdfUrl)
        parcel.writeString(benefitPdfUrl)
        parcel.writeTypedList(sliderPremiums)
        parcel.writeString(benefitQualifyText)
        parcel.writeString(benefitCoverText1)
        parcel.writeString(benefitCoverText2)
    }

    companion object CREATOR : Parcelable.Creator<Quotation> {
        override fun createFromParcel(parcel: Parcel): Quotation {
            return Quotation(parcel)
        }

        override fun newArray(size: Int): Array<Quotation?> {
            return arrayOfNulls(size)
        }
    }
}
