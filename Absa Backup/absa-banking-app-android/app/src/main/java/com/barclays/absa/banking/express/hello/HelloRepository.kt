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

package com.barclays.absa.banking.express.hello

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.crypto.SecureUtils
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.hmac.service.BaseRequest
import za.co.absa.networking.hmac.service.Request

class HelloRepository : Repository() {

    override val apiService = createXMMSService()

    override val service = "DeviceManagementFacade"
    override val operation = "Hello"

    override var mockResponseFile: String = "express/hello.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): Request {
        ExpressNetworkingConfig.deviceId = SecureUtils.getDeviceID()
        return createRequestBuilderWithDeviceDetail(baseRequest).build()
    }
}