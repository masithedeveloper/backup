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
package com.barclays.absa.banking.payments.swift.services.request

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.swift.services.SwiftService
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftQuoteResponse
import com.barclays.absa.banking.shared.BaseModel

class SwiftQuoteRequest<T>(swiftQuoteRequestDataModel: SwiftQuoteRequestDataModel,
                           responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        swiftQuoteRequestDataModel.apply {
            params = RequestParams.Builder(SwiftService.OP2140_GET_QUOTE)
                    .put("toAccount", toAccount)
                    .put("transferType", "SWIFT")
                    .put("bopCategory", bopCategory)
                    .put("foreignCurrencyCode", foreignCurrencyCode)
                    .put("categoryFlow", categoryFlow)
                    .put("foreignCurrencyAmount", foreignCurrencyAmount)
                    .put("country", country)
                    .put("whoWillPay", whoWillPay)
                    .put("caseIDNumber", caseIDNumber)
                    .build()
        }

        mockResponseFile = "swift/op2140_swift_get_quote.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwiftQuoteResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}

data class SwiftQuoteRequestDataModel(val toAccount: String, val bopCategory: String, val foreignCurrencyCode: String,
                                      val categoryFlow: String, val foreignCurrencyAmount: String, val country: String,
                                      val whoWillPay: String, val caseIDNumber: String) : BaseModel