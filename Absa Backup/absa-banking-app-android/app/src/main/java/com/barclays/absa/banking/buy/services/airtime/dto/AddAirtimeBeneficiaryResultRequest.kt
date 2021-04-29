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
package com.barclays.absa.banking.buy.services.airtime.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0330_ADD_AIRTIME_RESULT
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class AddAirtimeBeneficiaryResultRequest<T>(referenceNumber: String, hasImage: String,
                                            responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0330_ADD_AIRTIME_RESULT)
                .put(Transaction.SERVICE_TXNREF, referenceNumber)
                .put(Transaction.HAS_IMAGE, hasImage)
                .build()

        mockResponseFile = BeneficiariesMockFactory.addPaymentResults()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
