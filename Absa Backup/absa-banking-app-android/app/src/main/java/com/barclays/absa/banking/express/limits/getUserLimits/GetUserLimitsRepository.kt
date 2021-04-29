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
package com.barclays.absa.banking.express.limits.getUserLimits

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.hmac.service.BaseRequest

class GetUserLimitsRepository : Repository() {

    override val apiService = createXTMSService()
    override val service = "ManageLimitsFacade"
    override val operation = "GetUserLimits"

    override var mockResponseFile = "express/limits/get_user_limits_response.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.build()
}