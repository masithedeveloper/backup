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
package com.barclays.absa.banking.home.services.clickToCall.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.home.services.clickToCall.ClickToCallService

import com.barclays.absa.banking.home.services.clickToCall.ClickToCallService.OP2049_REQUEST_CALLBACK_OPCODE

class CallBackRequest<T>(secretCode: String, callBackDateTime: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2049_REQUEST_CALLBACK_OPCODE)
                .put(ClickToCallService.CALL_BACK_DATE, callBackDateTime)
                .put(ClickToCallService.SECRET_CODE, secretCode)
                .build()

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CallBackResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}