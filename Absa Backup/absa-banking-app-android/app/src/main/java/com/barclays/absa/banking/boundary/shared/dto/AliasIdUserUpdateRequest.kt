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

import com.barclays.absa.banking.boundary.model.AliasIdUserUpdate
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0813_UPDATE_USER_ID
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class AliasIdUserUpdateRequest<T>(idNumber: String, idType: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0813_UPDATE_USER_ID)
                .put(Transaction.ID_NUMBER, idNumber)
                .put(Transaction.ID_TYPE, idType)
                .build()

        mockResponseFile = "op0813_update_user_id"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AliasIdUserUpdate::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}