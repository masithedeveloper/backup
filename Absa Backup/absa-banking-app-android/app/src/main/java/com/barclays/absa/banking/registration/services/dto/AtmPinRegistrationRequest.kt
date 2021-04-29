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
package com.barclays.absa.banking.registration.services.dto

import com.barclays.absa.banking.boundary.model.RegisterProfileDetail
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0990_DECOUPLE_REGISTRATION_USING_ATM
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.utils.DeviceUtils

class AtmPinRegistrationRequest<T>(cardNumber: String, serverPath: String,
                                   atmPin: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(serverPath, responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0990_DECOUPLE_REGISTRATION_USING_ATM)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(Transaction.DECOUPLE_REGISTRATION_ATM_CARD_NUMBER, cardNumber.replace(" ", "").trim())
                .put(Transaction.DECOUPLE_REGISTRATION_ATM_PIN, atmPin.trim())
                .build()

        mockResponseFile = MockFactory.register()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RegisterProfileDetail::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}