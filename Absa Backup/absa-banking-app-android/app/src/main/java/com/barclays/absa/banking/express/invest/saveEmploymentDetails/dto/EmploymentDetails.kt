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

package com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto

import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.TaxDetails
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class EmploymentDetails : BaseModel {
    var employmentStatusCode: String = ""
    var employmentStatusValue: String = ""
    var occupationCode: String = ""
    var occupationValue: String = ""
    var employmentSectorCode: String = ""
    var employmentSectorValue: String = ""
    var occupationLevelCode: String = ""
    var occupationLevelValue: String = ""
    var employerName: String = ""
    var employerPhysicalAddress: String = ""

    @JsonProperty("employerPhysicalAddressLine2")
    var employerPhysicalAddressLineTwo: String = ""

    @JsonProperty("employerSubUrb")
    var employerSuburb: String = ""
    var employerCity: String = ""

    @JsonProperty("employerPostCode")
    var employerPostalCode: String = ""
    var employedSince: String = ""

    var communicationLanguageCode: String = ""
    var communicationLanguageValue: String = ""

    var qualificationCode: String = ""
    var qualificationValue: String = ""

    @JsonProperty("sourceofIncome")
    var sourceOfIncomeValue: String = ""

    @JsonProperty("sourceofIncomeCode")
    var sourceOfIncomeCode: String = ""

    @JsonProperty("sourceofFunds")
    var sourceOfFundsValue: String = ""

    @JsonProperty("sourceofFundsCode")
    var sourceOfFundsCode: String = ""
    var taxDetails: List<TaxDetails> = emptyList()
    var accountNumber: Long = 0L
}