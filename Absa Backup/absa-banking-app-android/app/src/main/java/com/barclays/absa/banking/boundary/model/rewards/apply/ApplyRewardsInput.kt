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
package com.barclays.absa.banking.boundary.model.rewards.apply

class ApplyRewardsInput : ApplyRewards() {

     var statementDeliveryIndicator: String? = null
     var receivedMarketingMaterial: String? = null
     var marketingMethod: String? = null
     var chargeFrequencyID: String? = null
     var orderFrequencyDate: String? = null
     var fromAccount: String? = null
     var accountDescription: String? = null
     var debitDate: String? = null
     var accountBalance: String? = null
}
