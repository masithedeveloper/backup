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

package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.Companion.OP2076_GET_ACCOUNT_DETAIL
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class FixedDepositGetAccountDetailRequest<T>(accountNumber: String, extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2076_GET_ACCOUNT_DETAIL)
                .put(FixedDepositParameters.ACCOUNT_NUMBER, accountNumber)
                .build()
        mockResponseFile = "fixed_deposit/op0276_fixed_deposit_get_account_detail.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = FixedDepositAccountDetailsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}