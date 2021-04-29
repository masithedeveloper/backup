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
package com.barclays.absa.banking.boundary.shared.dto

import android.os.Build
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0819_VALIDATE_SECURITY_CODE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.utils.DeviceUtils
import com.barclays.absa.crypto.SecureUtils

class SecurityCodeVerificationRequest<T>(securityCode: String?, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        val deviceID = SecureUtils.getDeviceID()
        val requestParamsBuilder = RequestParams.Builder()
                .put(OP0819_VALIDATE_SECURITY_CODE)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(Transaction.MANUFACTURER, Build.MANUFACTURER)
                .put(Transaction.MODEL, Build.MODEL)
                .put(Transaction.IMEI, deviceID)
                .put(Transaction.DEVICE_ID, deviceID)

        securityCode?.let { requestParamsBuilder.put(Transaction.SECURITY_CODE, it) }
        params = requestParamsBuilder.build()

        mockResponseFile = "transaction_verification/op0819_validate_security_code.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionVerificationValidateCodeResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}