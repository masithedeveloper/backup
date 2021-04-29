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
package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.shared.BaseModel

class RedeemFundDetails() : BaseModel {
    var successMessage: String = ""
    var unitTrustAccountNumber: String = ""
    var unitTrustAccountHolder: String = ""
    var fundName: String = ""
    var redeemAmount: Int = 0
    var fundUnits: String = ""
    var redeemAll: String = ""
    var cancelDebitOrder: String = ""
    var redemptionAccountNumber: String = ""
    var redemptionAccountHolder: String = ""
    var accountType: String = ""
    var redemptionAccountBankName: String = ""
    var redemptionAccountBankCode: String = ""
    var dateAndTime: String = ""
}

