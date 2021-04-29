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
package com.barclays.absa.banking.boundary.model.rewards

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class RewardsRedeemConfirmation() : ResponseObject(), KParcelable {

    @JsonProperty("txnRefID")
    var txnReferenceID: String? = null
    @JsonProperty("redemptionCode")
    var reedemptionCode: String? = null
    @JsonProperty("redemptionId")
    var redemptionId: Int? = null
    @JsonProperty("networkProviderId")
    var networkProviderId: Int? = null
    var networkName: String? = null
    var voucherName: String? = null
    @JsonProperty("faceValue")
    var airtimeValue: Amount? = null
        get() {
            if (field == null) {
                this.airtimeValue = Amount()
            }
            return field
        }
    @JsonProperty("discount")
    var discount: Amount? = null
        get() {
            if (field == null) {
                this.discount = Amount()
            }
            return field
        }

    @JsonProperty("accrualCost")
    var airtimeCostIncludingDiscount: Amount? = null
        get() {
            if (field == null) {
                this.airtimeCostIncludingDiscount = Amount()
            }
            return field
        }
    @JsonProperty("rewardsBalance")
    var rewardsBalance: Amount? = null
        get() {
            if (field == null) {
                this.rewardsBalance = Amount()
            }
            return field
        }
    @JsonProperty("charity")
    var charityName: String? = null
    @JsonProperty("retailVoucher")
    var voucherVendor: String? = null
    @JsonProperty("redemptionPartner")
    var partner: String? = null
    @JsonProperty("amt")
    var amount: Amount? = null
        get() {
            if (field == null) {
                this.amount = Amount()
            }
            return field
        }
    @JsonProperty("cardNo")
    var membershipNumber: String? = null
    @JsonProperty("actNo")
    var toAccount: String? = null
    @JsonProperty("cellNo")
    var cellphoneNumber: String? = null
    @JsonProperty("membershipNo")
    var fromAccountNumber: String? = null
    @JsonProperty("actDesc")
    var accountDescription: String? = null

    var toAccountDescription: String? = ""
    var fromAccountDescription: String? = ""

    constructor(parcel: Parcel) : this() {
        airtimeCostIncludingDiscount = parcel.readSerializable() as? Amount
        txnReferenceID = parcel.readString()
        reedemptionCode = parcel.readString()
        redemptionId = parcel.readValue(Int::class.java.classLoader) as? Int
        networkProviderId = parcel.readValue(Int::class.java.classLoader) as? Int
        networkName = parcel.readString()
        voucherName = parcel.readString()
        charityName = parcel.readString()
        voucherVendor = parcel.readString()
        partner = parcel.readString()
        membershipNumber = parcel.readString()
        toAccount = parcel.readString()
        cellphoneNumber = parcel.readString()
        fromAccountNumber = parcel.readString()
        accountDescription = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(airtimeCostIncludingDiscount)
        parcel.writeString(txnReferenceID)
        parcel.writeString(reedemptionCode)
        parcel.writeValue(redemptionId)
        parcel.writeValue(networkProviderId)
        parcel.writeString(networkName)
        parcel.writeString(voucherName)
        parcel.writeString(charityName)
        parcel.writeString(voucherVendor)
        parcel.writeString(partner)
        parcel.writeString(membershipNumber)
        parcel.writeString(toAccount)
        parcel.writeString(cellphoneNumber)
        parcel.writeString(fromAccountNumber)
        parcel.writeString(accountDescription)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<RewardsRedeemConfirmation> {
            override fun createFromParcel(parcel: Parcel): RewardsRedeemConfirmation {
                return RewardsRedeemConfirmation(parcel)
            }

            override fun newArray(size: Int): Array<RewardsRedeemConfirmation?> {
                return arrayOfNulls(size)
            }
        }
    }
}