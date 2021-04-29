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

import com.barclays.absa.banking.boundary.model.AccountInfo
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class PolicyDetail : ResponseObject() {

    @JsonProperty("claimTypeST")
    var claimTypes: List<String>? = null

    @JsonProperty("paymentDTO")
    var accountInfo: AccountInfo? = null

    @JsonProperty("expiryDate")
    var expiryDate: String? = null

    @JsonProperty("inceptionDate")
    var inceptionDate: String? = null

    @JsonProperty("policyFee")
    var policyFee: Amount? = null

    @JsonProperty("componentsJsonBean")
    var shortTermPolicyComponents: List<PolicyComponent>? = null

    @JsonProperty("rolePlayerJsonBean")
    var longTermPolicyComponents: List<PolicyComponent>? = null

    @JsonProperty("dtoJsonBean")
    var policy: Policy? = null

    @JsonProperty("lifeBeneficiaries")
    var policyBeneficiaries: List<PolicyBeneficiary> = emptyList()

    @JsonProperty("maxBenAllowed")
    var maximumAllowedBeneficiaries: Int = -1

    @JsonProperty("noOfBeneficiaries")
    var numberOfBeneficiaries: Int = -1
}
