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
import com.barclays.absa.banking.framework.data.ResponseObject

class CreditCardOverdraft() : ResponseObject(), KParcelable {

    var displayCreditCardGadget: CreditCardGadget? = null

    constructor(parcel: Parcel) : this() {
        displayCreditCardGadget = parcel.readParcelable(CreditCardGadget::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(displayCreditCardGadget, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardOverdraft> {
            override fun createFromParcel(parcel: Parcel): CreditCardOverdraft {
                return CreditCardOverdraft(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardOverdraft?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun buildVCLModel(): VCLParcelableModel {
        val vclParcelableModel = VCLParcelableModel()
        vclParcelableModel.populateInstalmentAmounts()
        vclParcelableModel.creditCardVCLGadget = displayCreditCardGadget
        vclParcelableModel.isFreshBureauDataAvailable = displayCreditCardGadget?.isBureauDataAvailableFromCams
        vclParcelableModel.creditCardNumber = displayCreditCardGadget?.creditCardNumber
        vclParcelableModel.currentCreditLimit = displayCreditCardGadget?.existingCreditCardLimit
        vclParcelableModel.newCreditLimitAmount = displayCreditCardGadget?.creditLimitIncreaseAmount
        vclParcelableModel.incomeAndExpense = displayCreditCardGadget?.incomeAndExpenses
        return vclParcelableModel
    }
}
