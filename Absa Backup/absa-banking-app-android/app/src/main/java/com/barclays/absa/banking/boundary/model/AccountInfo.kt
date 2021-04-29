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

import java.io.Serializable

class AccountInfo : Serializable {
    var sourceOfFund: String? = ""
    var accountNumber: String? = ""
    var accountType: String? = ""
    var bankName: String? = ""
    var branchCode: String? = ""
    var branchName: String? = ""
    var premiumFrequency: String? = ""
    var nextPremiumDate: String? = ""
    var annualPremiumIncreaseRate: String? = ""
    var bankId: String = ""
    var branchId: String = ""
}
