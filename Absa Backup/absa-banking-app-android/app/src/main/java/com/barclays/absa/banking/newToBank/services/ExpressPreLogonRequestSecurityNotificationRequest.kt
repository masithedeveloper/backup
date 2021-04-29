/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.newToBank.services.dto.ExpressPreLogonRequestSecurityNotificationResponse

import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2045_PRE_LOGON_REQUEST_SECURITY_NOTIFICATION

class ExpressPreLogonRequestSecurityNotificationRequest<T>(cellNumber: String, securityFunctionType: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2045_PRE_LOGON_REQUEST_SECURITY_NOTIFICATION)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.CELL_NUMBER.key, cellNumber)
                .put(NewToBankParams.SECURITY_NOTIFICATION_TYPE.key, "SureCheck")
                .put(NewToBankParams.SECURITY_FUNCTION_TYPE.key, securityFunctionType)
                .build()

        mockResponseFile = "new_to_bank/OP2045_request_security_notification.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ExpressPreLogonRequestSecurityNotificationResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
