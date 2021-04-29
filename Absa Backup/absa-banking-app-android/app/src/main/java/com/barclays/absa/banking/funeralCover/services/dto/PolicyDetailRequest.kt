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
package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0850_POLICY_DETAILS
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class PolicyDetailRequest<T>(policyType: String, policyNumber: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0850_POLICY_DETAILS)
                .put(Transaction.SERVICE_POLICY_NUMBER, policyNumber)
                .build()

        mockResponseFile = if (policyType == BMBConstants.EXERGY_POLICY_TYPE) {
            "insurance/op0850_exergy_policy_details.json"
        } else {
            "home_loan/op0850_policy_details.json"
        }
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PolicyDetail::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}