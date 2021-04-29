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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.OP0860_GET_RETAIL_ACCOUNTS
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse

class RetailAccountRequest<T>(responseListener: ExtendedResponseListener<T>, accountType: String) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0860_GET_RETAIL_ACCOUNTS)
                .put(FuneralCoverQuoteService.ACC_TYPE_FILTER, accountType)
                .build()

        mockResponseFile = "funeral_cover/op0860_get_retail_accounts.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RetailAccountsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}