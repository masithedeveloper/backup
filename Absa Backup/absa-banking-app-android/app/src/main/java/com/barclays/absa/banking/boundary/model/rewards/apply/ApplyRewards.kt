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
package com.barclays.absa.banking.boundary.model.rewards.apply

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

open class ApplyRewards : ResponseObject() {

    var languageCode: String? = null
    @JsonProperty("customerType")
    var customerType: String? = null
    @JsonProperty("addressLine1")
    var addressLine1: String? = null
    @JsonProperty("addressLine2")
    var addressLine2: String? = null
    @JsonProperty("suburb")
    var suburb: String? = null
    @JsonProperty("city")
    var city: String? = null
    @JsonProperty("postalCode")
    var postalCode: String? = null
    @JsonProperty("email")
    var email: String? = null
    @JsonProperty("homeTel")
    var homeTelephone: String? = null
    @JsonProperty("workTel")
    var workTelephone: String? = null
    @JsonProperty("cellNo")
    var cellphoneNumber: String? = null
    @JsonProperty("orderActList")
    var orderAccounttList: List<AccountObject>? = null
    @JsonProperty("allActList")
    var allAccountList: List<AccountObject>? = null
    @JsonProperty("balNotYetClrdDt")
    var balanceNotYetClearDate: String? = null
    @JsonProperty("resAddressLine1")
    var residenceAddressLine1: String? = null
    @JsonProperty("resAddressLine2")
    var residenceAddressLine2: String? = null
    @JsonProperty("resSuburb")
    var residenceSuburb: String? = null
    @JsonProperty("resCity")
    var residenceCity: String? = null
    @JsonProperty("resPostalCode")
    var residencePostalCode: String? = null
    @JsonProperty("countryOfResidence")
    var countryOfResidence: String? = null
    @JsonProperty("solicitIndicator")
    var solicitIndicator: String? = null
    @JsonProperty("spokenLanguageCode")
    var spokenLanguageCode: String? = null
    @JsonProperty("spokenLanguageName")
    var spokenLanguageName: String? = null
    @JsonProperty("writtenLanguageCode")
    var writtenLanguageCode: String? = null
    @JsonProperty("writtenLanguageName")
    var writtenLanguageName: String? = null
    @JsonProperty("monthlyAmt")
    var monthlyAmount: Amount? = null
    @JsonProperty("yearlyAmt")
    var yearlyAmount: Amount? = null
    @JsonProperty("corrTitle")
    var correspondenceTitle: String? = null
    @JsonProperty("homeFax")
    var homeFax: String? = null
    @JsonProperty("acHomeFax")
    var homeFaxCode: String? = null
    @JsonProperty("officeFax")
    var officeFax: String? = null
    @JsonProperty("acOfficeFax")
    var officeFaxCode: String? = null

}
