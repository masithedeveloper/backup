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

package com.barclays.absa.banking.freeCover.services.dto

import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.shared.BaseModel

class ApplyFreeCoverData : BaseModel {

    var coverAmount: String = ""
    var monthlyPremium: String = ""
    var accountHolderName: String = ""
    var titleCode: String = ""
    var firstName: String = ""
    var surname: String = ""
    var initials: String = ""
    var idType: String = ""
    var idNumber: String = ""
    var dateOfBirth: String = ""
    var relationshipCode: String = ""
    var cellphoneNumber: String = ""
    var emailAddress: String = ""
    var addressLineOne: String = ""
    var addressLineTwo: String = ""
    var suburbRsa: String = ""
    var town: String = ""
    var postalCode: String = ""
    var sourceOfFund: String = ""
    var employmentStatus: CodesLookupDetailsSelector = CodesLookupDetailsSelector("", "", "")
    var occupation: CodesLookupDetailsSelector = CodesLookupDetailsSelector("", "", "")
}