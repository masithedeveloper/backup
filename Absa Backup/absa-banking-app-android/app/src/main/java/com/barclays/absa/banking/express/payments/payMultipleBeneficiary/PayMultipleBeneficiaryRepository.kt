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
package com.barclays.absa.banking.express.payments.payMultipleBeneficiary

import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PayMultipleBeneficiaryRequest
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PayMultipleBeneficiaryResponse
import com.barclays.absa.banking.express.sureCheck.SureCheckRepository
import za.co.absa.networking.hmac.service.BaseRequest
import java.math.BigDecimal

class PayMultipleBeneficiaryRepository : SureCheckRepository() {
    private lateinit var payMultipleBeneficiaryRequestList: List<PayMultipleBeneficiaryRequest>

    override val apiService = createXTMSService()

    override val service = "RegularMultipleBeneficiaryPaymentFacade"
    override val operation = "PayMultipleBeneficiary"

    override var securityFunctionType: String = "performPayment_Multiple_TVM"

    override var mockResponseFile = "express/payments/payments_perform_multiple_payment_response.json"

    suspend fun payBeneficiaries(payMultipleBeneficiaryRequestList: List<PayMultipleBeneficiaryRequest>, totalPaymentAmount: BigDecimal): PayMultipleBeneficiaryResponse? {
        this.payMultipleBeneficiaryRequestList = payMultipleBeneficiaryRequestList
        additionalSureCheckParameters = HashMap<String, Any>().apply {
            put("transactionAmount", totalPaymentAmount.toString())
        }
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("noOfPayments", payMultipleBeneficiaryRequestList.size.toString())
            .addParameter("payMultiplePaymentRequestList", payMultipleBeneficiaryRequestList)
            .build()
}