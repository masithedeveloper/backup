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
package com.barclays.absa.banking.express.sureCheck

import android.os.Handler
import android.os.Looper
import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.dto.SecurityNotificationRequest
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler

abstract class SureCheckRepository : Repository() {

    abstract var securityFunctionType: String
    open val serviceOperation: String
        get() = "$service$operation"

    open var additionalSureCheckParameters: HashMap<String, Any> = HashMap()

    override fun handleSureCheckEvent() {
        val securityNotificationType = SecurityNotificationRequest().apply {
            securityFunctionType = this@SureCheckRepository.securityFunctionType
            serviceOperation = this@SureCheckRepository.serviceOperation
            additionalSureCheckParameters = this@SureCheckRepository.additionalSureCheckParameters
        }

        Handler(Looper.getMainLooper()).post { ExpressSureCheckHandler.processSureCheck(securityNotificationType) }
    }
}