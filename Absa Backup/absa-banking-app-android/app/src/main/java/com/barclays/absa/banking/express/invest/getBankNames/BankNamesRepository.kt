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

package com.barclays.absa.banking.express.invest.getBankNames

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.getBankNames.dto.BankNameResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BankNamesRepository : Repository() {
    override val apiService = createXSMSService()
    override val service = "BranchCodeFacade"
    override val operation = "GetBankNamesWithBranchCountDetails"
    override var mockResponseFile: String = "express/invest/get_bank_names_with_branch_count.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest.build()

    suspend fun fetchBankNamesWithBranchCount(): BankNameResponse? {
        return submitRequest()
    }
}