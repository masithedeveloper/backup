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

package com.barclays.absa.banking.express.securityNotification.resendNotificationDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.securityNotification.notificationDetails.dto.NotificationDetailsResponse
import com.barclays.absa.banking.express.securityNotification.notificationDetails.dto.SecurityNotificationDetails
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class ResendNotificationDetailsRepository : Repository() {
    private lateinit var securityNotificationDetails: SecurityNotificationDetails
    override val apiService: ApiService = createXSMSService()
    override val service: String = "SecurityNotificationFacade"
    override val operation: String = "Resend2FANotificationDetails"
    override var mockResponseFile: String = "express/securityNotification/resend_notification_details_response.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
            with(securityNotificationDetails) {
                baseRequest.addParameter("securityFunctionType", securityFunctionType)
                        .addParameter("securityNotificationType", securityNotificationType)
                        .addParameter("serviceOperation", serviceOperation)
                        .build()
            }

    suspend fun resendNotificationDetails(securityNotificationDetails: SecurityNotificationDetails): NotificationDetailsResponse? {
        this.securityNotificationDetails = securityNotificationDetails
        return submitRequest()
    }
}