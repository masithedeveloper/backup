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
package com.barclays.absa.banking.payments.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

import com.barclays.absa.banking.payments.services.PaymentsService.OP0866_FETCH_FUTURE_DATED_PAYMENTS

class FutureDatedPaymentsRequest<T>(futureDatedPaymentType: String, accountNumber: String,
                                    responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0866_FETCH_FUTURE_DATED_PAYMENTS)
                .put("futureDatedType", futureDatedPaymentType)
                .put("accountNumber", accountNumber)
                .build()

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = FutureDatedPaymentListResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}