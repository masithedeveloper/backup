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

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class UnitTrustFundsResponse : TransactionResponse() {
    @JsonProperty("getUnitTrustFundsFromMyAbsaRespDTO")
    val fundsDTO: UnitTrustFundsDTO = UnitTrustFundsDTO()
}

class UnitTrustFundsDTO : BaseModel {
    val successMessage: Boolean = true
    @JsonProperty("fundsMyAbsa")
    val funds: List<UnitTrustFund> = emptyList()
    @JsonProperty("fundRiskFilterList")
    private val fundRiskFilters: String = ""
    val fundFilterOptions: () -> ArrayList<String>
        get() = { ArrayList(fundRiskFilters.split("|")) }
}