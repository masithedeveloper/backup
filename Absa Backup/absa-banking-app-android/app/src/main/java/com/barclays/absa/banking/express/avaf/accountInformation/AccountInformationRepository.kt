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

package com.barclays.absa.banking.express.avaf.accountInformation

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.avaf.accountInformation.dto.AbsaVehicleAndAssetFinanceDetailResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class AccountInformationRepository : Repository() {
    private lateinit var accountNumber: String

    override var mockResponseFile: String = "express/avaf/account_information_success_response.json"
    override val service: String = "AVAFManagementFacade"
    override val operation: String = "GetAVAFAccountDetail"
    override val apiService: ApiService = createXTMSService()

    override var apiPollingMaxRetries: Int = 10
    override var apiRetryDelayMillis = 2000L

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        return baseRequest
                .addParameter("accountNumber", accountNumber)
                .build()
    }

    suspend fun fetchAccountInformation(userAccount: String): AbsaVehicleAndAssetFinanceDetailResponse? {
        accountNumber = userAccount

        return submitRequest()
    }
}