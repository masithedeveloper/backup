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

package com.barclays.absa.banking.funeralCover.ui

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable

class ChangePaymentDetails() : TransactionResponse(), KParcelable {
    var policyNumber: String = ""
    var accountHolderName: String = ""
    var bankName: String = ""
    var accountNumber: String = ""
    var branchName: String = ""
    var accountType: String = ""
    var dayOfDebit: String = ""
    var sourceOfFund: String = ""
    var branchCode: String = ""
    var itemCode: String = ""
    var nextPremiumDate: String = ""
    var bankId: String = ""
    var branchId: String = ""

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { policyNumber = it }
        parcel.readString()?.let { accountHolderName = it }
        parcel.readString()?.let { bankName = it }
        parcel.readString()?.let { accountNumber = it }
        parcel.readString()?.let { branchName = it }
        parcel.readString()?.let { accountType = it }
        parcel.readString()?.let { dayOfDebit = it }
        parcel.readString()?.let { sourceOfFund = it }
        parcel.readString()?.let { branchCode = it }
        parcel.readString()?.let { itemCode = it }
        parcel.readString()?.let { nextPremiumDate = it }
        parcel.readString()?.let { bankId = it }
        parcel.readString()?.let { branchId = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(policyNumber)
        parcel.writeString(accountHolderName)
        parcel.writeString(bankName)
        parcel.writeString(accountNumber)
        parcel.writeString(branchName)
        parcel.writeString(accountType)
        parcel.writeString(dayOfDebit)
        parcel.writeString(sourceOfFund)
        parcel.writeString(branchCode)
        parcel.writeString(itemCode)
        parcel.writeString(nextPremiumDate)
        parcel.writeString(bankId)
        parcel.writeString(branchId)
    }

    companion object CREATOR : Parcelable.Creator<ChangePaymentDetails> {
        override fun createFromParcel(parcel: Parcel): ChangePaymentDetails {
            return ChangePaymentDetails(parcel)
        }

        override fun newArray(size: Int): Array<ChangePaymentDetails?> {
            return arrayOfNulls(size)
        }
    }
}