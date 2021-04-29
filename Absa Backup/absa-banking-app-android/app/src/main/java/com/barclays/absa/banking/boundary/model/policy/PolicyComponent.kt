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
package com.barclays.absa.banking.boundary.model.policy

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.PropertyAddress
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class PolicyComponent : Serializable {
    @JsonProperty("address")
    var propertyAddress: PropertyAddress? = null
    @JsonProperty("coverAmount")
    var coverAmount: Amount? = null
    @JsonProperty("premiumAmount")
    var premiumAmount: Amount? = null
    @JsonProperty("componentName")
    var componentName: String? = ""
    @JsonProperty("description")
    var description: String? = ""
}
