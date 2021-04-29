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
package com.barclays.absa.banking.payments.services.multiple.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.payments.services.PaymentsMockFactory
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService

class MultiplePaymentResultRequest<T>(transactionReferenceId: String,
                                      extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder()
                .put(MultipleBeneficiaryPaymentService.OP0528_MULTIPLE_BENEFICIARIES_PAYMENT_RESULT)
                .put(Transaction.TXN_REF_ID, transactionReferenceId)
                .build()

        mockResponseFile = PaymentsMockFactory.multiplePaymentsResult()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PaymentResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}