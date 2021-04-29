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

package com.barclays.absa.banking.express.cashSend.cashSendBeneficiaryDetails.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendType
import com.barclays.absa.banking.express.shared.dto.BeneficiaryDetails
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class CashSendBeneficiaryDetails() : BeneficiaryDetails(), KParcelable {
    var cashSendType: CashSendType = CashSendType.Single
    var beneficiaryName: String = ""
    var beneficiaryShortName: String = ""
    var beneficiarySurname: String = ""
    var statementReference: String = ""
    var beneficiaryNarrative: String = ""
    var lastPaymentDateAndTime: String = ""
    var lastPaymentAmount: String = ""
    var lastPaymentStatus: String = ""
    var beneficiaryNumberNameAndAccount: String = ""

    @JsonProperty("recipientCellNo")
    var recipientCellphoneNumber: String = ""

    @JsonProperty("lastMaintDateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DASHED_PATTERN_YYYY_MM_DD)
    var lastMaintenanceDateTime: Date = Date()

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            beneficiaryName = readString() ?: ""
            beneficiaryShortName = readString() ?: ""
            beneficiarySurname = readString() ?: ""
            statementReference = readString() ?: ""
            beneficiaryNarrative = readString() ?: ""
            lastPaymentDateAndTime = readString() ?: ""
            lastPaymentAmount = readString() ?: ""
            lastPaymentStatus = readString() ?: ""
            beneficiaryNumberNameAndAccount = readString() ?: ""
            recipientCellphoneNumber = readString() ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(beneficiaryName)
            writeString(beneficiaryShortName)
            writeString(beneficiarySurname)
            writeString(statementReference)
            writeString(beneficiaryNarrative)
            writeString(lastPaymentDateAndTime)
            writeString(lastPaymentAmount)
            writeString(lastPaymentStatus)
            writeString(beneficiaryNumberNameAndAccount)
            writeString(recipientCellphoneNumber)
        }
    }

    companion object CREATOR : Parcelable.Creator<CashSendBeneficiaryDetails> {
        override fun createFromParcel(parcel: Parcel): CashSendBeneficiaryDetails {
            return CashSendBeneficiaryDetails(parcel)
        }

        override fun newArray(size: Int): Array<CashSendBeneficiaryDetails?> {
            return arrayOfNulls(size)
        }
    }
}