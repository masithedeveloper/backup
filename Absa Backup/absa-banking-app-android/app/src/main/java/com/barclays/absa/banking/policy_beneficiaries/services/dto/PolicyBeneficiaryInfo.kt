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

package com.barclays.absa.banking.policy_beneficiaries.services.dto

import com.barclays.absa.banking.shared.BaseModel

data class PolicyBeneficiaryInfo(var title: Pair<String, String> = Pair("", ""),
                                 var firstName: String = "",
                                 var surname: String = "",
                                 var initials: String = "",
                                 var idType: Pair<String, String>? = null,
                                 var idNumber: String = "",
                                 var dateOfBirth: String = "",
                                 var category: String = "",
                                 var roleNumber: String = "",
                                 var lifeClientCode: String = "",
                                 var relationship: Pair<String, String>? = null,
                                 var allocation: String = "",
                                 var cellphoneNumber: String = "",
                                 var emailAddress: String = "",
                                 var addressLine1: String = "",
                                 var addressLine2: String = "",
                                 var suburb: String = "",
                                 var town: String = "",
                                 var postalCode: String = "",
                                 var beneficiaryLifeClientCode: String = "",
                                 var beneficiaryName: String = "",
                                 var beneficiaryAllocation: String = "",
                                 var beneficiaryRoleNumber: String = "") : BaseModel
