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

package com.barclays.absa.banking.express.behaviouralRewards.fetchAvailableVouchers.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import za.co.absa.networking.dto.BaseResponse

class AvailableVouchersResponse : BaseResponse() {
    val voucherPartners: List<VoucherPartner> = arrayListOf()
}

class VoucherPartner : BaseModel {
    val partnerId: String = ""
    val partnerOffers: List<PartnerOffer> = arrayListOf()
    val rewardCategory: String = ""
    val partnerName: String = ""
    val partnerRegion: String = ""
    val partnerContact: String = ""
    val partnerAddress: String = ""
    val partnerURL: String = ""

    @JsonProperty("partnerTandC")
    val partnerTermsAndConditions: String = ""
    val partnerCategoryImage: String = ""
    val partnerImage: String = ""
    val imageType: String = ""
}

class PartnerOffer : BaseModel {
    val offerId: String = ""
    val offerTier: String = ""
    val offerExpiryDays: String = ""
    val offerDescription: String = ""
}