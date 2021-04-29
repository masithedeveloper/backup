/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.behaviouralRewards.acceptChallenge

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsAcceptChallengeRepository : Repository() {

    private lateinit var challengeId: String

    override val apiService = createXTMSService()
    override val service = "BehaviouralRewardsChallengesFacade"
    override val operation = "AcceptChallenge"
    override var mockResponseFile = "express/behavioural_rewards/behavioural_rewards_accept_challenge.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.addParameter("challengeId", challengeId).build()
    }

    suspend fun acceptChallenge(challengeId: String): BaseResponse? {
        this.challengeId = challengeId
        return submitRequest()
    }
}