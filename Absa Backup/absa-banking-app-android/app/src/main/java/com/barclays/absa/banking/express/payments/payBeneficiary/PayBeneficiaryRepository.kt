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
package com.barclays.absa.banking.express.payments.payBeneficiary

import com.barclays.absa.banking.express.payments.payBeneficiary.dto.PayBeneficiaryRequest
import com.barclays.absa.banking.express.payments.payBeneficiary.dto.PayBeneficiaryResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest

open class PayBeneficiaryRepository : SureCheckRepository() {

    private lateinit var payBeneficiaryRequest: PayBeneficiaryRequest

    override val apiService = createXTMSService()
    override val service = "RegularBeneficiaryPaymentFacade"
    override val operation = "PayBeneficiary"

    override var securityFunctionType: String = "performPayment_TVM"

    override lateinit var additionalSureCheckParameters: HashMap<String, Any>

    override var mockResponseFile = "express/payments/payments_perform_payment_response.json"

    override var allowRetries: Boolean = false

    suspend fun payBeneficiary(payBeneficiaryRequest: PayBeneficiaryRequest): PayBeneficiaryResponse? {
        this.payBeneficiaryRequest = payBeneficiaryRequest
        additionalSureCheckParameters = HashMap<String, Any>().apply {
            put("beneficiaryAccountNumber", payBeneficiaryRequest.targetAccountNumber)
            put("transactionAmount", payBeneficiaryRequest.transactionAmount)
            put("token", payBeneficiaryRequest.paymentToken)
        }
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("targetAccountNumber", payBeneficiaryRequest.targetAccountNumber)
            .addParameter("paymentToken", payBeneficiaryRequest.paymentToken)
            .build()
}