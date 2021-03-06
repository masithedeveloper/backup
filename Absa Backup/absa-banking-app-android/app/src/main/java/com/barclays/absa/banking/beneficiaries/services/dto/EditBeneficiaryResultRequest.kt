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
package com.barclays.absa.banking.beneficiaries.services.dto

import com.barclays.absa.banking.beneficiaries.services.AddPaymentBeneficiaryResponseParser
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0333_EDIT_PAYMENT_RESULT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class EditBeneficiaryResultRequest<T>(referenceNumber: String, hasImage: String,
                                      responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0333_EDIT_PAYMENT_RESULT)
                .put(Transaction.SERVICE_TXNREF, referenceNumber)
                .put(Transaction.HAS_IMAGE, hasImage)
                .build()

        mockResponseFile = "beneficiaries/op0333_edit_payment_result.json"
        responseParser = AddPaymentBeneficiaryResponseParser()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryPaymentObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
