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
package com.barclays.absa.banking.express.getAllBalances

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.getAllBalances.dto.BalanceResponse
import za.co.absa.networking.hmac.service.BaseRequest

class GetAllBalancesRepository : Repository() {

    override val apiService = createXTMSService()

    override val service = "BalanceEnquiryFacade"
    override val operation = "GetAllBalances"

    override var mockResponseFile = "express/get_all_balances.json"

    suspend fun getAllBalances(cacheHeader: CacheHeader): BalanceResponse? {

        headers = when (cacheHeader) {
            CacheHeader.BALANCE -> mapOf(Pair("X-Absa-C2C", "BAL"))
            CacheHeader.PROFILE -> mapOf(Pair("X-Absa-C2C", "ACP"))
            CacheHeader.REWARDS -> mapOf(Pair("X-Absa-C2C", "RWD"))
            else -> emptyMap()
        }

        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.build()

}