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

package com.barclays.absa.banking.express.shared.getCustomerDetails.dto

import com.barclays.absa.banking.shared.BaseModel

class FinanceDetails : BaseModel {
    var sourceOfIncome: String = ""
    var sourceOfFunds: String = ""
    var income: String = ""
    var expenses: String = ""
    var netSalaryIncomeMonth1: String = ""
    var netSalaryIncomeMonth2: String = ""
    var netSalaryIncomeMonth3: String = ""
}