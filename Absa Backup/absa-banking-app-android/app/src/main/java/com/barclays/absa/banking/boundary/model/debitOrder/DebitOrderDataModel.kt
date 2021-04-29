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
package com.barclays.absa.banking.boundary.model.debitOrder

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class DebitOrderDataModel() : KParcelable {

    var fromAccountNumber: String? = ""
    var fromDate: String? = ""
    var toDate: String? = ""
    var filterByType: String? = ""
    var actionDate: String? = ""
    var userReference: String? = ""
    var debitOrderStatus: String? = ""
    var userCode: String? = ""
    var userSequence: String? = ""
    var tiebNumber: String? = ""
    var instructionNumber: String? = ""
    var stopPaymentType: String? = ""
    var debitType: String? = ""
    var amount: String? = ""
    var reasonCode: String? = ""
    var reasonDescription: String? = ""

    constructor(parcel: Parcel) : this() {
        fromAccountNumber = parcel.readString()
        fromDate = parcel.readString()
        toDate = parcel.readString()
        filterByType = parcel.readString()
        actionDate = parcel.readString()
        userReference = parcel.readString()
        debitOrderStatus = parcel.readString()
        userCode = parcel.readString()
        userSequence = parcel.readString()
        tiebNumber = parcel.readString()
        instructionNumber = parcel.readString()
        stopPaymentType = parcel.readString()
        debitType = parcel.readString()
        amount = parcel.readString()
        reasonCode = parcel.readString()
        reasonDescription = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromAccountNumber)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeString(filterByType)
        parcel.writeString(actionDate)
        parcel.writeString(userReference)
        parcel.writeString(debitOrderStatus)
        parcel.writeString(userCode)
        parcel.writeString(userSequence)
        parcel.writeString(tiebNumber)
        parcel.writeString(instructionNumber)
        parcel.writeString(stopPaymentType)
        parcel.writeString(debitType)
        parcel.writeString(amount)
        parcel.writeString(reasonCode)
        parcel.writeString(reasonDescription)
    }

    companion object CREATOR : Parcelable.Creator<DebitOrderDataModel> {
        override fun createFromParcel(parcel: Parcel): DebitOrderDataModel {
            return DebitOrderDataModel(parcel)
        }

        override fun newArray(size: Int): Array<DebitOrderDataModel?> {
            return arrayOfNulls(size)
        }
    }

}