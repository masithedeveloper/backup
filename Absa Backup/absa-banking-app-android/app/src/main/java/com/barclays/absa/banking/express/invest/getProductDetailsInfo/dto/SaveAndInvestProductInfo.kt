/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.invest.getProductDetailsInfo.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class SaveAndInvestProductInfo : BaseModel {

    @JsonProperty("productCodeAndCRPCode")
    var productAndCreditRatePlanCode: ProductAndCreditRatePlanCode = ProductAndCreditRatePlanCode()
    var productName: String = ""

    @JsonProperty("crpName")
    var creditRatePlanName: String = ""
    var categoryName: String = ""
    var categoryCode: String = ""

    @JsonProperty("minInvestmentAmount")
    var minimumInvestmentAmount: Double = 0.0
    var minimumNotice: String = ""

    @JsonProperty("minInvestmentPeriod")
    var minimumInvestmentPeriod: String = ""

    @JsonProperty("maxInvestmentPeriod")
    var maximumInvestmentPeriod: String = ""
    var reinvestmentEnabled: String = ""
    var reinvestmentOptions: String = ""
    var receiveInterestFrequency: String = ""
    var capitalisationDay: String = ""
    var liquidityOptions: String = ""
    var interestRate: String = ""
    var productDescription: String = ""
    var preferredSavingsFrequency: String = ""
    var additionalPaymentViaDebitOrder: String = ""

    @JsonProperty("maxInvestmentAmount")
    var maximumInvestmentAmount: Double = 0.0

    @JsonProperty("ecMaxInvestmentAmount")
    var existingCustomerMaximumInvestmentAmount: Double = 0.0
    var interestCapitalizationDay: String = ""
}