/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.accountBalances

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AccountBalancesRepository : Repository() {

    private lateinit var accountNumber: String

    override val apiService = createXTMSService()

    override val service = "BalanceEnquiryFacade"
    override val operation = "GetBalancesForAccount"

    override var mockResponseFile = "express/get_balances_for_account.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.addParameter("accountNumber", accountNumber)
                .build()
    }

    suspend fun getAccountBalance(accountNumber: String): AccountBalanceResponse? {
        this.accountNumber = accountNumber
        return submitRequest()
    }
}

