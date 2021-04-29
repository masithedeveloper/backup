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

class PaymentCalculations() : KParcelable, BaseModel {
    var currencyCode: String? = ""
    var amountEntered: String? = ""
    var gender: String? = ""
    var country: String? = ""
    var availableBalance: String? = ""
    var accountNumber: String? = ""
    var sendCurrency: String? = ""

    constructor(parcel: Parcel) : this() {
        currencyCode = parcel.readString()
        amountEntered = parcel.readString()
        gender = parcel.readString()
        country = parcel.readString()
        availableBalance = parcel.readString()
        accountNumber = parcel.readString()
        sendCurrency = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(currencyCode)
        parcel.writeString(amountEntered)
        parcel.writeString(gender)
        parcel.writeString(country)
        parcel.writeString(availableBalance)
        parcel.writeString(accountNumber)
        parcel.writeString(sendCurrency)
    }

    companion object CREATOR : Parcelable.Creator<PaymentCalculations> {
        override fun createFromParcel(parcel: Parcel): PaymentCalculations {
            return PaymentCalculations(parcel)
        }

        override fun newArray(size: Int): Array<PaymentCalculations?> {
            return arrayOfNulls(size)
        }
    }
}