/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable

class BuyMoreUnitsCappedResponse() : KParcelable, TransactionResponse() {
    var status: Boolean = false

    constructor(parcel: Parcel) : this() {
        status = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (status) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<BuyMoreUnitsCappedResponse> {
        override fun createFromParcel(parcel: Parcel): BuyMoreUnitsCappedResponse {
            return BuyMoreUnitsCappedResponse(parcel)
        }

        override fun newArray(size: Int): Array<BuyMoreUnitsCappedResponse?> {
            return arrayOfNulls(size)
        }
    }
}
