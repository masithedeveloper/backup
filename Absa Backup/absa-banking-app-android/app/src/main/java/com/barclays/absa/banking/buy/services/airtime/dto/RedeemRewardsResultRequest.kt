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

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

import com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0913_REDEEM_REWARDS_RESULT

class RedeemRewardsResultRequest<T>(transactionReferenceId: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0913_REDEEM_REWARDS_RESULT)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.CARD_CONFIRM_TXN_REF_ID, transactionReferenceId)
                .build()

        mockResponseFile = "rewards/op0913_redeem_rewards_confirmation.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsRedeemResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}