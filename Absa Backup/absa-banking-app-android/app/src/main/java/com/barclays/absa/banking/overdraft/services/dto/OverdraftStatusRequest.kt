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
package com.barclays.absa.banking.overdraft.services.dto

import com.barclays.absa.banking.boundary.model.OverdraftStatus
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0827_OVERDRAFT_STATUS
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory

class OverdraftStatusRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    enum class Offers { NONE, EXISTING_OVERDRAFT_LIMIT, NEW_OVERDRAFT_LIMIT }

    init {
        params = RequestParams.Builder().put(OP0827_OVERDRAFT_STATUS).build()
        mockResponseFile = OverdraftMockFactory.overdraftStatus()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftStatus::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}