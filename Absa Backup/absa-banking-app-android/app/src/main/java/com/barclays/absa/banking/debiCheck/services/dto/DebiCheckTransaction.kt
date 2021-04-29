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
package com.barclays.absa.banking.debiCheck.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class DebiCheckTransaction() : KParcelable {

    var paymentKey: String = ""
    var mandateReferenceNumber: String = ""
    var companyReference: String = ""
    var lastPaymentDate: String = ""
    var reason: String = ""
    var collectionAmount: String = ""
    var actionDate: String = ""
    var fromAccount: String = ""
    var userReference: String = ""
    var creditorName: String = ""
    var debtorAccountNumber: String = ""
    var disputable: Boolean = true
    var disputeReason: String = ""
    var status: String = ""

    constructor(parcel: Parcel) : this() {
        paymentKey = parcel.readString() ?: ""
        mandateReferenceNumber = parcel.readString() ?: ""
        companyReference = parcel.readString() ?: ""
        lastPaymentDate = parcel.readString() ?: ""
        reason = parcel.readString() ?: ""
        collectionAmount = parcel.readString() ?: ""
        actionDate = parcel.readString() ?: ""
        fromAccount = parcel.readString() ?: ""
        userReference = parcel.readString() ?: ""
        creditorName = parcel.readString() ?: ""
        debtorAccountNumber = parcel.readString() ?: ""
        disputable = parcel.readValue(Boolean::class.java.classLoader) as Boolean
        disputeReason = parcel.readString() ?: ""
        status = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(paymentKey)
        parcel.writeString(mandateReferenceNumber)
        parcel.writeString(companyReference)
        parcel.writeString(lastPaymentDate)
        parcel.writeString(reason)
        parcel.writeString(collectionAmount)
        parcel.writeString(actionDate)
        parcel.writeString(fromAccount)
        parcel.writeString(userReference)
        parcel.writeString(creditorName)
        parcel.writeString(debtorAccountNumber)
        parcel.writeValue(disputable)
        parcel.writeString(disputeReason)
        parcel.writeString(status)
    }

    companion object CREATOR : Parcelable.Creator<DebiCheckTransaction> {
        override fun createFromParcel(parcel: Parcel): DebiCheckTransaction {
            return DebiCheckTransaction(parcel)
        }

        override fun newArray(size: Int): Array<DebiCheckTransaction?> {
            return arrayOfNulls(size)
        }
    }
}