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
 */
package com.barclays.absa.banking.express.limits.changeUserLimits

import com.barclays.absa.banking.express.limits.changeUserLimits.dto.ChangeLimitsRequest
import com.barclays.absa.banking.express.limits.changeUserLimits.dto.ChangeUserLimitsResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest

class ChangeUserLimitsRepository : SureCheckRepository() {

    private lateinit var changeLimitsRequest: ChangeLimitsRequest

    override val apiService = createXTMSService()
    override val service = "ManageLimitsFacade"
    override val operation = "ChangeUserLimits"

    override var securityFunctionType: String = "changeUserLimits_TVM"

    override lateinit var additionalSureCheckParameters: HashMap<String, Any>

    override var mockResponseFile = "express/limits/change_user_limits_response.json"

    suspend fun changeUserLimits(changeLimitsRequest: ChangeLimitsRequest): ChangeUserLimitsResponse? {
        this.changeLimitsRequest = changeLimitsRequest
        additionalSureCheckParameters = HashMap<String, Any>().apply {
            put("newPaymentLimit", changeLimitsRequest.paymentLimit)
            put("newInterAccountTransferLimit", changeLimitsRequest.interAccountTransferLimit)
            put("newFutureDatedPaymentLimit", changeLimitsRequest.futureDatedPaymentLimit)
            put("newRecurringPaymentLimit", changeLimitsRequest.recurringPaymentLimit)
        }
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("paymentLimit", changeLimitsRequest.paymentLimit)
            .addParameter("interAccountTransferLimit", changeLimitsRequest.interAccountTransferLimit)
            .addParameter("futureDatedPaymentLimit", changeLimitsRequest.futureDatedPaymentLimit)
            .addParameter("recurringPaymentLimit", changeLimitsRequest.recurringPaymentLimit)
            .build()
}