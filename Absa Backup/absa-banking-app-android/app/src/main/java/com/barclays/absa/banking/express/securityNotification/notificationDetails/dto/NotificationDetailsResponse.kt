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

package com.barclays.absa.banking.express.securityNotification.notificationDetails.dto

import za.co.absa.networking.dto.BaseResponse

class NotificationDetailsResponse : BaseResponse() {
    var result: String = ""
    var securityNotificationType: String = ""
    var cellNumber: String = ""
    var resendsRemaining: Int = 0
    var otpRetriesLeft: Int = 0
    var secondFactorState: String = ""
}