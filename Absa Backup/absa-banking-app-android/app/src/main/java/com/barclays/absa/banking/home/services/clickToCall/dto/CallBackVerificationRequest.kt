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

import com.barclays.absa.banking.home.services.clickToCall.ClickToCallService.OP2050_VERIFY_SECRET_CODE_OPCODE

class CallBackVerificationRequest<T>(callBackVerificationDataModel: CallBackVerificationDataModel,
                                     callBackRequestExtendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(callBackRequestExtendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2050_VERIFY_SECRET_CODE_OPCODE)
                .put(ClickToCallService.SECRET_CODE, callBackVerificationDataModel.secretCode)
                .put(ClickToCallService.SECRET_CODE_VERIFIED, callBackVerificationDataModel.secretCodeVerified.toString())
                .put(ClickToCallService.UNIQUE_REFERENCE_NUMBER, callBackVerificationDataModel.uniqueRefNo)
                .build()

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CallBackVerificationResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}