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

package com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class InvestmentAccount : BaseModel {
    var noticePeriod: Int = 0

    @JsonProperty("withdrawlPercent")
    var withdrawalPercentage: Int = 0
    var investmentTerm: Int = 0
    var rateOption: Int = 0
    var frequencyCode: Int = 0
    var accountName: String = ""
    var cifKey: String = ""
    var productCode: String = ""
    var productType: Int = 0
    var productName: String = ""
    var sourceOfFunds: String = ""
    var initialInvestmentAmount: String = ""
    var creditRatePlanCode: String = ""

    @JsonProperty("rBAServiceStatus")
    var riskBasedServiceStatus: String = ""
    var riskRating: String = ""
}