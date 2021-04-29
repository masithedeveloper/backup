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
import com.fasterxml.jackson.annotation.JsonProperty

class CreditCardGadget() : KParcelable {
    var offersCreditCardEnum: String? = "0.00"

    @JsonProperty("offersCreditExistingValue")
    var existingCreditCardLimit: String? = "0.00"

    @JsonProperty("offersCreditNewValue")
    var newCreditLimitForAcquisitions: String? = "0.00"

    @JsonProperty("offersCreditUnUtilizedValue")
    var unutilizedCreditLimit: String? = "0.00"

    @JsonProperty("offersCreditUtilizedValue")
    var utilizedCreditLimit: String? = "0.00"

    @JsonProperty("offersCreditCardNumber")
    var creditCardNumber: String? = "0.00"

    @JsonProperty("creditLimitIncreaseAmtForCLI")
    var creditLimitIncreaseAmount: String? = "0.00"

    @JsonProperty("isFreshBureauDataAvailableWithCams")
    var isBureauDataAvailableFromCams: String? = "false"

    @JsonProperty("incomeAndExpenseDTO")
    var incomeAndExpenses: IncomeAndExpensesResponse? = IncomeAndExpensesResponse()

    private constructor(parcel: Parcel) : this() {
        offersCreditCardEnum = parcel.readString()
        existingCreditCardLimit = parcel.readString()
        newCreditLimitForAcquisitions = parcel.readString()
        unutilizedCreditLimit = parcel.readString()
        utilizedCreditLimit = parcel.readString()
        creditCardNumber = parcel.readString()
        creditLimitIncreaseAmount = parcel.readString()
        isBureauDataAvailableFromCams = parcel.readString()
        incomeAndExpenses = parcel.readParcelable(IncomeAndExpensesResponse::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(offersCreditCardEnum)
        parcel.writeString(existingCreditCardLimit)
        parcel.writeString(newCreditLimitForAcquisitions)
        parcel.writeString(unutilizedCreditLimit)
        parcel.writeString(utilizedCreditLimit)
        parcel.writeString(creditCardNumber)
        parcel.writeString(creditLimitIncreaseAmount)
        parcel.writeString(isBureauDataAvailableFromCams)
        parcel.writeParcelable(incomeAndExpenses, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardGadget> {
            override fun createFromParcel(parcel: Parcel): CreditCardGadget {
                return CreditCardGadget(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardGadget?> {
                return arrayOfNulls(size)
            }
        }
    }
}

