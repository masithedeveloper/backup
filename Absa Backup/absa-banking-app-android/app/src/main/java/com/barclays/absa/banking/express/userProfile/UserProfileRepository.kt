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
package com.barclays.absa.banking.express.userProfile

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.hmac.service.BaseRequest

class UserProfileRepository : Repository() {

    override val apiService = createXTMSService()

    override val service = "UserProfileFacade"
    override val operation = "GetUserProfileDetails"

    override var mockResponseFile: String = getMockForUserProfileAccounts()

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.build()

    private fun getMockForUserProfileAccounts(): String {
        val choice = 0
        return when (choice) {
            0 -> "express/user_profile.json"
            else -> "express/user_profile_no_rewards.json"
        }
    }
}