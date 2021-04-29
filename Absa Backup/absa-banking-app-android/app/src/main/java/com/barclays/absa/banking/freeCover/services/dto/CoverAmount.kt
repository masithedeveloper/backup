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

import com.barclays.absa.banking.shared.BaseModel

class CoverAmount : BaseModel {
    var coverAmount: String = ""
    var outstandingPremiumAmount: String = ""
    var monthlyPremium: String = ""
    var loadingAmount: String = ""
    var saspremium: String = ""
    var planCode: String = ""
    var planCounter: String = ""
    var spousePlanCounter: String = ""
    var spousePlanCode: String = ""
    var spouseSumAssured: String = ""
    var spouseBenefitPremium: String = ""
    var firstHHorHO: String = ""
    var vatAmount: String = ""
    var serviceFee: String = ""
    var totalSTMonthlyPremium: String = ""
}