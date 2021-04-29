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

package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class BureauDataApplicationResponse() : KParcelable {
    var offersCreditCardNumber: String? = "0.0"
    var creditLimitIncreaseAmtForCLI: String? = "0.0"
    var creditCardApplyStatusEnum: String? = "0.0"
    var existingCreditLimitOfCreditCard: String? = "0.0"
    var incomeAndExpense: IncomeAndExpensesResponse? = null

    constructor(parcel: Parcel) : this() {
        offersCreditCardNumber = parcel.readString()
        creditLimitIncreaseAmtForCLI = parcel.readString()
        creditCardApplyStatusEnum = parcel.readString()
        existingCreditLimitOfCreditCard = parcel.readString()
        incomeAndExpense = parcel.readParcelable(IncomeAndExpensesResponse::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(offersCreditCardNumber)
        parcel.writeString(creditLimitIncreaseAmtForCLI)
        parcel.writeString(creditCardApplyStatusEnum)
        parcel.writeString(existingCreditLimitOfCreditCard)
        parcel.writeParcelable(incomeAndExpense, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BureauDataApplicationResponse> {
            override fun createFromParcel(parcel: Parcel): BureauDataApplicationResponse {
                return BureauDataApplicationResponse(parcel)
            }

            override fun newArray(size: Int): Array<BureauDataApplicationResponse?> {
                return arrayOfNulls(size)
            }
        }
    }
}