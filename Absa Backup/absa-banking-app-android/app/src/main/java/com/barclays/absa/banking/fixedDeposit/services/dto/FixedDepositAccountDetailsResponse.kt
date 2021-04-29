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
 */

package com.barclays.absa.banking.fixedDeposit.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class FixedDepositAccountDetailsResponse() : ResponseObject(), KParcelable {
    @JsonProperty("fixedDeposit")
    var fixedDeposit: FixedDeposit? = FixedDeposit()
    @JsonProperty("interestPaymentInstDTO")
    var interestPaymentInstruction: InterestPaymentInstruction? = InterestPaymentInstruction()

    constructor(parcel: Parcel) : this() {
        fixedDeposit = parcel.readParcelable(FixedDeposit::class.java.classLoader)
        interestPaymentInstruction = parcel.readParcelable(InterestPaymentInstruction::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(fixedDeposit, flags)
        parcel.writeParcelable(interestPaymentInstruction, flags)
    }

    companion object CREATOR : Parcelable.Creator<FixedDepositAccountDetailsResponse> {
        override fun createFromParcel(parcel: Parcel): FixedDepositAccountDetailsResponse {
            return FixedDepositAccountDetailsResponse(parcel)
        }

        override fun newArray(size: Int): Array<FixedDepositAccountDetailsResponse?> {
            return arrayOfNulls(size)
        }
    }
}
