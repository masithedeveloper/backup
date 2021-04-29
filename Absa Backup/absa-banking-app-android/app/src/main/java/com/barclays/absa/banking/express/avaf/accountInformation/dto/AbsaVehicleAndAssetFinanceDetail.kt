/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.avaf.accountInformation.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class AbsaVehicleAndAssetFinanceDetail() : KParcelable {
    var accountNumber: String = ""
    var avafAccountDetailsSet: Boolean = false
    var contractEndDate: String = ""
    var contractStartDate: String = ""
    var estimatedSettlementValue: String = ""
    var installmentAmount: String = ""
    var interestRate: String = ""
    var interestRateType: String = ""
    var makeAndModel: String = ""
    var nextInstallmentDate: String = ""
    var originalFinanceAmount: String = ""
    var originalTerm: String = ""
    var outstandingBalance: String = ""
    var paymentFrequency: String = ""
    var paymentMethod: String = ""
    var remainingTerm: String = ""
    var residualValue: String = ""

    constructor(parcel: Parcel) : this() {
        accountNumber = parcel.readString() ?: ""
        avafAccountDetailsSet = parcel.readValue(Boolean::class.java.classLoader) as? Boolean ?: false
        contractEndDate = parcel.readString() ?: ""
        contractStartDate = parcel.readString() ?: ""
        estimatedSettlementValue = parcel.readString() ?: ""
        installmentAmount = parcel.readString() ?: ""
        interestRate = parcel.readString() ?: ""
        interestRateType = parcel.readString() ?: ""
        makeAndModel = parcel.readString() ?: ""
        nextInstallmentDate = parcel.readString() ?: ""
        originalFinanceAmount = parcel.readString() ?: ""
        originalTerm = parcel.readString() ?: ""
        outstandingBalance = parcel.readString() ?: ""
        paymentFrequency = parcel.readString() ?: ""
        paymentMethod = parcel.readString() ?: ""
        remainingTerm = parcel.readString() ?: ""
        residualValue = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(accountNumber)
            writeValue(avafAccountDetailsSet)
            writeString(contractEndDate)
            writeString(contractStartDate)
            writeString(estimatedSettlementValue)
            writeString(installmentAmount)
            writeString(interestRate)
            writeString(interestRateType)
            writeString(makeAndModel)
            writeString(nextInstallmentDate)
            writeString(originalFinanceAmount)
            writeString(originalTerm)
            writeString(outstandingBalance)
            writeString(paymentFrequency)
            writeString(paymentMethod)
            writeString(remainingTerm)
            writeString(residualValue)
        }
    }

    companion object CREATOR : Parcelable.Creator<AbsaVehicleAndAssetFinanceDetail> {
        override fun createFromParcel(parcel: Parcel): AbsaVehicleAndAssetFinanceDetail {
            return AbsaVehicleAndAssetFinanceDetail(parcel)
        }

        override fun newArray(size: Int): Array<AbsaVehicleAndAssetFinanceDetail?> {
            return arrayOfNulls(size)
        }
    }
}