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

package com.barclays.absa.banking.express.shared.getCustomerDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

open class EmploymentInfo : BaseModel {
    @JsonProperty("highestLvlOfEducation")
    var highestLevelOfEducation: String = ""
    var occupationStatus: String = ""
    var nameOfEmployer: String = ""
    var employerAddress: AddressInfo = AddressInfo()
    var employedSince: String = ""
    var occupationLevel: String = ""
    var occupation: String = ""
    var occupationSector: String = ""
    var matricQualificationFlag: String = ""
    var sourceOfIncome: String = ""
    var sourceOfFunds: String = ""
}