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
package com.barclays.absa.banking.boundary.model.creditCardInsurance

import java.io.Serializable

class CreditProtection: Serializable {
    var accountNo: String = ""
    var accountType: String = ""
    var age: String = ""
    var overDraftLimit: String = ""
    var policyDetails: String = ""
    var policyNumber: String = ""
    var monthlyPremium: String = ""
    var accountToBeDebited: String = ""
    var frequency: String = ""
    var dayOfDebit: String = ""
    var premiumAmount: String = ""
    var totalCoverAmount: String = ""
    var policyCommencementDate: String = ""
    var monthlyPremiumPercentage: String = ""
    var expiryDate: String = ""
}