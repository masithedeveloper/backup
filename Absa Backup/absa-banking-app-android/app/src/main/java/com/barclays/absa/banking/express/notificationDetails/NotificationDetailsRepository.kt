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

package com.barclays.absa.banking.express.notificationDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.notificationDetails.dto.NotificationDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class NotificationDetailsRepository : Repository() {
    lateinit var accountNumber: String
    lateinit var userNumber: String

    override val apiService = createXTMSService()
    override val service = "ManageNotificationDetailsFacade"
    override val operation = "GetNotificationDetails"
    override var mockResponseFile = "express/notification_details_success_response.json"

    suspend fun fetchNotificationDetails(accountNumber: String, userNumber: String): NotificationDetailsResponse? {
        this.userNumber = userNumber
        this.accountNumber = accountNumber
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("accountNumber", accountNumber)
            .addParameter("userNumber", userNumber)
            .build()
}