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

package com.barclays.absa.banking.express.shared.getSavedApplications.dto

import com.barclays.absa.banking.shared.BaseModel

class SavedApplication : BaseModel {
    var productName: String = ""
    var referenceNumber: String = ""
    var productCode: String = ""
    var remainingDays: Long = 0L
    var overdraftValue: Double = 0.0
    var loanAmount: Double = 0.0
    var initialDeposit: String = ""
    var monthlyContribution: String = ""
    var expired: Boolean = false
    var quoteExist: Boolean = false
}