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
package com.barclays.absa.banking.rewards.services.dto

import com.barclays.absa.banking.boundary.model.rewards.RewardsMembershipUpdatedDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class RewardsMembershipDetailsUpdateRequest<T>(responseListener: ExtendedResponseListener<T>,
                                               transactionReferenceId: String) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0918_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE_RESULT)
                .put(Transaction.TXN_REF_ID, transactionReferenceId)
                .build()

        mockResponseFile = "op0918_membership_details_update.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsMembershipUpdatedDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}