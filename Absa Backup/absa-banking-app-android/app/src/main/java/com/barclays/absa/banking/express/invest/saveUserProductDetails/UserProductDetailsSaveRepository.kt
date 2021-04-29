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

package com.barclays.absa.banking.express.invest.saveUserProductDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.invest.saveUserProductDetails.dto.UserProductDetailsSaveRequest
import com.barclays.absa.banking.express.invest.saveUserProductDetails.dto.UserProductDetailsSaveResponse
import za.co.absa.networking.hmac.service.BaseRequest

class UserProductDetailsSaveRepository : Repository() {
    private lateinit var userProductDetailsSaveRequest: UserProductDetailsSaveRequest

    override val apiService = createXSMSService()
    override val service = "AuthenticationFacade"
    override val operation = "SaveUserProductDetails"
    override var mockResponseFile: String = "express/invest/save_user_product_details.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        with(userProductDetailsSaveRequest) {
            baseRequest.addParameter("productCode", productCode)
                .addParameter("pricingCode", pricingCode)
                .addParameter("productType", productType)
                .addParameter("productName", productName)
                .addParameter("crpCode", creditRatePlanCode).build()
        }

    suspend fun saveUserProductDetails(userProductDetailsSaveRequest: UserProductDetailsSaveRequest): UserProductDetailsSaveResponse? {
        this.userProductDetailsSaveRequest = userProductDetailsSaveRequest
        return submitRequest()
    }
}