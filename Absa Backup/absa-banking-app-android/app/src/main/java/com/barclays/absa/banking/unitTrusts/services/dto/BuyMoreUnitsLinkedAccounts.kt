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
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class BuyMoreUnitsLinkedAccounts() : KParcelable {

    @JsonProperty("accountDetailsDTOs")
    var accounts: List<BuyMoreUnitsLinkedAccount>? = null

    constructor(parcel: Parcel) : this() {
        accounts = parcel.createTypedArrayList(BuyMoreUnitsLinkedAccount)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(accounts)
    }

    companion object CREATOR : Parcelable.Creator<BuyMoreUnitsLinkedAccounts> {
        override fun createFromParcel(parcel: Parcel): BuyMoreUnitsLinkedAccounts {
            return BuyMoreUnitsLinkedAccounts(parcel)
        }

        override fun newArray(size: Int): Array<BuyMoreUnitsLinkedAccounts?> {
            return arrayOfNulls(size)
        }
    }
}