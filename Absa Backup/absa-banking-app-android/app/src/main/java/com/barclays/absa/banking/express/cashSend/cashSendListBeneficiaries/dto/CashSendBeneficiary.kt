/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.express.cashSend.cashSendBeneficiaryDetails.dto.CashSendBeneficiaryDetails
import com.barclays.absa.banking.express.shared.dto.Transaction
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class CashSendBeneficiary() : BaseModel, KParcelable {
    var beneficiaryNumber: Int = 0
    var beneficiaryName: String = ""
    var targetAccountNumber: String = ""
    var beneficiaryNumberNameAndAccount: String = ""

    var processedTransactions: List<Transaction> = arrayListOf()
    var futureDatedTransactionsList: List<Transaction> = arrayListOf()

    @JsonProperty("beneficiaryDetailsVO")
    var beneficiaryDetails: CashSendBeneficiaryDetails = CashSendBeneficiaryDetails()

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DASHED_PATTERN_YYYY_MM_DD)
    var transactionDate: Date = Date()

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            beneficiaryNumber = readInt()
            beneficiaryName = readString() ?: ""
            targetAccountNumber = readString() ?: ""
            beneficiaryNumberNameAndAccount = readString() ?: ""
            beneficiaryDetails = readParcelable(CashSendBeneficiaryDetails::class.java.classLoader) ?: CashSendBeneficiaryDetails()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeInt(beneficiaryNumber)
            writeString(beneficiaryName)
            writeString(targetAccountNumber)
            writeString(beneficiaryNumberNameAndAccount)
            writeParcelable(beneficiaryDetails, flags)
        }
    }

    companion object CREATOR : Parcelable.Creator<CashSendBeneficiary> {
        override fun createFromParcel(parcel: Parcel): CashSendBeneficiary {
            return CashSendBeneficiary(parcel)
        }

        override fun newArray(size: Int): Array<CashSendBeneficiary?> {
            return arrayOfNulls(size)
        }
    }
}