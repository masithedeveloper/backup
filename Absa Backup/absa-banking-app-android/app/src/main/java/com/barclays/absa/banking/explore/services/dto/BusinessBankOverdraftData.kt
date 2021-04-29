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

package com.barclays.absa.banking.explore.services.dto

import android.os.Parcelable
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusinessBankOverdraftData(
        @JsonProperty("disableApply")
        val disableApply: Boolean = true,

        @JsonProperty("vclOfferAmt")
        val vclOfferAmt: String = "",

        @JsonProperty("moreVCLInfoUrl")
        val moreVCLInfoUrl: String = "",

        @JsonProperty("bcaUrl")
        val bcaUrl: String = "",

        @JsonProperty("offersBBOverdraftEnum")
        val offersBBOverdraftEnum: String = "",

        @JsonProperty("existingBBOverdraftLimit")
        val existingBBOverdraftLimit: String = "",

        @JsonProperty("utilizedOverdraftLimit")
        val utilizedOverdraftLimit: String = ""
) : BaseModel, Parcelable