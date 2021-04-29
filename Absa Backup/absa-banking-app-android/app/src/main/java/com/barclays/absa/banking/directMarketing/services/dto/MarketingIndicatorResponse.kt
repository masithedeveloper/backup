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
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.KParcelable

class MarketingIndicatorResponse() : TransactionResponse(), KParcelable {

    var marketingIndicator: MarketingIndicators? = MarketingIndicators()

    constructor(parcel: Parcel) : this() {
        marketingIndicator = parcel.readParcelable(MarketingIndicators::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(marketingIndicator, flags)
    }

    companion object CREATOR : Parcelable.Creator<MarketingIndicatorResponse> {
        override fun createFromParcel(parcel: Parcel): MarketingIndicatorResponse {
            return MarketingIndicatorResponse(parcel)
        }

        override fun newArray(size: Int): Array<MarketingIndicatorResponse?> {
            return arrayOfNulls(size)
        }
    }
}