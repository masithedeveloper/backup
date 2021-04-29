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
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2047_REGISTRATION_NEW_APPLICATION_CUSTOMER
import com.barclays.absa.banking.newToBank.services.dto.RegistrationNewApplicationCustomerResponse

class NewToBankRegistrationNewApplicationCustomerRequest<T>(idNumber: String, PIN: String, nVal: String, clientTypeGroup: String,
                                                            responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(BuildConfigHelper.nCipherServerPath, responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2047_REGISTRATION_NEW_APPLICATION_CUSTOMER)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.ID_NUMBER, idNumber)
                .put(Transaction.PIN, PIN)
                .put(NewToBankParams.TOKEN_NUMBER.key, nVal)
                .put(NewToBankParams.CLIENT_TYPE_GROUP.key, clientTypeGroup)
                .build()

        mockResponseFile = "new_to_bank/op2047_set_digital_pin.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RegistrationNewApplicationCustomerResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}