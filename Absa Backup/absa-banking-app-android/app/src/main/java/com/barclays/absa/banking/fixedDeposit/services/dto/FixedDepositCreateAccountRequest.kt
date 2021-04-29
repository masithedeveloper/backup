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

import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.Companion.OP2071_CREATE_ACCOUNT
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class FixedDepositCreateAccountRequest<T>(extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2071_CREATE_ACCOUNT)
                .put(Transaction.PRODUCT_CODE, "03040")
                .put(FixedDepositParameters.CATEGORY_CODE, "aoctd002")
                .build()
        mockResponseFile = "fixed_deposit/op0271_fixed_deposit_create_account.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = FixedDepositCreateAccountResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}