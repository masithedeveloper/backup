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

package com.barclays.absa.banking.express.behaviouralRewards.fetchAvailableVouchers

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.behaviouralRewards.fetchAvailableVouchers.dto.AvailableVouchersResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsFetchAvailableVouchersRepository : Repository() {

    override val apiService = createXTMSService()
    override val service = "BehaviouralRewardsVouchersFacade"
    override val operation = "GetAvailableVouchers"

    override var mockResponseFile = "express/behavioural_rewards/behavioural_rewards_available_vouchers.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.build()

    suspend fun fetchAvailableVouchers(): AvailableVouchersResponse? = submitRequest()

}