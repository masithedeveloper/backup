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
package com.barclays.absa.banking.boundary.model.rewards

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class RewardMembershipDetails : ResponseObject() {

    @JsonProperty("txnRefID")
    val transactionReferenceId: String? = null
    @JsonProperty("monthlyFee")
    val monthlyFee: Amount? = null
    @JsonProperty("annualFee")
    val annualFee: Amount? = null
    @JsonProperty("chargeFreqID")
    val chargeFrequencyId: String? = null
    @JsonProperty("orderFreqDate")
    val orderFrequencyDate: String? = null
    @JsonProperty("fromAct")
    val fromAccount: String? = null
    @JsonProperty("actList")
    val accountList: List<RewardsAccountDetails>? = null
}
