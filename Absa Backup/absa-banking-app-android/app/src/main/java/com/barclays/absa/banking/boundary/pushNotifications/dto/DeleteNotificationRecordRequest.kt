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
package com.barclays.absa.banking.boundary.pushNotifications.dto

import com.barclays.absa.banking.boundary.pushNotifications.PushNotificationService.OP0826_DELETE_PUSH_NOTIFICATION_RECORD
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.crypto.SecureUtils

class DeleteNotificationRecordRequest<T>(pushNotificationRegistrationToken: String,
                                         responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val app = BMBApplication.getInstance()
        params = RequestParams.Builder()
                .put(OP0826_DELETE_PUSH_NOTIFICATION_RECORD)
                .put(Transaction.I_VAL, app.iVal)
                .put(Transaction.PUSH_NOTIFICATION_DEVICE_ID, pushNotificationRegistrationToken)
                .put(Transaction.LOWERCASE_DEVICE_ID, SecureUtils.getDeviceID())
                .build()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}