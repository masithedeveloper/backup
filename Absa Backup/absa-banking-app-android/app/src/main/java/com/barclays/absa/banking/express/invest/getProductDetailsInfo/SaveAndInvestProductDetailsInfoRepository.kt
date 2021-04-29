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

package com.barclays.absa.banking.express.invest.getProductDetailsInfo

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.getProductDetailsInfo.dto.ProductDetailsInfoResponse
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestProductType
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class SaveAndInvestProductDetailsInfoRepository : Repository() {
    private lateinit var saveAndInvestProductType: SaveAndInvestProductType

    override val apiService: ApiService = createXSMSService()
    override val service: String = "SaveAndInvestProductsFacade"
    override val operation: String = "GetProductDetailsInfo"
    override var mockResponseFile: String = "express/invest/depositor_plus_product_details_info.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest
            .addParameter("productCode", saveAndInvestProductType.productCode)
            .addParameter("crpCode", saveAndInvestProductType.creditRatePlanCode)
            .build()

    suspend fun fetchProductDetailsInfo(saveAndInvestProductType: SaveAndInvestProductType): ProductDetailsInfoResponse? {
        this.saveAndInvestProductType = saveAndInvestProductType
        setMockFileForProduct()
        return submitRequest()
    }

    private fun setMockFileForProduct() {
        mockResponseFile = when (saveAndInvestProductType) {
            SaveAndInvestProductType.FUTURE_PLAN -> "express/invest/future_plan_product_details_info.json"
            else -> "express/invest/depositor_plus_product_details_info.json"
        }
    }
}