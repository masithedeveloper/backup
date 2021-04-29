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

package com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class Voucher() : KParcelable {

    var title: String = ""
    var message: String = ""
    var imageData: String = ""
    var isSelected: Boolean = false
    var voucherDetails: VoucherDetails = VoucherDetails()

    constructor(parcel: Parcel) : this() {
        title = parcel.readString() ?: ""
        message = parcel.readString() ?: ""
        imageData = parcel.readString() ?: ""
        isSelected = parcel.readInt() != 0
        voucherDetails = parcel.readParcelable(VoucherDetails::class.java.classLoader) ?: VoucherDetails()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(imageData)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeParcelable(voucherDetails, flags)
    }

    companion object CREATOR : Parcelable.Creator<Voucher> {
        override fun createFromParcel(parcel: Parcel): Voucher {
            return Voucher(parcel)
        }

        override fun newArray(size: Int): Array<Voucher?> {
            return arrayOfNulls(size)
        }
    }
}

class VoucherDetails() : KParcelable {
    var offerId: String = ""
    var offerTier: String = ""
    var partnerId: String = ""
    var challengeId: String = ""

    constructor(parcel: Parcel) : this() {
        offerId = parcel.readString() ?: ""
        offerTier = parcel.readString() ?: ""
        partnerId = parcel.readString() ?: ""
        challengeId = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(offerId)
        parcel.writeString(offerTier)
        parcel.writeString(partnerId)
        parcel.writeString(challengeId)
    }

    companion object CREATOR : Parcelable.Creator<VoucherDetails> {
        override fun createFromParcel(parcel: Parcel): VoucherDetails {
            return VoucherDetails(parcel)
        }

        override fun newArray(size: Int): Array<VoucherDetails?> {
            return arrayOfNulls(size)
        }
    }
}
