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

package com.barclays.absa.banking.express.behaviouralRewards.shared

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel

class Challenge() : BaseModel, KParcelable {
    var longDescription: String = ""
    var shortDescription: String = ""
    var challengeId: String = ""
    var challengeStartDate: String = ""
    var challengeEndDate: String = ""
    var customerChallengeStatus = CustomerChallengeStatus()
    var active = false

    constructor(parcel: Parcel) : this() {
        longDescription = parcel.readString().toString()
        shortDescription = parcel.readString().toString()
        challengeId = parcel.readString().toString()
        challengeStartDate = parcel.readString().toString()
        challengeEndDate = parcel.readString().toString()
        customerChallengeStatus = parcel.readParcelable(CustomerChallengeStatus::class.java.classLoader) ?: CustomerChallengeStatus()
        active = parcel.readInt() != 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(longDescription)
        parcel.writeString(shortDescription)
        parcel.writeString(challengeId)
        parcel.writeString(challengeStartDate)
        parcel.writeString(challengeEndDate)
        parcel.writeParcelable(customerChallengeStatus, flags)
        parcel.writeByte(if (active) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Challenge> {
        override fun createFromParcel(parcel: Parcel): Challenge {
            return Challenge(parcel)
        }

        override fun newArray(size: Int): Array<Challenge?> {
            return arrayOfNulls(size)
        }
    }
}

class CustomerChallengeStatus() : BaseModel, KParcelable {
    var status: String = ""
    var progress: String = ""
    var customerChallengeEndDate: String = ""
    var customerChallengeStartDate: String = ""
    var acceptedOnDate: String = ""
    var voucherAllocationStatus: String = ""
    var voucherForChallenge = VoucherForChallenge()

    constructor(parcel: Parcel) : this() {
        status = parcel.readString().toString()
        progress = parcel.readString().toString()
        customerChallengeEndDate = parcel.readString().toString()
        customerChallengeStartDate = parcel.readString().toString()
        acceptedOnDate = parcel.readString().toString()
        voucherAllocationStatus = parcel.readString().toString()
        voucherForChallenge = parcel.readParcelable(VoucherForChallenge::class.java.classLoader) ?: VoucherForChallenge()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(progress)
        parcel.writeString(customerChallengeEndDate)
        parcel.writeString(customerChallengeStartDate)
        parcel.writeString(acceptedOnDate)
        parcel.writeString(voucherAllocationStatus)
        parcel.writeParcelable(voucherForChallenge, flags)
    }

    companion object CREATOR : Parcelable.Creator<CustomerChallengeStatus> {
        override fun createFromParcel(parcel: Parcel): CustomerChallengeStatus {
            return CustomerChallengeStatus(parcel)
        }

        override fun newArray(size: Int): Array<CustomerChallengeStatus?> {
            return arrayOfNulls(size)
        }
    }
}

class VoucherForChallenge() : BaseModel, KParcelable {
    var voucherPin = ""
    var voucherPartnerID = ""
    var voucherIssueDate = ""
    var voucherType = ""
    var voucherValue = ""
    var voucherExpiryDate = ""
    var voucherStatus = false

    constructor(parcel: Parcel) : this() {
        voucherPin = parcel.readString().toString()
        voucherPartnerID = parcel.readString().toString()
        voucherIssueDate = parcel.readString().toString()
        voucherType = parcel.readString().toString()
        voucherValue = parcel.readString().toString()
        voucherExpiryDate = parcel.readString().toString()
        voucherStatus = parcel.readInt() != 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(voucherPin)
        parcel.writeString(voucherPartnerID)
        parcel.writeString(voucherIssueDate)
        parcel.writeString(voucherType)
        parcel.writeString(voucherValue)
        parcel.writeString(voucherExpiryDate)
        parcel.writeByte(if (voucherStatus) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<VoucherForChallenge> {
        override fun createFromParcel(parcel: Parcel): VoucherForChallenge {
            return VoucherForChallenge(parcel)
        }

        override fun newArray(size: Int): Array<VoucherForChallenge?> {
            return arrayOfNulls(size)
        }
    }
}