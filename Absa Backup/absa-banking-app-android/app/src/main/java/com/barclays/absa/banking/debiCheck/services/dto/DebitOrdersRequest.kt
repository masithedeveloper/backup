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

import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.debiCheck.services.DebitOrderMockFactory
import com.barclays.absa.banking.debiCheck.services.DebitOrderService.Companion.FROM_ACCOUNT_NUMBER
import com.barclays.absa.banking.debiCheck.services.DebitOrderService.Companion.FROM_DATE
import com.barclays.absa.banking.debiCheck.services.DebitOrderService.Companion.OP2110_SEARCH_DEBIT_ORDER
import com.barclays.absa.banking.debiCheck.services.DebitOrderService.Companion.TO_DATE
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class DebitOrdersRequest<T>(debitOrderDataModel: DebitOrderDataModel, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2110_SEARCH_DEBIT_ORDER)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(FROM_ACCOUNT_NUMBER, debitOrderDataModel.fromAccountNumber)
                .put(FROM_DATE, debitOrderDataModel.fromDate)
                .put(TO_DATE, debitOrderDataModel.toDate)
                .build()
        mockResponseFile = DebitOrderMockFactory.debitOrderList(1)
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DebitOrderList::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}