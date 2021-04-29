/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.shared.services.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class SuburbResult : Serializable {
    @JsonProperty("streetPostCode")
    var streetPostalCode: String = ""

    var postalCode: String = ""

    @JsonProperty("suburbName")
    var suburb: String = ""

    @JsonProperty("townOrCityName")
    var townOrCity: String = ""
}