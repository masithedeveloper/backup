/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.express.payments.paymentsSourceAccounts

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.dto.SourceAccountsResponse
import za.co.absa.networking.hmac.service.BaseRequest

open class PaymentsSourceAccountsRepository : Repository() {

    private lateinit var searchString: String

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryPaymentFacade"
    override val operation = "GetSourceAccounts"

    override var mockResponseFile = "express/payments/payments_get_source_accounts_response.json"

    suspend fun fetchSourceAccounts(searchString: String): SourceAccountsResponse? {
        this.searchString = searchString
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .enablePagination()
            .addParameter("searchString", searchString)
            .build()
}