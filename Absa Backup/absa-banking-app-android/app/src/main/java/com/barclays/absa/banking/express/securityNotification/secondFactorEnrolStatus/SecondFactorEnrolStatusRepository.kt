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
 *
 */

package com.barclays.absa.banking.express.securityNotification.secondFactorEnrolStatus

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.securityNotification.secondFactorEnrolStatus.dto.SecondFactorEnrolStatusResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class SecondFactorEnrolStatusRepository : Repository() {
    override val apiService: ApiService = createXSMSService()
    override val service: String = "SecurityNotificationFacade"
    override val operation: String = "GetSecondFactorEnrolStatusDetails"
    override var mockResponseFile: String = "express/securityNotification/second_factor_enrol_status_response.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest.build()

    suspend fun getSecondFactorEnrolStatus(): SecondFactorEnrolStatusResponse? = submitRequest()
}