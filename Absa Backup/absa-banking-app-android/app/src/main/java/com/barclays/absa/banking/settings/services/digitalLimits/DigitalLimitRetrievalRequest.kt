/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.settings.services.digitalLimits

import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0630_PROFILE_PAGE
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class DigitalLimitRetrievalRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder().put(OP0630_PROFILE_PAGE).build()
        mockResponseFile = "profile/op0630_profile_page.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DigitalLimit::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}