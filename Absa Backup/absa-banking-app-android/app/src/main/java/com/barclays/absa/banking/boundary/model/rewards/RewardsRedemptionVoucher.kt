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
package com.barclays.absa.banking.boundary.model.rewards

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class RewardsRedemptionVoucher : ResponseObject(), Serializable {

    @JsonProperty("id")
    val id: String? = null
    @JsonProperty("name")
    val name: String? = null
    @JsonProperty("faceValue")
    val faceValue: Amount? = null
    @JsonProperty("accrualCost")
    val accrualCost: Amount? = null

}