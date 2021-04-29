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

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0311_BENEFICIARY_DETAILS
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class BeneficiaryDetailsRequest<T>(beneficiaryId: String, beneficiaryType: String,
                                   responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0311_BENEFICIARY_DETAILS)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, beneficiaryType.toLowerCase())
                .build()

        mockResponseFile = BeneficiariesMockFactory.beneficiaryDetails()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BeneficiaryDetailsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
