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

package com.barclays.absa.banking.express.cashSend.cashSendResendWithdrawalPin

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.cashSendResendWithdrawalPin.dto.ResendPinRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.ApiService
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendResendWithdrawalPinRepository : Repository() {
    lateinit var resendPinRequest: ResendPinRequest

    override val apiService: ApiService = createXTMSService()

    override val service: String = "CashSendPaymentFacade"
    override val operation: String = "ResendCashSendWithdrawalNumber"

    override var mockResponseFile: String = "express/cashSend/resend_cash_send_withdrawal_pin_response.json"

    suspend fun resendCashSendPin(resendPinRequest: ResendPinRequest): BaseResponse? {
        this.resendPinRequest = resendPinRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest
            .addParameter("uniqueEFT", resendPinRequest.uniqueEFT)
            .addParameter("sourceAccount", resendPinRequest.sourceAccount)
            .addParameter("paymentNumber", resendPinRequest.paymentNumber)
            .build()
}