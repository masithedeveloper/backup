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
package com.barclays.absa.banking.express.cashSend.performCashSend

import com.barclays.absa.banking.express.cashSend.performCashSend.dto.CashSendPaymentResponse
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationDataModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.dto.CashSendValidationResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest

class CashSendPaymentRepository : SureCheckRepository() {

    override val service: String = "CashSendPaymentFacade"
    override val operation: String = "PerformSingleCashSendPayment"
    override var mockResponseFile = "express/cashSend/cash_send_perform_payment.json"
    override val apiService = createXTMSService()

    lateinit var cashSendValidationResponse: CashSendValidationResponse

    suspend fun performCashSendPayment(cashSendValidationResponse: CashSendValidationResponse, cashSendValidationDataModel: CashSendValidationDataModel): CashSendPaymentResponse? {
        with(cashSendValidationResponse) {
            this@CashSendPaymentRepository.cashSendValidationResponse = this
            additionalSureCheckParameters = hashMapOf(
                    "token" to cashSendPaymentToken,
                    "transactionAmount" to cashSendValidationDataModel.paymentAmount,
                    "beneficiaryAccountNumber" to cashSendValidationResponse.sourceAccount
            )
        }
        return submitRequest()
    }

    override var securityFunctionType = "performSingleCashSend_TVM"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest.addParameter("sourceAccount", cashSendValidationResponse.sourceAccount)
            .addParameter("cashSendPaymentToken", cashSendValidationResponse.cashSendPaymentToken)
            .build()
}