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
package com.barclays.absa.banking.settings.services.digitalLimits

import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class DigitalLimitChangeConfirmationRequest<T>(stubDigitalLimitsChangeRequestSureCheck2Required: Boolean,
                                               transactionReferenceId: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OpCodeParams.OP0632_CHANGE_LIMITS_RESULT)
                .put(Transaction.TXN_REF_ID, transactionReferenceId)
                .build()

        this.mockResponseFile = if (stubDigitalLimitsChangeRequestSureCheck2Required)
            "profile/op0632_change_limits_result_surecheck2_required.json"
        else
            "profile/op0632_change_limits_result.json"

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DigitalLimitsChangeConfirmationResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}