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

package com.barclays.absa.banking.atmAndBranchLocator.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class AtmBranchLocatorRequest<T>(latitude: String, longitude: String, radius: Int, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OpCodeParams.OP2051_BRANCH_LOCATOR)
                .put("latitude", latitude)
                .put("longitude", longitude)
                .put("radius", radius.toString())
                .build()

        mockResponseFile = "atm_branch_locator/op2051_atm_branch_locator.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = AtmBranchLocatorResponse::class.java as Class<T>

    override fun isEncrypted() = false
}