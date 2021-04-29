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

package com.barclays.absa.banking.boundary.model.policy

import java.io.Serializable

class PolicyBeneficiary : Serializable {
    var title: String = ""
    var firstName: String = ""
    var surname: String = ""
    var initials: String = ""
    var idType: String = ""
    var idNumber: String = ""
    var dateOfBirth: String = ""
    var relationship: String = ""
    var lifeClientCode: String = ""
    var cellphoneNumber: String = ""
    var emailAddress: String = ""
    var address: PolicyAddress? = null
    var roleNumber: String = ""
    var allocation: String = ""
    var fullName: String = ""
}