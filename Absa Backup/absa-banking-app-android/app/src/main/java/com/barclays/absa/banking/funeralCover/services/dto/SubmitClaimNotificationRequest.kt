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
package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.notification.SubmitClaim
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0854_VALIDATE_CLAIM_NOTIFICATION_SUBMISSION
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class SubmitClaimNotificationRequest<T>(referenceNumber: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0854_VALIDATE_CLAIM_NOTIFICATION_SUBMISSION)
                .put(Transaction.NOTIFICATION_REFERENCE_NUMBER, referenceNumber)
                .build()

        mockResponseFile = "policy_stubs/op0854_validate_claim_notification_submission.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SubmitClaim::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}