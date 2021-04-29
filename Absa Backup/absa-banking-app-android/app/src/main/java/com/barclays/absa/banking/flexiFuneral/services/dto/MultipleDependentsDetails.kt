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

package com.barclays.absa.banking.flexiFuneral.services.dto

class MultipleDependentsDetails {
    var planCode: String = ""
    var coverAmount: String = ""
    var monthlyPremium: String = ""
    var dependentsInitials: String = ""
    var dependentsSurname: String = ""
    var dependentsGender: String = ""
    var dependentsDateOfBirth: String = ""
    var dependentsRelationship: String = ""
    var dependentsCategory: String = ""
    var dependentsCoverAmount: String = ""
    var dependentsPremium: String = ""
    var familyMemberQuotes = emptyList<CoverDetails>()
    var selectedCoverIndex: Int = -1
}