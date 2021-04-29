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

class AddressDetails : BaseModel {
    var residentialStatusCode: String = ""
    var residentialStatusValue: String = ""
    var residentialAddressLine1: String = ""
    var residentialAddressLine2: String = ""

    @JsonProperty("residentialSubUrb")
    var residentialSuburb: String = ""

    var residentialCity: String = ""

    @JsonProperty("residentialPostCode")
    var residentialPostalCode: String = ""

    var residentialMarketPropertyValue: String = ""
    var postalAddressLine1: String = ""
    var postalAddressLine2: String = ""

    @JsonProperty("postalSubUrb")
    var postalSuburb: String = ""
    var postalCity: String = ""

    @JsonProperty("postalPostCode")
    var postalCode: String = ""

}