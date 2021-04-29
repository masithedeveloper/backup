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

package com.barclays.absa.banking.express.shared.updateCustomerDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class PersonalDetails : BaseModel {
    var titleCode: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var initials: String = ""

    @JsonProperty("identityNo")
    var identityNumber: String = ""

    var nationalityCode: String = ""

    @JsonProperty("countryofBirthCode")
    var countryOfBirthCode: String = ""

    @JsonProperty("countryofResidenceCode")
    var countryOfResidenceCode: String = ""

    var dateOfBirth: String = ""

    var maritalStatusCode: String = ""

    @JsonProperty("maritalContractValue")
    var maritalContract: String = ""

    @JsonProperty("casaRefNo")
    var casaReferenceNumber: String = ""

    var gender: String = ""

    var homeLanguage: String = ""

    var residentialStatus: String = ""

    @JsonProperty("kinFullName")
    var nextOfKinFullName: String = ""

    @JsonProperty("kinSurname")
    var nextOfKinSurname: String = ""

    @JsonProperty("kinRelationShipValue")
    var nextOfKinRelationship: String = ""

    @JsonProperty("kinContactNumber")
    var nextOfKinContactNumber: String = ""

    @JsonProperty("kinEmail")
    var nextOfKinEmailAddress: String = ""

    @JsonProperty("saTaxNumber")
    var localTaxNumber: String = ""
}