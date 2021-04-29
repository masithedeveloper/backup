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
package com.barclays.absa.banking.express.cashSend.validateCashSend

import com.barclays.absa.banking.express.ExpressBaseHttpFormRepository
import com.barclays.absa.banking.express.cashSend.validateCashSend.dto.CashSendValidationResponse
import com.barclays.absa.banking.framework.BuildConfigHelper
import za.co.absa.networking.BuildConfig
import za.co.absa.networking.ExpressHttpClient
import za.co.absa.networking.HttpFormRequest

class CashSendValidationRepository : ExpressBaseHttpFormRepository() {

    val service: String = "CashSendPaymentFacade"
    val operation: String = "ValidateCashSend"

    var mockResponseFile: String = "express/cashSend/validate_cash_send_response.json"

    suspend fun validateCashSend(cashSendValidationData: CashSendValidationDataModel): CashSendValidationResponse? {
        val httpFormRequest = HttpFormRequest.Builder()
                .addParameter("cashSendType", cashSendValidationData.cashSendType)
                .addParameter("instructionType", cashSendValidationData.instructionType)
                .addParameter("beneficiaryName", cashSendValidationData.beneficiaryName)
                .addParameter("beneficiarySurname", cashSendValidationData.beneficiarySurname)
                .addParameter("beneficiaryShortName", cashSendValidationData.beneficiaryShortName)
                .addParameter("recipientCellNo", cashSendValidationData.recipientCellphoneNumber)
                .addParameter("statementReference", cashSendValidationData.statementReference)
                .addParameter("transactionDateTime", cashSendValidationData.transactionDateTime)
                .addParameter("sourceAccount", cashSendValidationData.sourceAccount)
                .addParameter("paymentAmount", cashSendValidationData.paymentAmount)
                .addParameter("cifKey", cashSendValidationData.cifKey)
                .addParameter("tieBreaker", cashSendValidationData.tieBreaker)
                .addParameter("beneficiaryNumber", cashSendValidationData.beneficiaryNumber)
                .addParameter("applicationId", cashSendValidationData.applicationId)
                .addParameter("PIN", cashSendValidationData.pin)
                .addParameter("serviceOperation", "$service$operation")
                .build()

        httpClient = ExpressHttpClient("${BuildConfig.nCipherBaseUrl}CashSendPaymentFacadeValidateCashSend.exp", true)
        if (BuildConfigHelper.STUB) {
            httpClient.useStubs = true
            httpClient.stubResponseFile = mockResponseFile
        }
        val response = httpClient.performRequest(httpFormRequest, CashSendValidationResponse::class.java)
        return handleResponse(response)
    }

}