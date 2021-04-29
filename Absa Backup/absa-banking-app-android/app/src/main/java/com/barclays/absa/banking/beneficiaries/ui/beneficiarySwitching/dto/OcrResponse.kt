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
package com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.OcrRectangle
import com.barclays.absa.banking.framework.KParcelable

class OcrResponse() : KParcelable {

    var theirReference: String? = ""
    var theirReferenceSuspicious: Boolean? = false
    var nameSuspicious: Boolean? = false
    val theirReferenceRectangle: OcrRectangle? = null
    var myReference: String? = ""
    var branchCodeSuspicious: Boolean? = false
    var accountNumberSuspicious: Boolean? = false
    val nameRectangle: OcrRectangle? = null
    val version: String? = ""
    var name: String? = ""
    val bankRectangle: OcrRectangle? = null
    var beneficiaryExtractor: String? = ""
    var accountNumber: String? = ""
    val accountNumberRectangle: OcrRectangle? = null
    var bankSuspicious: Boolean? = false
    var myReferenceSuspicious: Boolean? = false
    var branchCode: String? = ""
    val myReferenceRectangle: OcrRectangle? = null
    val branchCodeRectangle: OcrRectangle? = null
    var bank: String? = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        nameSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        bank = parcel.readString()
        bankSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        branchCode = parcel.readString()
        branchCodeSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        accountNumber = parcel.readString()
        accountNumberSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        theirReference = parcel.readString()
        theirReferenceSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        myReference = parcel.readString()
        myReferenceSuspicious = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        beneficiaryExtractor = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(nameSuspicious)
        parcel.writeString(bank)
        parcel.writeValue(bankSuspicious)
        parcel.writeString(branchCode)
        parcel.writeValue(branchCodeSuspicious)
        parcel.writeString(accountNumber)
        parcel.writeValue(accountNumberSuspicious)
        parcel.writeString(theirReference)
        parcel.writeValue(theirReferenceSuspicious)
        parcel.writeString(myReference)
        parcel.writeValue(myReferenceSuspicious)
        parcel.writeString(beneficiaryExtractor)
    }

    companion object CREATOR : Parcelable.Creator<OcrResponse> {
        override fun createFromParcel(parcel: Parcel): OcrResponse {
            return OcrResponse(parcel)
        }

        override fun newArray(size: Int): Array<OcrResponse?> {
            return arrayOfNulls(size)
        }
    }

}