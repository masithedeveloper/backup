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

package com.barclays.absa.banking.payments.international.data

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel

class InternationalPaymentBeneficiaryDetails(var lastPaidAmount: String? = "", var beneficiaryDisplayName: String? = "",
                                             private var lastPaidDate: String? = "", var beneficiaryId: String? = "",
                                             var transferType: String? = "", var beneficiaryIFTType: String? = "",
                                             var status: String? = "", var eftNumber: String? = "",
                                             var cifkey: String? = "", var tiebNumber: String? = "") : KParcelable, BaseModel {

    constructor(parcel: Parcel) : this() {
        lastPaidAmount = parcel.readString()
        beneficiaryDisplayName = parcel.readString()
        lastPaidDate = parcel.readString()
        beneficiaryId = parcel.readString()
        transferType = parcel.readString()
        beneficiaryIFTType = parcel.readString()
        status = parcel.readString()
        eftNumber = parcel.readString()
        cifkey = parcel.readString()
        tiebNumber = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lastPaidAmount)
        parcel.writeString(beneficiaryDisplayName)
        parcel.writeString(lastPaidDate)
        parcel.writeString(beneficiaryId)
        parcel.writeString(transferType)
        parcel.writeString(beneficiaryIFTType)
        parcel.writeString(status)
        parcel.writeString(eftNumber)
        parcel.writeString(cifkey)
        parcel.writeString(tiebNumber)
    }

    companion object CREATOR : Parcelable.Creator<InternationalPaymentBeneficiaryDetails> {
        override fun createFromParcel(parcel: Parcel): InternationalPaymentBeneficiaryDetails {
            return InternationalPaymentBeneficiaryDetails(parcel)
        }

        override fun newArray(size: Int): Array<InternationalPaymentBeneficiaryDetails?> {
            return arrayOfNulls(size)
        }
    }
}