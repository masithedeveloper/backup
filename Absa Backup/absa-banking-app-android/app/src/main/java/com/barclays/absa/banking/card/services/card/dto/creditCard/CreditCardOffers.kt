/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.services.card.dto.creditCard

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class CreditCardOffers() : KParcelable {
    var creditCardAccountnumbers: String? = ""
    var creditLimitIncreaseAmtForCLI: String? = ""
    var existingCreditLimitOfCreditCard: String? = ""
    var offerIndicator: String? = ""

    constructor(parcel: Parcel) : this() {
        creditCardAccountnumbers = parcel.readString()
        creditLimitIncreaseAmtForCLI = parcel.readString()
        existingCreditLimitOfCreditCard = parcel.readString()
        offerIndicator = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(creditCardAccountnumbers)
        parcel.writeString(creditLimitIncreaseAmtForCLI)
        parcel.writeString(existingCreditLimitOfCreditCard)
        parcel.writeString(offerIndicator)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardOffers> {
            override fun createFromParcel(parcel: Parcel): CreditCardOffers {
                return CreditCardOffers(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardOffers?> {
                return arrayOfNulls(size)
            }
        }
    }
}
