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
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty


class Policy : ResponseObject(), Entry {

    @JsonProperty("policyDescription")
    val description: String? = null
    @JsonProperty("policyNumber")
    val number: String? = null
    @JsonProperty("policyType")
    val type: String? = null
    @JsonProperty("coverAmount")
    val coverAmount: Amount? = null
    @JsonProperty("premiumAmount")
    val monthlyPremium: Amount? = null
    @JsonProperty("policyStatus")
    val status: String? = null
    @JsonProperty("processingType")
    val processingType: String? = null
    @JsonProperty("manageClaims")
    val manageClaims: String? = null
    @JsonProperty("renewDate")
    val renewalDate: String? = null
    @JsonProperty("productCode")
    val productCode: String? = null

    override fun getEntryType(): Int {
        return Entry.POLICY
    }
}
