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

package com.barclays.absa.banking.express.behaviouralRewards.shareVoucher

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.behaviouralRewards.shareVoucher.dto.BehaviouralRewardsShareVoucher
import styleguide.utils.extensions.removeSpaces
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class BehaviouralRewardsShareVoucherRepository : Repository() {
    private lateinit var shareVoucher: BehaviouralRewardsShareVoucher

    override val apiService = createXTMSService()
    override val service = "BehaviouralRewardsVouchersFacade"
    override val operation = "ShareVoucher"

    override var mockResponseFile = "express/behavioural_rewards/behavioural_rewards_share_voucher.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest.enablePagination()
                .addParameter("voucherPin", shareVoucher.voucherPin)
                .addParameter("partnerId", shareVoucher.partnerId)
                .addParameter("cellNumber", shareVoucher.cellNumber.removeSpaces())
                .addParameter("shareMethod", shareVoucher.shareMethod)
                .build()
    }

    suspend fun shareVoucher(shareVoucher: BehaviouralRewardsShareVoucher): BaseResponse? {
        this.shareVoucher = shareVoucher
        return submitRequest()
    }
}