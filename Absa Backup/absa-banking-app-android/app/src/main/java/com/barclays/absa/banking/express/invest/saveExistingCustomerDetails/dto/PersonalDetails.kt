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
import com.fasterxml.jackson.annotation.JsonProperty

class PersonalDetails : BaseModel {
    var productCode: String = ""
    var productName: String = ""
    var crpCode: String = ""
    var titleCode: String = ""
    var titleValue: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var initials: String = ""
    var identityID: String = ""
    var genderCode: String = ""
    var genderValue: String = ""

    @JsonProperty("doB")
    var dateOfBirth: String = ""

    @JsonProperty("countryofResidenceCode")
    var countryOfResidenceCode: String = ""

    @JsonProperty("countryofResidenceValue")
    var countryOfResidenceValue: String = ""

    @JsonProperty("countryofBirthCode")
    var countryOfBirthCode: String = ""

    @JsonProperty("countryofBirthValue")
    var countryOfBirthValue: String = ""

    var nationalityCode: String = ""
    var nationalityValue: String = ""
    var referenceNumber: String = ""
    var applicationType: String = ""
    var homeLanguageCode: String = ""
    var homeLanguageValue: String = ""
    var maritalStatusCode: String = ""
    var spousalConsentCheck: String = ""
}