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

package com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendType
import com.barclays.absa.banking.express.shared.dto.BeneficiaryDetails
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class CashSendPaymentTransaction() : BeneficiaryDetails(), KParcelable {
    var cashSendType: CashSendType = CashSendType.Single
    var beneficiaryName: String = ""
    var beneficiaryShortName: String = ""
    var beneficiarySurname: String = ""
    var statementReference: String = ""
    var uniqueEFT: String = ""
    var paymentNumber = 0
    var sourceAccount: String = ""
    var paymentAmount: String = ""
    var accessCode: String = ""
    var cashSendKpnSessionKey: String = ""
    var cashSendKpnServerId: String = ""
    var paymentNarrative: String = ""
    var chosenSymmetricKey: String = ""
    var chosenInitializationVector: String = ""
    var chosenCredential: String = ""
    var transactionResult = true
    var paymentProcessed = false
    var returnCode: String = ""

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DASHED_PATTERN_YYYY_MM_DD)
    var transactionDateTime: Date = Date()

    @JsonProperty("recipientCellNo")
    var recipientCellphoneNumber: String = ""

    @JsonProperty("proxyCellNumber")
    var proxyCellphoneNumber: String = ""

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            beneficiaryName = readString().toString()
            beneficiaryShortName = readString().toString()
            beneficiarySurname = readString().toString()
            statementReference = readString().toString()
            uniqueEFT = readString().toString()
            paymentNumber = readInt()
            sourceAccount = readString().toString()
            paymentAmount = readString().toString()
            accessCode = readString().toString()
            cashSendKpnSessionKey = readString().toString()
            cashSendKpnServerId = readString().toString()
            paymentNarrative = readString().toString()
            chosenSymmetricKey = readString().toString()
            chosenInitializationVector = readString().toString()
            chosenCredential = readString().toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                transactionResult = readBoolean()
                paymentProcessed = readBoolean()
            } else {
                transactionResult = readInt() != 0
                paymentProcessed = readInt() != 0
            }
            returnCode = readString().toString()
            recipientCellphoneNumber = readString().toString()
            proxyCellphoneNumber = readString().toString()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(beneficiaryName)
            writeString(beneficiaryShortName)
            writeString(beneficiarySurname)
            writeString(statementReference)
            writeString(uniqueEFT)
            writeInt(paymentNumber)
            writeString(sourceAccount)
            writeString(paymentAmount)
            writeString(accessCode)
            writeString(cashSendKpnSessionKey)
            writeString(cashSendKpnServerId)
            writeString(paymentNarrative)
            writeString(chosenSymmetricKey)
            writeString(chosenInitializationVector)
            writeString(chosenCredential)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                writeBoolean(transactionResult)
                writeBoolean(paymentProcessed)
            } else {
                writeInt(if (transactionResult) 1 else 0)
                writeInt(if (paymentProcessed) 1 else 0)
            }
            writeString(returnCode)
            writeString(recipientCellphoneNumber)
            writeString(proxyCellphoneNumber)
        }
    }

    companion object CREATOR : Parcelable.Creator<CashSendPaymentTransaction> {
        override fun createFromParcel(parcel: Parcel): CashSendPaymentTransaction {
            return CashSendPaymentTransaction(parcel)
        }

        override fun newArray(size: Int): Array<CashSendPaymentTransaction?> {
            return arrayOfNulls(size)
        }
    }
}