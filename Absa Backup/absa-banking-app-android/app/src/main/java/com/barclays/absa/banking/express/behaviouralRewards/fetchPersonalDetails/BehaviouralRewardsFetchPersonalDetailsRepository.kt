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
package com.barclays.absa.banking.express.behaviouralRewards.fetchPersonalDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.behaviouralRewards.fetchPersonalDetails.dto.PersonalDetailsResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsFetchPersonalDetailsRepository : Repository() {

    override val apiService = createXTMSService()
    override val service = "PersonalDetailsFacade"
    override val operation = "GetPersonalDetailsForCustomer"

    override var mockResponseFile = "express/behavioural_rewards/behavioural_rewards_personal_details_for_customer.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.build()

    suspend fun fetchPersonalDetails(): PersonalDetailsResponse? = submitRequest()
}