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
 *
 */
package com.barclays.absa.banking.lawForYou.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.lawForYou.services.LawForYouService.Companion.OP2153_GET_COVER_AMOUNT_LAW_FOR_YOU
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse

class LawForYouCoverAmountsRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2153_GET_COVER_AMOUNT_LAW_FOR_YOU).build()
        mockResponseFile = "law_for_you/op2153_get_cover_amounts.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = CoverAmountsResponse::class.java as Class<T>
    override fun isEncrypted() = true
}