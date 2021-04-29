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

package com.barclays.absa.banking.express.behaviouralRewards.shareVoucher.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class BehaviouralRewardsShareVoucher() : KParcelable {
    var offerDescription: String = ""
    var voucherPin: String = ""
    var partnerId: String = ""
    var cellNumber: String = ""
    var shareMethod: String = ""

    constructor(parcel: Parcel) : this() {
        offerDescription = parcel.readString() ?: ""
        voucherPin = parcel.readString() ?: ""
        partnerId = parcel.readString() ?: ""
        cellNumber = parcel.readString() ?: ""
        shareMethod = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(offerDescription)
        parcel.writeString(voucherPin)
        parcel.writeString(partnerId)
        parcel.writeString(cellNumber)
        parcel.writeString(shareMethod)
    }

    companion object CREATOR : Parcelable.Creator<BehaviouralRewardsShareVoucher> {
        override fun createFromParcel(parcel: Parcel): BehaviouralRewardsShareVoucher {
            return BehaviouralRewardsShareVoucher(parcel)
        }

        override fun newArray(size: Int): Array<BehaviouralRewardsShareVoucher?> {
            return arrayOfNulls(size)
        }
    }
}