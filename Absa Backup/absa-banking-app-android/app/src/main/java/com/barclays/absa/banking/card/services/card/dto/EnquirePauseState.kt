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

class EnquirePauseState() : KParcelable, ResponseObject() {

    @JsonProperty("enquirePauseCardRespDTO")
    var pauseStates: PauseStates? = null

    constructor(parcel: Parcel) : this() {
        pauseStates = parcel.readParcelable(PauseStates::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(pauseStates, flags)

    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<EnquirePauseState> {
            override fun createFromParcel(parcel: Parcel): EnquirePauseState {
                return EnquirePauseState(parcel)
            }

            override fun newArray(size: Int): Array<EnquirePauseState?> {
                return arrayOfNulls(size)
            }
        }
    }

}
