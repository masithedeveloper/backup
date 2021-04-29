/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.integration.deviceScoring

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP2106_DEVICE_PROFILING_LOGIN_SCORE
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class DeviceProfilingScoreRequest<T>(function: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        mockResponseFile = "device_profiling/op2106_login_score_response.json"
        val paramsBuilder = RequestParams.Builder()
        paramsBuilder.put(OP2106_DEVICE_PROFILING_LOGIN_SCORE)
        paramsBuilder.put("serVer", "2.0")
        if (function.isNotEmpty()) {
            paramsBuilder.put("function", function)
        }
        params = paramsBuilder.build()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionResponse::class.java as Class<T>

    override fun isEncrypted() = true
}