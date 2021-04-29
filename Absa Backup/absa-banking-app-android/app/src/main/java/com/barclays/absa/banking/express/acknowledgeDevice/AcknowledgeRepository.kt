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

package com.barclays.absa.banking.express.acknowledgeDevice

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.crypto.SecureUtils
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.hmac.service.BaseRequest

class AcknowledgeRepository : Repository() {

    override var apiService = createXMMSService()

    override val service = "DeviceManagementFacade"
    override val operation = "AcknowledgeDevice"

    override var mockResponseFile: String = "express/acknowledge_device.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        ExpressNetworkingConfig.deviceId = SecureUtils.getDeviceID()
        return createBaseRequestBuilderWithDeviceDetail(baseRequest).build()
    }
}