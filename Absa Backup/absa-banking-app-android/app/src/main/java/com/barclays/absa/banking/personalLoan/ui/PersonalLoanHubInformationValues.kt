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
package com.barclays.absa.banking.personalLoan.ui

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class PersonalLoanHubInformationValues() : KParcelable {
    val accountNumber: String = ""
    @JsonProperty("arrearAdvanceAmount")
    var arrearsAdvanceAmount: Double = 0.0
    var instalment: String = ""
    var interestRate: String = ""
    var contractDate: String = ""
    var term: String = ""
    var remainingTerm: String = ""
    var repaymentDay: String = ""

    constructor(parcel: Parcel) : this() {
        arrearsAdvanceAmount = parcel.readDouble()
        parcel.readString()?.let { instalment = it }
        parcel.readString()?.let { interestRate = it }
        parcel.readString()?.let { contractDate = it }
        parcel.readString()?.let { term = it }
        parcel.readString()?.let { remainingTerm = it }
        parcel.readString()?.let { repaymentDay = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(arrearsAdvanceAmount)
        parcel.writeString(instalment)
        parcel.writeString(interestRate)
        parcel.writeString(contractDate)
        parcel.writeString(term)
        parcel.writeString(remainingTerm)
        parcel.writeString(repaymentDay)
    }

    companion object CREATOR : Parcelable.Creator<PersonalLoanHubInformationValues> {
        override fun createFromParcel(parcel: Parcel) = PersonalLoanHubInformationValues(parcel)

        override fun newArray(size: Int) = arrayOfNulls<PersonalLoanHubInformationValues?>(size)
    }
}