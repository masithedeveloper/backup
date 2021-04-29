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

package com.barclays.absa.banking.express.cashSend.cancelCashSendPayment

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.cashSend.cancelCashSendPayment.dto.CancelCashSendPaymentRequest
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class CancelCashSendPaymentRepository : Repository() {
    private lateinit var cancelCashSendPaymentRequest: CancelCashSendPaymentRequest

    override val apiService = createXTMSService()

    override val service = "CashSendPaymentFacade"
    override val operation = "CancelCashSendPayment"

    override var mockResponseFile = "express/base_response_success.json"

    suspend fun cancelCashSendPayment(cancelCashSendPaymentRequest: CancelCashSendPaymentRequest): BaseResponse? {
        this.cancelCashSendPaymentRequest = cancelCashSendPaymentRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest = baseRequest
            .addParameter("uniqueEFT", cancelCashSendPaymentRequest.uniqueEFT)
            .addParameter("paymentNumber", cancelCashSendPaymentRequest.paymentNumber.toString())
            .addParameter("sourceAccount", cancelCashSendPaymentRequest.sourceAccount)
            .addParameter("paymentAmount", cancelCashSendPaymentRequest.paymentAmount)
            .addParameter("recipientCellNo", cancelCashSendPaymentRequest.recipientCellphoneNumber)
            .build()
}