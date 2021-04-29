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

class VCLDataModel() : KParcelable {
    var creditCardOverdraft: CreditCardOverdraft? = null
    var creditCardBureauData: CreditCardBureauData? = null

    constructor(parcel: Parcel) : this() {
        creditCardOverdraft = parcel.readParcelable(CreditCardOverdraft::class.java.classLoader)
        creditCardBureauData = parcel.readParcelable(CreditCardBureauData::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(creditCardOverdraft, flags)
        parcel.writeParcelable(creditCardBureauData, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<VCLDataModel> {
            override fun createFromParcel(parcel: Parcel): VCLDataModel {
                return VCLDataModel(parcel)
            }

            override fun newArray(size: Int): Array<VCLDataModel?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun buildVCLParcelableModel(): VCLParcelableModel {
        val vclModel = VCLParcelableModel()
        vclModel.incomeAndExpense = creditCardBureauData?.fetchBureauDataForVCLCLIApplyRespDTO?.incomeAndExpense
        vclModel.currentCreditLimit = creditCardOverdraft?.displayCreditCardGadget?.existingCreditCardLimit
        vclModel.newCreditLimitAmount = creditCardOverdraft?.displayCreditCardGadget?.creditLimitIncreaseAmount
        vclModel.creditCardNumber = creditCardBureauData?.fetchBureauDataForVCLCLIApplyRespDTO?.offersCreditCardNumber
        vclModel.populateInstalmentAmounts()
        return vclModel
    }
}
