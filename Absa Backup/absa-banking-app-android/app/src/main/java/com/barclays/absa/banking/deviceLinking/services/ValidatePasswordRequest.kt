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
package com.barclays.absa.banking.deviceLinking.services

import com.barclays.absa.banking.boundary.model.ValidatePasswordResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0986_VALIDATE_PASSWORD
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService

class ValidatePasswordRequest<T>(passwordDigit1: String, passwordDigit2: String, passwordDigit3: String,
                                 responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val appCacheService: IAppCacheService = getServiceInterface()
        val secureHomePageObject = appCacheService.getSecureHomePageObject()
        params =
                if (secureHomePageObject == null) {
                    RequestParams.Builder().put(OP0986_VALIDATE_PASSWORD).build()
                } else {
                    val passwordDigits = secureHomePageObject.passwordDigits
                    RequestParams.Builder()
                            .put(OP0986_VALIDATE_PASSWORD)
                            .put(Transaction.PWD_DIGIT_VALUE_1, passwordDigit1)
                            .put(Transaction.PWD_DIGIT_VALUE_2, passwordDigit2)
                            .put(Transaction.PWD_DIGIT_VALUE_3, passwordDigit3)
                            .put(Transaction.PWD_DIGIT_INDEX_1, passwordDigits?.get(0)?.index ?: "")
                            .put(Transaction.PWD_DIGIT_INDEX_2, passwordDigits?.get(1)?.index ?: "")
                            .put(Transaction.PWD_DIGIT_INDEX_3, passwordDigits?.get(2)?.index ?: "")
                            .put(Transaction.PWD_DIGIT_NAME_1, passwordDigits?.get(0)?.name ?: "")
                            .put(Transaction.PWD_DIGIT_NAME_2, passwordDigits?.get(1)?.name ?: "")
                            .put(Transaction.PWD_DIGIT_NAME_3, passwordDigits?.get(2)?.name ?: "")
                            .build()
                }

        mockResponseFile = "login/op0986_validate_password_success.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ValidatePasswordResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}