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

package com.barclays.absa.banking.express.behaviouralRewards.acceptAndDisplayVoucherDetails.dto

import za.co.absa.networking.dto.BaseResponse

class BehaviouralRewardsAcceptAndDisplayVoucherDetailsResponse : BaseResponse() {
    val valid: String = ""
    val redemptionDateTime: String = ""
    val voucherPin: String = ""
    val partnerId: String = ""
    val partnerCategory: String = ""
    val partnerName: String = ""
    val partnerImage: String = ""
    val imageType: String = ""
    val offerId: String = ""
    val offerTier: String = ""
    val offerRandValue: String = ""
    val offerPointsValue: String = ""
    val offerExpiryDays: String = ""
    val offerDescription: String = ""
    val voucherExpiryDateTime: String = ""
}