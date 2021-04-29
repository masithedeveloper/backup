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
import java.io.Serializable

class RedeemRewards : ResponseObject(), Serializable {

    @JsonProperty("absaRewards")
    var absaRewards: String? = null
    @JsonProperty("balance")
    var balance: Amount? = null
    @JsonProperty("dateOpened")
    var dateOpened: String? = null
    @JsonProperty("status")
    var status: String? = null
    @JsonProperty("earningsToDate")
    var earningsToDate: Amount? = null
    @JsonProperty("redeemedToDate")
    var redeemedToDate: Amount? = null
    @JsonProperty("earningsPerMonth")
    var earningsPerMonth: Amount? = null
    @JsonProperty("cellNo")
    var cellPhoneNumber: String? = null
    @JsonProperty("network")
    var networkList: List<RewardsRedemptionAirtime>? = null
    @JsonProperty("charityType")
    var charityList: List<RewardsRedemptionCharity>? = null
    @JsonProperty("toActList")
    var toAccountList: List<RewardsAccountDetails>? = null
    @JsonProperty("voucherVendor")
    var voucherList: List<RewardsRedemptionRetailVouchers>? = null
    @JsonProperty("partner")
    var partnerList: List<RewardsRedemptionPartner>? = null
}
