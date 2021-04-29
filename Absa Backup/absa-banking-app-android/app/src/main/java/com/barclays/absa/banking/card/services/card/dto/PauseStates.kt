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
package com.barclays.absa.banking.card.services.card.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class PauseStates() : KParcelable, ResponseObject() {
    var allTransactions: String? = ""

    var internationalAtmTransactions: String? = ""

    @JsonProperty("internationalPosTransactions")
    var internationalPointOfSaleTransactions: String? = ""

    var localAtmTransactions: String? = ""

    @JsonProperty("localPosTransactions")
    var localPointOfSaleTransactions: String? = ""

    var onlinePurchases: String? = ""

    var cardNumber: String? = ""

    var digitalWallet: String? = ""

    constructor(parcel: Parcel) : this() {
        allTransactions = parcel.readString()
        internationalAtmTransactions = parcel.readString()
        internationalPointOfSaleTransactions = parcel.readString()
        localAtmTransactions = parcel.readString()
        localPointOfSaleTransactions = parcel.readString()
        onlinePurchases = parcel.readString()
        cardNumber = parcel.readString()
        digitalWallet = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(allTransactions)
        parcel.writeString(internationalAtmTransactions)
        parcel.writeString(internationalPointOfSaleTransactions)
        parcel.writeString(localAtmTransactions)
        parcel.writeString(localPointOfSaleTransactions)
        parcel.writeString(onlinePurchases)
        parcel.writeString(cardNumber)
        parcel.writeString(digitalWallet)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<PauseStates> {
            override fun createFromParcel(parcel: Parcel): PauseStates {
                return PauseStates(parcel)
            }

            override fun newArray(size: Int): Array<PauseStates?> {
                return arrayOfNulls(size)
            }
        }
    }
}
