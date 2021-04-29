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

import com.barclays.absa.banking.boundary.model.AccrualData
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class RewardsDetails : ResponseObject() {

    @JsonProperty("txnRefID")
    val transactionReferenceId: String? = null
    val monthlyFee: Amount? = null
    val annualFee: Amount? = null
    @JsonProperty("chargeFreqID")
    var chargeFrequencyId: String? = null
    @JsonProperty("orderFreqDate")
    var orderFrequencyDate: String? = null
    @JsonProperty("fromAct")
    var fromAccount: String? = null
    @JsonProperty("actList")
    val accountList: List<RewardsAccountDetails>? = null
    val accrualDataList: List<AccrualData>? = null
    val absaRewards: String? = null
    val dateOpened: String? = null
    val earningsPerMonth: Amount? = null
    val status: String? = null
    val earningsToDate: Amount? = null
    val redeemedToDate: Amount? = null
    val membershipTier: String = ""

}