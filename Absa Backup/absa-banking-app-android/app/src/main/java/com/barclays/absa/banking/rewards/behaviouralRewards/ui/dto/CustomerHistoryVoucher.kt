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
 */

package com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class CustomerHistoryVoucher(var partnerId: String = "",
                             var offerDescription: String = "",
                             var offerExpiryDateTime: String = "",
                             var rewardPinVoucher: String = "",
                             var voucherImage: String = "",
                             var isFromClaimScreen: Boolean = false,
                             var offerStatus: String = "",
                             var termsAndConditions: String = "",
                             var redemptionDate: Date = Date()) : Parcelable