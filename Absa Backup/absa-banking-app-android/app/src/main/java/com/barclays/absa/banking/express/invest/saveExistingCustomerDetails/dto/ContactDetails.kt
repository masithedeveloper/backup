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

class ContactDetails : BaseModel {
    var preferredContactMethodCode: String = ""
    var preferredContactMethodValue: String = ""
    var homePhoneNumberCode: String = ""
    var homePhoneNumber: String = ""
    var workPhoneNumberCode: String = ""
    var workPhoneNumber: String = ""

    @JsonProperty("gsmnumber")
    var gsmNumber: String = ""

    @JsonProperty("alternateNumber")
    var alternativeNumber: String = ""

    var emailAddress: String = ""
}