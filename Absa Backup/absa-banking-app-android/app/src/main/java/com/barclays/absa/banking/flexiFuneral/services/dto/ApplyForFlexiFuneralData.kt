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

import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector

class ApplyForFlexiFuneralData {
    var bankName: String = ""
    var dayOfDebit: String = ""
    var accountToBeDebited: String = ""
    var accountNumber: String = ""
    var accountDescription: String = ""
    var planCode: String = ""
    var employmentStatus: CodesLookupDetailsSelector? = null
    var occupation: CodesLookupDetailsSelector? = null
    var sourceOfFund: CodesLookupDetailsSelector? = null
    var totalCoverAmount: String = ""
    var totalPremium: String = ""
    var isReplacement: String = ""
    var company: String = ""
}