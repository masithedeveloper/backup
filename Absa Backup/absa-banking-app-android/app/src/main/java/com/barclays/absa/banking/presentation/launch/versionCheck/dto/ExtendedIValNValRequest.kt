/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.presentation.launch.versionCheck.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.presentation.launch.versionCheck.IValNValService.Companion.OP0001_IVAL_NVAL
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.DeviceUtils

class ExtendedIValNValRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        val deviceID = SecureUtils.getDeviceID()

        params = RequestParams.Builder(OP0001_IVAL_NVAL)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(Transaction.IMEI, deviceID)
                .build()

        mockResponseFile = "ival_nval/op0001_ival_nval.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = IValNValResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = false
}