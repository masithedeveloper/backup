/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */
package com.barclays.absa.banking.recognition.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.recognition.services.BranchRecognitionMockFactory
import com.barclays.absa.banking.recognition.services.dto.BranchRecognitionService.Companion.OP2080_CUSTOMER_RATING_GET_FUNCTIONS_AND_PRODUCTS
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionResponse
import com.barclays.absa.utils.DeviceUtils

class BranchRecognitionRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2080_CUSTOMER_RATING_GET_FUNCTIONS_AND_PRODUCTS)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .build()
        mockResponseFile = BranchRecognitionMockFactory.getFunctionsAndProducts()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BranchRecognitionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}