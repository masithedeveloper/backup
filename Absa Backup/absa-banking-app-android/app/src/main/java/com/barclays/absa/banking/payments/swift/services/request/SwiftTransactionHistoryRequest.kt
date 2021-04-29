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
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.swift.services.SwiftService
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftTransactionHistoryResponse
import com.barclays.absa.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class SwiftTransactionHistoryRequest<T>(accessAccountNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        val backDatedCalendar = DateUtils.getCalendar(DateUtils.getTodaysDate(), DateUtils.DASHED_DATE_PATTERN)
        backDatedCalendar.add(Calendar.MONTH, -3)

        params = RequestParams.Builder(SwiftService.OP0879_GET_TRANSACTION_HISTORY)
                .put("fromAccount", accessAccountNumber)
                .put("fromDate", SimpleDateFormat(DateUtils.DASHED_DATE_PATTERN, BMBApplication.getApplicationLocale()).format(backDatedCalendar.time))
                .put("toDate", DateUtils.getTodaysDate())
                .put("transferType", "SWIFT")
                .build()
        mockResponseFile = "swift/op0879_swift_get_transaction_history.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = SwiftTransactionHistoryResponse::class.java as Class<T>

    override fun isEncrypted() = true
}