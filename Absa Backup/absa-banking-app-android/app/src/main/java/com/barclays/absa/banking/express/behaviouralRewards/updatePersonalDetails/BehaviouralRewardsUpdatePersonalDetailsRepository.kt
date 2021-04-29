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
package com.barclays.absa.banking.express.behaviouralRewards.updatePersonalDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.behaviouralRewards.updatePersonalDetails.dto.UpdatePersonalDetails
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsUpdatePersonalDetailsRepository : Repository() {
    private lateinit var updatePersonalDetails: UpdatePersonalDetails

    override val apiService = createXTMSService()
    override val service = "PersonalDetailsFacade"
    override val operation = "UpdatePersonalDetails"

    override var mockResponseFile: String = "express/behavioural_rewards/behavioural_rewards_update_personal_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.enablePagination()
                .addObjectParameter("personalDetails", updatePersonalDetails)
                .build()
    }

    suspend fun updatePersonalDetails(updatePersonalDetails: UpdatePersonalDetails): BaseResponse? {
        this.updatePersonalDetails = updatePersonalDetails
        return submitRequest()
    }
}