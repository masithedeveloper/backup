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

package com.barclays.absa.banking.unitTrusts.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable
import com.fasterxml.jackson.annotation.JsonProperty

class LinkedAccountsWrapper() : KParcelable, TransactionResponse() {

    @JsonProperty("utDebitAccountDTO")
    var accounts: List<LinkedAccount> = emptyList()

    constructor(parcel: Parcel) : this() {
        accounts = parcel.createTypedArrayList(LinkedAccount) as List<LinkedAccount>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(accounts)
    }

    companion object CREATOR : Parcelable.Creator<LinkedAccountsWrapper> {
        override fun createFromParcel(parcel: Parcel): LinkedAccountsWrapper {
            return LinkedAccountsWrapper(parcel)
        }

        override fun newArray(size: Int): Array<LinkedAccountsWrapper?> {
            return arrayOfNulls(size)
        }
    }
}
