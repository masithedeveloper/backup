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
 */

package com.barclays.absa.banking.manage.profile.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.manage.profile.services.ManageProfileService.ManageProfileServiceParameters.OP2099_GET_SUBURBS

class ManageProfileSuburbLookupRequest<T>(area: String, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2099_GET_SUBURBS)
                .put("area", area)
                .build()
        mockResponseFile = "shared/op2099_get_suburbs.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = PostalCodeLookUpResponse::class.java as Class<T>

    override fun isEncrypted() = true
}