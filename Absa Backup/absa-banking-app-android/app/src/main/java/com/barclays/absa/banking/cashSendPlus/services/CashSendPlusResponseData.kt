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

package com.barclays.absa.banking.cashSendPlus.services

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class CashSendPlusResponseData() : BaseModel, KParcelable {
    var transactionResult: String = ""
    var returnCode: String = ""
    var returnDescription: String = ""
    var cashSendPlusStatus: String = ""

    @JsonProperty("cashSendPlusLimitAmt")
    var cashSendPlusLimitAmount: String = ""

    @JsonProperty("cashSendPlusLimitAmtPrev")
    var cashSendPlusLimitPrevAmount: String = ""

    @JsonProperty("cashSendPlusLimitAmtUsed")
    var cashSendPlusLimitAmountUsed: String = ""

    @JsonProperty("cashSendPlusLimitAmtAvail")
    var cashSendPlusLimitAmountAvailable: String = ""

    var registerSuccess: String = ""

    var cashSendPlusOutstanding: String = ""

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CashSendPlusResponseData> {
            override fun createFromParcel(parcel: Parcel): CashSendPlusResponseData {
                return CashSendPlusResponseData(parcel)
            }

            override fun newArray(size: Int): Array<CashSendPlusResponseData?> {
                return arrayOfNulls(size)
            }
        }
    }

    private constructor(parcel: Parcel) : this() {
        with(parcel) {
            transactionResult = readString() ?: ""
            returnCode = readString() ?: ""
            returnDescription = readString() ?: ""
            cashSendPlusStatus = readString() ?: ""
            cashSendPlusLimitAmount = readString() ?: ""
            cashSendPlusLimitPrevAmount = readString() ?: ""
            cashSendPlusLimitAmountUsed = readString() ?: ""
            cashSendPlusLimitAmountAvailable = readString() ?: ""
            registerSuccess = readString() ?: ""
            cashSendPlusOutstanding = readString() ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(transactionResult)
            writeString(returnCode)
            writeString(returnDescription)
            writeString(cashSendPlusStatus)
            writeString(cashSendPlusLimitAmount)
            writeString(cashSendPlusLimitPrevAmount)
            writeString(cashSendPlusLimitAmountUsed)
            writeString(cashSendPlusLimitAmountAvailable)
            writeString(registerSuccess)
            writeString(cashSendPlusOutstanding)
        }
    }
}