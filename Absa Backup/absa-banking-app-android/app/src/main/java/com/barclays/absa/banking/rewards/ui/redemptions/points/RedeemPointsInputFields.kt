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

package com.barclays.absa.banking.rewards.ui.redemptions.points

import com.barclays.absa.banking.boundary.model.rewards.RewardsAccountDetails
import java.io.Serializable

class RedeemPointsInputFields : Serializable {
    var redeemType: String? = null
    var amount: String? = null
    var partnerName: String? = null
    var partnerID: String? = null
    var cellphoneNumber: String? = null
    var cardNumber: String? = null
    var toAccountList: List<RewardsAccountDetails>? = null
}