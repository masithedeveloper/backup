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

import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0919_REWARDS_DETAILS
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService

class RewardsDetailsRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    init {
        params = RequestParams.Builder()
                .put(OP0919_REWARDS_DETAILS)
                .put("membershipNumber", rewardsCacheService.getRewardsAccount()?.accountNumber)
                .build()
        mockResponseFile = "op0919_rewards_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}