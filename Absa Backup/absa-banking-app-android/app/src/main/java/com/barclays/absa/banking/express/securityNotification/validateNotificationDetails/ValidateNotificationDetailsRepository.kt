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

package com.barclays.absa.banking.express.securityNotification.validateNotificationDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.securityNotification.notificationDetails.dto.NotificationDetailsResponse
import com.barclays.absa.banking.express.securityNotification.validateNotificationDetails.dto.ValidateNotificationDetailsRequest
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class ValidateNotificationDetailsRepository : Repository() {
    private lateinit var validateNotificationDetailsRequest: ValidateNotificationDetailsRequest
    override val apiService: ApiService = createXSMSService()
    override val service: String = "SecurityNotificationFacade"
    override val operation: String = "Validate2FANotificationDetails"
    override var mockResponseFile: String = "express/securityNotification/validate_notification_details_response.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
            with(validateNotificationDetailsRequest) {
                if (oneTimePinToBeVerified.isNotBlank()) {
                    baseRequest.addParameter("otpToBeVerified", oneTimePinToBeVerified)
                }
                baseRequest.addParameter("resendTransaction", resendTransaction.toString())
                        .addParameter("securityFunctionType", securityFunctionType)
                        .addParameter("securityNotificationType", securityNotificationType)
                        .addParameter("serviceOperation", serviceOperation)
                        .build()
            }

    suspend fun validateNotificationDetails(validateNotificationDetailsRequest: ValidateNotificationDetailsRequest): NotificationDetailsResponse? {
        this.validateNotificationDetailsRequest = validateNotificationDetailsRequest
        return submitRequest()
    }
}