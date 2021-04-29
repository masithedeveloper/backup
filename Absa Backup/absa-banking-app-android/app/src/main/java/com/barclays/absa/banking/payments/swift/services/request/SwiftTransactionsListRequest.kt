/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.services.request

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.swift.services.SwiftService
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionsListResponse

class SwiftTransactionsListRequest<T>(caseIDNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(SwiftService.OP2137_GET_TRANSACTIONS_LIST)
                .put("caseIDNumber", caseIDNumber)
                .build()
        mockResponseFile = if (caseIDNumber.isEmpty()) {
            "swift/op2137_swift_get_transaction_list.json"
        } else {
            "swift/op2137_swift_get_transaction_details.json"
        }
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = SwiftTransactionsListResponse::class.java as Class<T>

    override fun isEncrypted() = true
}