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

package com.barclays.absa.banking.fixedDeposit.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class FixedDeposit() : BaseModel, KParcelable {
    @JsonProperty("accountNumber")
    var accountNumber: String = ""
    @JsonProperty("accountType")
    var accountType: String = ""
    @JsonProperty("description")
    var description: String = ""
    @JsonProperty("term")
    var term: String = ""
    @JsonProperty("dateOpened")
    var dateOpened: String = ""
    @JsonProperty("subAccountBalance")
    var subAccountBalance: String = ""
    @JsonProperty("currentInterestRate")
    var currentInterestRate: String = ""
    @JsonProperty("accruedInterest")
    var accruedInterest: String = ""
    @JsonProperty("bonusRate")
    var bonusRate: String = ""
    @JsonProperty("accruedBonus")
    var accruedBonus: String = ""
    @JsonProperty("maturityDate")
    var maturityDate: String = ""
    @JsonProperty("nextCapDate")
    var nextCapDate: String = ""
    @JsonProperty("capFrequency")
    var capFrequency: String = ""
    @JsonProperty("postDatedTrans")
    var postDatedTrans: String = ""
    @JsonProperty("amountCeded")
    var amountCeded: String = ""
    @JsonProperty("accountStatus")
    var accountStatus: String = ""
    @JsonProperty("productCode")
    var productCode: String? = ""
    @JsonProperty("capitalisationDay")
    var capitalisationDay: String? = ""

    constructor(parcel: Parcel) : this() {
        accountNumber = parcel.readString() ?: ""
        accountType = parcel.readString() ?: ""
        description = parcel.readString() ?: ""
        term = parcel.readString() ?: ""
        dateOpened = parcel.readString() ?: ""
        subAccountBalance = parcel.readString() ?: ""
        currentInterestRate = parcel.readString() ?: ""
        accruedInterest = parcel.readString() ?: ""
        bonusRate = parcel.readString() ?: ""
        accruedBonus = parcel.readString() ?: ""
        maturityDate = parcel.readString() ?: ""
        nextCapDate = parcel.readString() ?: ""
        capFrequency = parcel.readString() ?: ""
        postDatedTrans = parcel.readString() ?: ""
        amountCeded = parcel.readString() ?: ""
        accountStatus = parcel.readString() ?: ""
        productCode = parcel.readString() ?: ""
        capitalisationDay = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(accountNumber)
            writeString(accountType)
            writeString(description)
            writeString(term)
            writeString(dateOpened)
            writeString(subAccountBalance)
            writeString(currentInterestRate)
            writeString(accruedInterest)
            writeString(bonusRate)
            writeString(accruedBonus)
            writeString(maturityDate)
            writeString(nextCapDate)
            writeString(capFrequency)
            writeString(postDatedTrans)
            writeString(amountCeded)
            writeString(accountStatus)
            writeString(productCode)
            writeString(capitalisationDay)
        }
    }

    companion object CREATOR : Parcelable.Creator<FixedDeposit> {
        override fun createFromParcel(parcel: Parcel): FixedDeposit {
            return FixedDeposit(parcel)
        }

        override fun newArray(size: Int): Array<FixedDeposit?> {
            return arrayOfNulls(size)
        }
    }
}
