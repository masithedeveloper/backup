/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.Companion.OP2061_RETRIEVE_DEBIT_DAYS

class DebitPeriodRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder().put(OP2061_RETRIEVE_DEBIT_DAYS).build()
        mockResponseFile = "unit_trust/op2061_retrieve_debit_period.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitPeriod::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}