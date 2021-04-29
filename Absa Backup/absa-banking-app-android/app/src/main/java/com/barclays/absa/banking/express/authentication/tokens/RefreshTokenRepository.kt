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

package com.barclays.absa.banking.express.authentication.tokens

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.hmac.service.BaseRequest

class RefreshTokenRepository : Repository() {

    override val apiService = createXMMSService()

    override val service = "AuthenticationFacade"
    override val operation = "RefreshToken"

    override var mockResponseFile = "express/refresh_access_token.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.addParameter("deviceId", ExpressNetworkingConfig.deviceId)
                .addParameter("accessToken", ExpressNetworkingConfig.accessToken)
                .build()
    }
}