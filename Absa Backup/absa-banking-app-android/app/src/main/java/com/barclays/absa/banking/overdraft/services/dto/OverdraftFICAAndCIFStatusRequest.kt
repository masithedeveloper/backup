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
package com.barclays.absa.banking.overdraft.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.model.FicaCheckResponse
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.OP2004_OVERDRAFT_FICA_CIF_STATUS

class OverdraftFICAAndCIFStatusRequest<T>(FICAAndCIFStatusResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(FICAAndCIFStatusResponseListener) {

    init {
        params = RequestParams.Builder().put(OP2004_OVERDRAFT_FICA_CIF_STATUS).build()
        mockResponseFile = OverdraftMockFactory.overdraftFICAAndCIFStatus()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = FicaCheckResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}