/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.invest.getProductInterestRate

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.ProductProfileInterestRateResponse
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.ProductInterestRateRequest
import za.co.absa.networking.hmac.service.BaseRequest

class ProductProfileInterestRateRepository(private val productInterestRatesRequest: ProductInterestRateRequest) : Repository() {

    override val apiService = createXSMSService()
    override val service = "SaveAndInvestInterestRateFacade"
    override val operation = "GetProductProfileInterestRateDetails"
    override var mockResponseFile: String = "express/invest/get_future_plan_interest_rates.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addParameter("productCode", productInterestRatesRequest.productCode)
            .addParameter("crpCode", productInterestRatesRequest.creditRatePlan)
            .build()

    suspend fun fetchProductProfileInterestRates(): ProductProfileInterestRateResponse? {
        return submitRequest()
    }
}