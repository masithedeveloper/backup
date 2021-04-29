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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralService.Companion.OP2143_FETCH_MAIN_MEMBER_COVER_AMOUNTS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class MainMemberCoverAmountsRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2143_FETCH_MAIN_MEMBER_COVER_AMOUNTS)
                .build()
        mockResponseFile = "flexi_funeral/op2143_flexi_funeral_main_member_cover_options.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = MainMemberCoverAmountsResponse::class.java as Class<T>

    override fun isEncrypted() = true
}