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

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.newToBank.services.dto.PhotoMatchAndMobileLookUpResponse

import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2038_PHOTO_MATCH_AND_MOBILE_LOOKUP

class NewToBankPhotoMatchAndMobileLookupRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2038_PHOTO_MATCH_AND_MOBILE_LOOKUP)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .build()

        mockResponseFile = "new_to_bank/op2038_perform_photo_match_and_mobile_lookup.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PhotoMatchAndMobileLookUpResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
