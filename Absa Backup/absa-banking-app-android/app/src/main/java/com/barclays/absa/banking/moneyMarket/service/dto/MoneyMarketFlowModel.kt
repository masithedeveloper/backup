/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.moneyMarket.service.dto

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.shared.BaseModel

class MoneyMarketFlowModel : BaseModel {
    var termsAndConditionsCompleted: Boolean = false
    var moneyMarketFlow: MoneyMarketFlowStates = MoneyMarketFlowStates.CONVERT
    var moneyMarketAccount: AccountObject = AccountObject()
    var moneyMarketDestinationAccount: AccountObject = AccountObject()
}

enum class MoneyMarketFlowStates(val key: Int) {
    CONVERT(0),
    WITHDRAW(1)
}