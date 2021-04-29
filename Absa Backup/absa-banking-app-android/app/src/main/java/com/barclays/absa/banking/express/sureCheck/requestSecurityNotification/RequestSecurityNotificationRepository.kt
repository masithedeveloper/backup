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
package com.barclays.absa.banking.express.sureCheck.requestSecurityNotification

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.dto.SecurityNotificationRequest
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.dto.SecurityNotificationResponse
import za.co.absa.networking.hmac.service.BaseRequest

class RequestSecurityNotificationRepository : Repository() {
    private lateinit var securityNotificationRequest: SecurityNotificationRequest
    override val apiService = createXTMSService()

    override val service = "SecurityNotificationFacade"
    override val operation = "RequestSecurityNotification"

    override var mockResponseFile = "express/sure_check/request_security_notification.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        baseRequest.addParameter("securityNotificationType", securityNotificationRequest.securityNotificationType)
                .addParameter("securityFunctionType", securityNotificationRequest.securityFunctionType)
                .addParameter("serviceOperation", securityNotificationRequest.serviceOperation)

        securityNotificationRequest.additionalSureCheckParameters.entries.forEach {
            baseRequest.addParameter(it.key, it.value)
        }

        return baseRequest.build()
    }

    suspend fun requestSecurityNotification(securityNotificationRequest: SecurityNotificationRequest): SecurityNotificationResponse? {
        this.securityNotificationRequest = securityNotificationRequest
        return submitRequest()
    }
}