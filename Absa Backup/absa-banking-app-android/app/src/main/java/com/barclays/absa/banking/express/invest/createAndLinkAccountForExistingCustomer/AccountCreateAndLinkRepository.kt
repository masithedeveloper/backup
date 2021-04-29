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

package com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkRequest
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkResponse
import za.co.absa.networking.hmac.service.BaseRequest

class AccountCreateAndLinkRepository : Repository() {
    private lateinit var createAndLinkRequest: AccountCreateAndLinkRequest

    override val apiService = createXSMSService()
    override val service = "SaveAndInvestAccountFacade"
    override val operation = "ExistingCustomerCreateAndLinkAccountDetails"
    override var mockResponseFile: String = "express/invest/create_and_link_account.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addObjectParameter("createAndLinkAccountInputVO", createAndLinkRequest).build()

    suspend fun createAndLinkAccount(createAndLinkRequest: AccountCreateAndLinkRequest): AccountCreateAndLinkResponse? {
        this.createAndLinkRequest = createAndLinkRequest
        return submitRequest()
    }
}