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
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class CreditLimitApplicationResult() : SureCheckResponse(), KParcelable {

    @JsonProperty("respDTO")
    var increaseResponse: VLCIncreaseResponse? = VLCIncreaseResponse()

    constructor(parcel: Parcel) : this() {
        increaseResponse = parcel.readParcelable(VLCIncreaseResponse::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(increaseResponse, flags)
    }

    class VLCIncreaseResponse() : KParcelable {
        @JsonProperty("cliapplicationResult")
        var creditLimitIncreaseResult: String? = null

        constructor(parcel: Parcel) : this() {
            creditLimitIncreaseResult = parcel.readString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(creditLimitIncreaseResult)
        }

        companion object CREATOR : Parcelable.Creator<VLCIncreaseResponse> {
            override fun createFromParcel(parcel: Parcel): VLCIncreaseResponse {
                return VLCIncreaseResponse(parcel)
            }

            override fun newArray(size: Int): Array<VLCIncreaseResponse?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<CreditLimitApplicationResult> {
        override fun createFromParcel(parcel: Parcel): CreditLimitApplicationResult {
            return CreditLimitApplicationResult(parcel)
        }

        override fun newArray(size: Int): Array<CreditLimitApplicationResult?> {
            return arrayOfNulls(size)
        }
    }
}
