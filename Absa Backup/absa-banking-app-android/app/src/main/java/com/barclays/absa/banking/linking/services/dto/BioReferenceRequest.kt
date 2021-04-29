/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.linking.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.linking.services.LinkingService
import com.barclays.absa.crypto.SecureUtils

class BioReferenceRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder(LinkingService.OP2207_GET_BIO_REFERENCE)
                .put("imei", SecureUtils.getDeviceID())
                .build()

        mockResponseFile = "linking/op2207_get_bio_reference.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = BioReferenceResponse::class.java as Class<T>

    override fun isEncrypted() = true
}

class BioReferenceResponse : ResponseObject() {
    var referenceNumber: String = ""
}