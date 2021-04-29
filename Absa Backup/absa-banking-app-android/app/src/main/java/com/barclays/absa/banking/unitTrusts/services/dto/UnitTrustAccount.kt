/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class UnitTrustAccount : BaseModel {
    var accountName: String = ""
    var accountNumber: String = ""
    @JsonProperty("accountAvlUnits")
    var availableAccountUnits: String = ""
    var availableBalance: String = ""
    var accountUnits: String = ""
    var accountBalance: String = ""
    @JsonProperty("accountHoldername")
    var accountHolderName: String = ""
    @JsonProperty("fundDetails")
    var unitTrustFundList: List<UnitTrustFund> = listOf()
    var fundStatus: Boolean = false
}