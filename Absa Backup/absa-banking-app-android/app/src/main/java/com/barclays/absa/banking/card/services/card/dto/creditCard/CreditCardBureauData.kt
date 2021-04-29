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
import com.barclays.absa.banking.framework.data.ResponseObject

class CreditCardBureauData() : ResponseObject(), KParcelable {
    var fetchBureauDataForVCLCLIApplyRespDTO: BureauDataApplicationResponse? = null

    constructor(parcel: Parcel) : this() {
        fetchBureauDataForVCLCLIApplyRespDTO = parcel.readParcelable(BureauDataApplicationResponse::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(fetchBureauDataForVCLCLIApplyRespDTO, flags)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CreditCardBureauData> {
            override fun createFromParcel(parcel: Parcel): CreditCardBureauData {
                return CreditCardBureauData(parcel)
            }

            override fun newArray(size: Int): Array<CreditCardBureauData?> {
                return arrayOfNulls(size)
            }
        }
    }
}