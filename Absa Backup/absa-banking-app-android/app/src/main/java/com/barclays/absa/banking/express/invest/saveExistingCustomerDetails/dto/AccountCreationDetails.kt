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

package com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class AccountCreationDetails : BaseModel {
    var categoryCode: String = ""
    var categoryName: String = ""

    @JsonProperty("liquidityPerc")
    var liquidityPercentage: String = ""
    var investmentAmount: String = ""

    @JsonProperty("capDay")
    var capitalizationDay: String = ""
    var termDays: Int = 0
    var automaticallyReinvest: String = ""
    var monthlyInterestPayments: String = ""
    var maturityDate: String = ""
    var automaticRenewal: String = ""
    var accountName: String = ""
    var initialPaymentDetails: InitialPaymentDetails = InitialPaymentDetails()
    var monthlyDebitOrder: MonthlyDebitOrder = MonthlyDebitOrder()
    var interestPayoutDetails: MonthlyDebitOrder = MonthlyDebitOrder()
    var savingFrequency: String = ""
    var receiveInterestFrequency: String = ""

    @JsonProperty("nextCapDate")
    var nextCapitilizationDate: String = ""

    @JsonInclude(JsonInclude.Include.ALWAYS)
    var investmentTerm: String = ""
    var creditRatePlan: String = ""

    @get:JsonProperty("isSameAccountDebited")
    var isSameAccountDebited: Boolean = false

    @JsonInclude(JsonInclude.Include.ALWAYS)
    var investmentDuration: String = ""
}