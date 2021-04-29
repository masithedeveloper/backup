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
package com.barclays.absa.banking.lotto.services

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.shared.BaseModel

class LottoSourceAccountsRequest(responseListener: ExtendedResponseListener<SourceAccountsResponse>) : ExtendedRequest<SourceAccountsResponse>(responseListener) {

    init {
        params = RequestParams.Builder("OP2128")
                .build()
        mockResponseFile = "lotto/op2128_lotto_fetch_source_accounts.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = SourceAccountsResponse::class.java

    override fun isEncrypted() = true
}

class SourceAccountsResponse : TransactionResponse() {
    var sourceAccounts = mutableListOf<SourceAccount>()
}

class SourceAccount : BaseModel {
    var accountNumber = ""
    var accountName = ""
    var accountType = ""
}
