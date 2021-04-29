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

package com.barclays.absa.banking.express.securityNotification.validateNotificationDetails.dto

enum class SecurityNotificationResultStatus(val result: String) {
    RESULT_STATUS_PROCESSING("Processing"),
    RESULT_STATUS_REVERT("RevertBack"),
    RESULT_STATUS_FAILED("Failed"),
    RESULT_STATUS_REJECTED("Rejected"),
    RESULT_STATUS_PROCESSED("Processed")
}