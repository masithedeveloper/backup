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
package com.barclays.absa.banking.debiCheck.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class DebiCheckTransactionsRequest<T>(accountNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    companion object {
        const val OP2102_FETCH_DEBICHECK_TRANSACTIONS = "OP2102"
    }

    init {
        val calendar = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DAY_OF_YEAR, -40)
        }
        val fromDate = SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale()).format(calendar.time)
        val toDate: String = DateUtils.getTodaysDate()

        params = RequestParams.Builder(OP2102_FETCH_DEBICHECK_TRANSACTIONS)
                .put("accountNumber", accountNumber)
                .put("startDate", fromDate)
                .put("endDate", toDate)
                .build()

        mockResponseFile = "debicheck/op2102_fetch_transactions_for_mandate.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebiCheckTransactionsResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}