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

class FundSwitchInfo() : BaseModel {
    var unitTrustFund: UnitTrustFund = UnitTrustFund()
    var unitTrustAccountHolderName: String = ""
    var unitTrustAccountNumber: String = ""
    var incomeDistributionIndicator: String = "N"
    var fromFundCode: String = ""
    var toFundCode: String = ""
    var fromFundName: String = ""
    var toFundName: String = ""
    var switchOption: String = ""
    var unitsToSwitch: String = ""
    var incomeDistributionAccountName: String = ""
    var incomeDistributionBankname: String = ""
    var incomeDistributionBankCode: String = ""
    var incomeDistributionBankAccount: String = ""
    var incomeDistributionAccountType: String = ""
    var newDebitOrder: String = ""
    var debitOrderOperation: String = ""
    var debitOrderAccount: String = ""
    var debitOrderAccountType: String = ""
    var debitOrderAccountName: String = ""
    var debitOrderBank: String = ""
    var debitOrderBankCode: String = ""
    var debitOrderAmount: String = ""
    var debitOrderStartMonth: String = ""
    var debitOrderDay: String = ""
    var debitOrderPercentageIncrease: String = ""
    var debitOrderIncreaseEffectiveDate: String = ""
}