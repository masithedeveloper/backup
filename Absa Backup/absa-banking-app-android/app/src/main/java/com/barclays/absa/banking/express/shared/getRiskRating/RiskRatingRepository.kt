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

package com.barclays.absa.banking.express.shared.getRiskRating

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.getRiskRating.dto.RiskRatingRequest
import com.barclays.absa.banking.express.shared.getRiskRating.dto.RiskRatingResponse
import za.co.absa.networking.hmac.service.BaseRequest

class RiskRatingRepository : Repository() {
    private lateinit var riskRatingRequest: RiskRatingRequest

    override val apiService = createXSMSService()
    override val service = "CustomerScreeningFacade"
    override val operation = "GetRiskRatingDetails"
    override var mockResponseFile: String = "express/invest/get_risk_rating.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addObjectParameter("riskRatingInputVO", riskRatingRequest)
            .build()

    suspend fun fetchRiskRating(riskRatingRequest: RiskRatingRequest): RiskRatingResponse? {
        this.riskRatingRequest = riskRatingRequest
        return submitRequest()
    }
}