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
package com.barclays.absa.banking.express.payments.validateMultiplePayment

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.payments.validateMultiplePayment.dto.ValidateMultiplePaymentRequest
import com.barclays.absa.banking.express.payments.validateMultiplePayment.dto.ValidateMultiplePaymentResponse
import za.co.absa.networking.hmac.service.BaseRequest

class ValidateMultiplePaymentRepository : Repository() {

    private lateinit var validateMultiplePaymentRequestList: List<ValidateMultiplePaymentRequest>

    override val apiService = createXTMSService()
    override val service = "RegularMultipleBeneficiaryPaymentFacade"
    override val operation = "ValidateMultiplePayment"

    override var mockResponseFile = "express/payments/payments_validate_multiple_payment_response.json"

    suspend fun validatePayments(multiplePaymentRequest: List<ValidateMultiplePaymentRequest>): ValidateMultiplePaymentResponse? {
        this.validateMultiplePaymentRequestList = multiplePaymentRequest
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("noOfPayments", validateMultiplePaymentRequestList.size.toString())
            .addParameter("validateMultiplePaymentRequestList", validateMultiplePaymentRequestList)
            .build()
}