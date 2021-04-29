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
package com.barclays.absa.banking.presentation.shared

import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0609_ONCEOFF_CASHSEND_ENCRYPTION
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

internal class PinBlockRequest<T>(atmPin: String, pinBlockResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(BuildConfigHelper.pinEncryptServerPath, pinBlockResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0609_ONCEOFF_CASHSEND_ENCRYPTION)
                .put(Transaction.SERVICE_ACCESS_PIN, atmPin)
                .build()
        mockResponseFile = "beneficiaries/op0609_onceoff_cashsend_encryption.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PINObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}