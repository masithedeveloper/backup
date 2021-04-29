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
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

open class DebitOrderDetailsResponse() : TransactionResponse(), KParcelable {
    var actionDate: String = ""
    @JsonProperty("userReferance")
    var userReference: String = ""
    @JsonProperty("status")
    var debitOrderStatus: String = ""
    var userCode: String = ""
    @JsonProperty("userSeq")
    var userSequence: String = ""
    var tiebNumber: String = ""
    @JsonProperty("instractionNo")
    var instructionNumber: String = ""
    var stopPaymentType: String = ""
    var debitType: String = ""
    @JsonProperty("uniqNumber")
    var uniqueNumber: String = ""
    var amount: String = ""

    constructor(parcel: Parcel) : this() {
        actionDate = parcel.readString() ?: ""
        userReference = parcel.readString() ?: ""
        debitOrderStatus = parcel.readString() ?: ""
        userCode = parcel.readString() ?: ""
        userSequence = parcel.readString() ?: ""
        tiebNumber = parcel.readString() ?: ""
        instructionNumber = parcel.readString() ?: ""
        stopPaymentType = parcel.readString() ?: ""
        debitType = parcel.readString() ?: ""
        uniqueNumber = parcel.readString() ?: ""
        amount = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(actionDate)
        parcel.writeString(userReference)
        parcel.writeString(debitOrderStatus)
        parcel.writeString(userCode)
        parcel.writeString(userSequence)
        parcel.writeString(tiebNumber)
        parcel.writeString(instructionNumber)
        parcel.writeString(stopPaymentType)
        parcel.writeString(debitType)
        parcel.writeString(uniqueNumber)
        parcel.writeString(amount)
    }

    companion object CREATOR : Parcelable.Creator<DebitOrderDetailsResponse> {
        override fun createFromParcel(parcel: Parcel): DebitOrderDetailsResponse {
            return DebitOrderDetailsResponse(parcel)
        }

        override fun newArray(size: Int): Array<DebitOrderDetailsResponse?> {
            return arrayOfNulls(size)
        }
    }
}