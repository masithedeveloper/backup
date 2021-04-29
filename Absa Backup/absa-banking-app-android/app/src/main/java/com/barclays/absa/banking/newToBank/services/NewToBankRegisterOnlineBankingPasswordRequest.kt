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

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2048_REGISTER_ONLINE_BANKING_PASSWORD
import com.barclays.absa.banking.newToBank.services.dto.RegisterOnlineBankingPasswordResponse

class NewToBankRegisterOnlineBankingPasswordRequest<T>(password: String, tokenNumber: String, nonce: String, clientTypeGroup: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(BuildConfigHelper.nCipherServerPath, responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2048_REGISTER_ONLINE_BANKING_PASSWORD)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.PASSWORD, password)
                .put(NewToBankParams.TOKEN_NUMBER.key, tokenNumber)
                .put(NewToBankParams.NONCE.key, nonce)
                .put(NewToBankParams.CLIENT_TYPE_GROUP.key, clientTypeGroup)
                .build()

        mockResponseFile = "new_to_bank/op2048_set_digital_password.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RegisterOnlineBankingPasswordResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}
