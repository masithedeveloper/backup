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
package com.barclays.absa.banking.boundary.model

import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import java.io.Serializable

class PolicyClaim : Serializable {
    var itemAffected: String? = ""
    var additionalDamage: String? = ""
    var incidentDate: String? = ""
    var alternativeContactName: String? = ""
    var alternativeContactNumber: String? = ""
    var contactNumber: String? = ""
    var contactEmail: String? = ""
    var policyDetail: PolicyDetail? = null
    var beneficiaryDetail: BeneficiaryDetailObject? = null
    var claimCategory: String? = ""
    var causeCode: String? = ""
    var claimType: String? = ""
    var typeOfClaim: String? = ""
    var additionalDamagedItems: String? = ""
    var claimNumber: String? = ""
    var propertyAddress: String? = ""
}
