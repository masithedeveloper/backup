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
package com.barclays.absa.banking.lawForYou.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class ApplyLawForYou() : KParcelable {

    var disableLawForYou: Boolean = false
    var validAge: Boolean = false
    var findOutMorePdfUrl: String = ""
    var termsAndConditionsPdfUrl: String = ""
    var coverFactsheetPdfUrl: String = ""
    var lowestPremium: String = ""
    @JsonProperty("debitOrderAuthTCPdfUrl")
    var debitOrderAuthorizationTermsAndConditionsPdfUrl: String = ""

    constructor(parcel: Parcel) : this() {
        disableLawForYou = parcel.readValue(Boolean::class.java.classLoader) as Boolean
        validAge = parcel.readValue(Boolean::class.java.classLoader) as Boolean
        findOutMorePdfUrl = parcel.readString() ?: ""
        termsAndConditionsPdfUrl = parcel.readString() ?: ""
        coverFactsheetPdfUrl = parcel.readString() ?: ""
        lowestPremium = parcel.readString() ?: ""
        debitOrderAuthorizationTermsAndConditionsPdfUrl = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeValue(disableLawForYou)
            writeValue(validAge)
            writeString(findOutMorePdfUrl)
            writeString(termsAndConditionsPdfUrl)
            writeString(coverFactsheetPdfUrl)
            writeString(lowestPremium)
            writeString(debitOrderAuthorizationTermsAndConditionsPdfUrl)
        }
    }

    companion object CREATOR : Parcelable.Creator<ApplyLawForYou> {
        override fun createFromParcel(parcel: Parcel): ApplyLawForYou = ApplyLawForYou(parcel)
        override fun newArray(size: Int): Array<ApplyLawForYou?> = arrayOfNulls(size)
    }
}