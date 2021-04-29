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
package com.barclays.absa.banking.rewards.services.dto

import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.home.services.HomeScreenService.OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY

class RewardsTransactionsRequest<T> : ExtendedRequest<T> {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    constructor(responseListener: ExtendedResponseListener<T>) : super(responseListener) {
        params = RequestParams.Builder()
                .put(OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY)
                .put("membershipNumber", rewardsCacheService.getRewardsAccount()?.accountNumber)
                .build()
        mockResponseFile = "op0915_rewards_transaction_history.json"
        printRequest()
    }

    constructor(fromDate: String, toDate: String, responseListener: ExtendedResponseListener<T>) : super(responseListener) {
        params = RequestParams.Builder()
                .put("membershipNumber", rewardsCacheService.getRewardsAccount()?.accountNumber)
                .put(OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY)
                .put(Transaction.FROM_DATE, fromDate)
                .put(Transaction.TO_DATE, toDate)
                .build()

        mockResponseFile = "op0915_rewards_transaction_history.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionWrapper::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}