/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.beneficiaries.services.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0312_VIEW_TRANSACTION_DETAILS
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class TransactionDetailsRequest<T>(responseListener: ExtendedResponseListener<T>, beneficiaryId: String,
                                   referenceNumber: String, beneficiaryType: String) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0312_VIEW_TRANSACTION_DETAILS)
                .put(Transaction.SERVICE_REF_NO, referenceNumber)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, beneficiaryType)
                .build()

        mockResponseFile = "op0312_view_transaction_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ViewTransactionDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
