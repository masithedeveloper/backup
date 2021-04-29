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
 */
package com.barclays.absa.banking.express.behaviouralRewards.customerVouchers.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import za.co.absa.networking.dto.BaseResponse

class CustomerVouchersResponse : BaseResponse() {
    var customerVoucherHistory: ArrayList<CustomerVoucherHistory> = arrayListOf()
}

class CustomerVoucherHistory : BaseModel {
    var partnerId: String = ""
    var vouchers: ArrayList<CustomerVoucher> = arrayListOf()
}

class CustomerVoucher() : KParcelable {
    var redemptionDateTime: String = ""
    var channel: String = ""
    var rewardPinVoucher: String = ""
    var offerTier: String = ""
    var offerDescription: String = ""
    var offerExpiryDays: String = ""
    var offerExpiryDateTime: String = ""
    var offerStatus: String = ""

    constructor(parcel: Parcel) : this() {
        redemptionDateTime = parcel.readString() ?: ""
        channel = parcel.readString() ?: ""
        rewardPinVoucher = parcel.readString() ?: ""
        offerTier = parcel.readString() ?: ""
        offerDescription = parcel.readString() ?: ""
        offerExpiryDays = parcel.readString() ?: ""
        offerExpiryDateTime = parcel.readString() ?: ""
        offerStatus = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(redemptionDateTime)
        parcel.writeString(channel)
        parcel.writeString(rewardPinVoucher)
        parcel.writeString(offerTier)
        parcel.writeString(offerDescription)
        parcel.writeString(offerExpiryDays)
        parcel.writeString(offerExpiryDateTime)
        parcel.writeString(offerStatus)
    }

    companion object CREATOR : Parcelable.Creator<CustomerVoucher> {
        override fun createFromParcel(parcel: Parcel): CustomerVoucher {
            return CustomerVoucher(parcel)
        }

        override fun newArray(size: Int): Array<CustomerVoucher?> {
            return arrayOfNulls(size)
        }
    }
}
