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

package com.barclays.absa.banking.directMarketing.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.framework.KParcelable

class MarketingIndicators() : KParcelable {
    var nonCreditAutoVoiceIndicator: String? = ""
    var nonCreditEmailIndicator: String? = ""
    var nonCreditIndicator: String? = ""
    var nonCreditPostIndicator: String? = ""
    var nonCreditSmsIndicator: String? = ""
    var nonCreditTeleIndicator: String? = ""
    var creditAutoVoiceIndicator: String? = ""
    var creditEmailIndicator: String? = ""
    var creditIndicator: String? = ""
    var creditPostIndicator: String? = ""
    var creditSmsIndicator: String? = ""
    var creditTeleIndicator: String? = ""

    constructor(parcel: Parcel) : this() {
        nonCreditAutoVoiceIndicator = parcel.readString()
        nonCreditEmailIndicator = parcel.readString()
        nonCreditIndicator = parcel.readString()
        nonCreditPostIndicator = parcel.readString()
        nonCreditSmsIndicator = parcel.readString()
        nonCreditTeleIndicator = parcel.readString()
        creditAutoVoiceIndicator = parcel.readString()
        creditEmailIndicator = parcel.readString()
        creditIndicator = parcel.readString()
        creditPostIndicator = parcel.readString()
        creditSmsIndicator = parcel.readString()
        creditTeleIndicator = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nonCreditAutoVoiceIndicator)
        parcel.writeString(nonCreditEmailIndicator)
        parcel.writeString(nonCreditIndicator)
        parcel.writeString(nonCreditPostIndicator)
        parcel.writeString(nonCreditSmsIndicator)
        parcel.writeString(nonCreditTeleIndicator)
        parcel.writeString(creditAutoVoiceIndicator)
        parcel.writeString(creditEmailIndicator)
        parcel.writeString(creditIndicator)
        parcel.writeString(creditPostIndicator)
        parcel.writeString(creditSmsIndicator)
        parcel.writeString(creditTeleIndicator)
    }

    companion object CREATOR : Parcelable.Creator<MarketingIndicators> {
        override fun createFromParcel(parcel: Parcel): MarketingIndicators {
            return MarketingIndicators(parcel)
        }

        override fun newArray(size: Int): Array<MarketingIndicators?> {
            return arrayOfNulls(size)
        }
    }
}