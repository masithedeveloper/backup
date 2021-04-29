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
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftLevelTwoCategoriesResponse

class SwiftLevelTwoCategoriesRequest<T>(categoryFlow: String, toAccountNo: String, senderType: String, categoryDescription: String,
                                        responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder(SwiftService.OP2138_GET_LEVEL_2_CATEGORIES)
                .put("categoryFlow", categoryFlow)
                .put("transferType", "SWIFT")
                .put("toAccountNo", toAccountNo)
                .put("senderType", senderType)
                .put("categoryDescription", categoryDescription)
                .build()

        mockResponseFile = "swift/op2138_swift_get_level2_categories.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwiftLevelTwoCategoriesResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}