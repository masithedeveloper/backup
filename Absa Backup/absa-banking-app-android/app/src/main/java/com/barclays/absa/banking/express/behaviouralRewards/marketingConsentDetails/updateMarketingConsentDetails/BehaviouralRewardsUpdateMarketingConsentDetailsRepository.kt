/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.updateMarketingConsentDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.dto.BehaviouralRewardsCurrentMarketingConsentDetailsResponse
import com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.dto.BehaviouralRewardsFetchMarketingConsentDetailsRequest
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsUpdateMarketingConsentDetailsRepository : Repository() {

    private lateinit var behaviouralRewardsFetchMarketingConsentDetailsRequest: BehaviouralRewardsFetchMarketingConsentDetailsRequest
    override val apiService = createXTMSService()
    override val service = "PersonalDetailsFacade"
    override val operation = "GetAndUpdateMarketingDetails"
    override var mockResponseFile = "express/behavioural_rewards/behavioural_rewards_get_updated_marketing_consent_details.json"

    suspend fun updateMarketingConsentDetails(behaviouralRewardsFetchMarketingConsentDetailsRequest: BehaviouralRewardsFetchMarketingConsentDetailsRequest): BehaviouralRewardsCurrentMarketingConsentDetailsResponse? {
        this.behaviouralRewardsFetchMarketingConsentDetailsRequest = behaviouralRewardsFetchMarketingConsentDetailsRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {

        return baseRequest
                .addParameter("option", behaviouralRewardsFetchMarketingConsentDetailsRequest.option)
                .addParameter("nonCreditIndicator", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditIndicator)
                .addParameter("nonCreditSMS", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditSMS)
                .addParameter("nonCreditEmail", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditEmail)
                .addParameter("nonCreditAVoice", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditAVoice)
                .addParameter("nonCreditTel", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditTel)
                .addParameter("nonCreditPost", behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditPost)
                .addParameter("creditIndicator", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditIndicator)
                .addParameter("creditSMS", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditSMS)
                .addParameter("creditEmail", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditEmail)
                .addParameter("creditAVoice", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditAVoice)
                .addParameter("creditTel", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditTel)
                .addParameter("creditPost", behaviouralRewardsFetchMarketingConsentDetailsRequest.creditPost)
                .build()
    }
}