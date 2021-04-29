/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0886_GET_OUT_BOUNDING_PENDING_TRANSACTION
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters
import com.barclays.absa.utils.DeviceUtils

class OutBoundPendingTransactionRequest<T>(extendedResponseListener: ExtendedResponseListener<T>, beneficiaryId: String) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0886_GET_OUT_BOUNDING_PENDING_TRANSACTION)
                .put(WesternUnionParameters.SERVICE_WU_BENEFICIARY_NUMBER, beneficiaryId)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .build()
        printRequest()
        mockResponseFile = "international_payments/op0886_get_out_bound_pending_transaction.json"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = OutBoundPendingTransactionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}