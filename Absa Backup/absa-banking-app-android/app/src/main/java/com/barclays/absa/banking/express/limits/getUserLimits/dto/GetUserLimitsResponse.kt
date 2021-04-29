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
 */

package com.barclays.absa.banking.express.limits.getUserLimits.dto

import za.co.absa.networking.dto.BaseResponse

class GetUserLimitsResponse : BaseResponse() {
    var interAccountTransferLimit: String = ""
    var paymentLimit: String = ""
    var futureDatedPaymentLimit: String = ""
    var minFutureDatedPaymentLimit: String = ""
    var maxFutureDatedPaymentLimit: String = ""
    var recurringPaymentLimit: String = ""
    var minRecurringPaymentLimit: String = ""
    var maxRecurringPaymentLimit: String = ""
    var interAccountTransferLimitUsed: String = ""
    var interAccountTransferLimitAvailable: String = ""
    var minInterAccountTransferLimit: String = ""
    var maxInterAccountTransferLimit: String = ""
    var paymentLimitUsed: String = ""
    var paymentLimitAvailable: String = ""
    var minPaymentLimit: String = ""
    var maxPaymentLimit: String = ""
    var cashSendDailyUsed: String = ""
    var cashSendMonthlyUsed: String = ""

    // TODO: Store of Value limits - can probably remove these
    var sovLimit: String = ""
    var sovUsed: String = ""
    var sovPaymentLimit: String = ""
    var sovPaymentLimitUsed: String = ""
    var sovPrepaidLimit: String = ""
    var sovPrepaidLimitUsed: String = ""
    var sovCashSendLimit: String = ""
    var sovCashSendLimitUsed: String = ""
}