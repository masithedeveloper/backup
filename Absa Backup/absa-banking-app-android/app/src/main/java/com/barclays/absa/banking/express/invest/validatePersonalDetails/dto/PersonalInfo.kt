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

package com.barclays.absa.banking.express.invest.validatePersonalDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class PersonalInfo : BaseModel {
    var title: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var initials: String = ""
    var identityNo: String = ""
    var nationality: String = ""
    var countryOfBirth: String = ""

    @JsonProperty("contryOfResidence")
    var countryOfResidence: String = ""
    var dateOfBirth: String = ""
    var maritalStatus: String = ""
    var maritalContract: String = ""

    @JsonProperty("casaRefNo")
    var casaReferenceNumber: String = ""
    var gender: String = ""
    var homeLanguage: String = ""
}