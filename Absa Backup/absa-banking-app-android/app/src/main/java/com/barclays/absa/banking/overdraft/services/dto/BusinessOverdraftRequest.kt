/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.ApplyBusinessOverdraftResponse
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.OP2157_APPLY_BUSINESS_OVERDRAFT

class BusinessOverdraftRequest<T>(offersBusinessBankProduct: String, existingOverdraftLimit: String, newOverdraftLimit: String, businessOverdraftResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(businessOverdraftResponseListener) {

    init {
        params = RequestParams.Builder(OP2157_APPLY_BUSINESS_OVERDRAFT)
                .put("offersBusinessBankProductsEnum", offersBusinessBankProduct)
                .put("existingOverdraftLimit", existingOverdraftLimit)
                .put("newOverdraftLimit", newOverdraftLimit)
                .build()

        mockResponseFile = OverdraftMockFactory.applyBusinessOverdraft()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ApplyBusinessOverdraftResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}