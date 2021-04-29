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
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class CheckCashSendPlusRegistrationStatusResponse() : TransactionResponse(), KParcelable {

    @JsonProperty("clntAgrmntAccepted")
    var clientAgreementAccepted: String = ""

    @JsonProperty("cspViewFees")
    var cashSendPlusViewFeesPdf: String = ""

    @JsonProperty("cspTermsOfUse")
    var cashSendPlusTermsOfUse: String = ""

    @JsonProperty("cspBca")
    var cashSendPlusBusinessClientAgreement: String = ""

    @JsonProperty("checkForCashSendPlusRegistrationRespDTO")
    var cashSendPlusResponseData = CashSendPlusResponseData()

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CheckCashSendPlusRegistrationStatusResponse> {
            override fun createFromParcel(parcel: Parcel): CheckCashSendPlusRegistrationStatusResponse {
                return CheckCashSendPlusRegistrationStatusResponse(parcel)
            }

            override fun newArray(size: Int): Array<CheckCashSendPlusRegistrationStatusResponse?> {
                return arrayOfNulls(size)
            }
        }
    }

    private constructor(parcel: Parcel) : this() {
        with(parcel) {
            clientAgreementAccepted = readString() ?: ""
            cashSendPlusViewFeesPdf = readString() ?: ""
            cashSendPlusTermsOfUse = readString() ?: ""
            cashSendPlusBusinessClientAgreement = readString() ?: ""
            cashSendPlusResponseData = readParcelable(CashSendPlusResponseData::class.java.classLoader) ?: CashSendPlusResponseData()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(clientAgreementAccepted)
            writeString(cashSendPlusViewFeesPdf)
            writeString(cashSendPlusTermsOfUse)
            writeString(cashSendPlusBusinessClientAgreement)
            writeParcelable(cashSendPlusResponseData, flags)
        }
    }
}