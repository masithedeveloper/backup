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

package com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import za.co.absa.networking.dto.BaseResponse

class AccountCreateAndLinkResponse : BaseResponse() {
    @JsonProperty("createAndLinkAccountOutputVO")
    var accountCreateAndLinkStatus = AccountCreateAndLinkStatus()
}

class AccountCreateAndLinkStatus : BaseModel {
    var accountNumber: Long = 0L
    var accountCreated: Boolean = false
    var accountLinked: Boolean = false
    var payOutIndicator: String = ""
}