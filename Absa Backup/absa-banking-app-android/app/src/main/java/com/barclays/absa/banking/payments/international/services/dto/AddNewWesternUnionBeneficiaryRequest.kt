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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0867_ADD_NEW_WESTERN_UNION_BENEFICIARY
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class AddNewWesternUnionBeneficiaryRequest<T>(transactionReferenceId: String, extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0867_ADD_NEW_WESTERN_UNION_BENEFICIARY)
                .put(WesternUnionParameters.SERVICE_TRANSACTION_REFERENCE_ID, transactionReferenceId)
                .build()

        mockResponseFile = "international_payments/op0867_add_new_international_fund_transfer_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = AddNewWesternUnionBeneficiaryResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}