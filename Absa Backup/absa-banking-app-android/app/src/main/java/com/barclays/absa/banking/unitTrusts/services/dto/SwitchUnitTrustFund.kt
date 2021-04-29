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
import styleguide.forms.SelectorInterface

class SwitchUnitTrustFund : BaseModel, SelectorInterface {
    var fundCode: String = ""
    var fundName: String = ""
    var availFundsForPurchase: String = ""
    var fundObjective: String = ""
    var fundPdfUrl: String = ""
    var fundRisk: String = ""
    var fundTerm: String = ""
    var fundSuitableFor: String = ""
    var fundCore: String = ""
    var minLumpSumAmount: String = ""
    var minDebitOrderAmount: String = ""
    var fundCategory: String = ""

    override val displayValue: String?
        get() = fundName

    override val displayValueLine2: String?
        get() = ""
}