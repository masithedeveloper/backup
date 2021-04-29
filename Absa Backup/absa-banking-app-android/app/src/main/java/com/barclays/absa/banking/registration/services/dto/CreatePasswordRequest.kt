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

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0987_CREATE_PASS_RESULT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class CreatePasswordRequest<T>(password: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    companion object {
        private var stubAlreadyTriggered: Boolean = false
    }

    init {
        params = RequestParams.Builder()
                .put(OP0987_CREATE_PASS_RESULT)
                .put(Transaction.PASSWORD, password)
                .build()

        mockResponseFile = if (stubAlreadyTriggered) {
            "registration/op0987_create_password.json"
        } else {
            stubAlreadyTriggered = true
            "registration/op0987_create_password_security_code.json"
        }
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreatePasswordResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}