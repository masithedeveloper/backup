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
package com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin

import com.barclays.absa.banking.express.ExpressBaseHttpFormRepository
import com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin.dto.CashSendUpdateAtmPinRequest
import com.barclays.absa.banking.express.cashSend.cashSendUpdateATMPin.dto.UnredeemedCashSendTransactionsResponse
import com.barclays.absa.banking.framework.BuildConfigHelper
import za.co.absa.networking.BuildConfig
import za.co.absa.networking.ExpressHttpClient
import za.co.absa.networking.HttpFormRequest

class CashSendUpdateAtmPinRepository : ExpressBaseHttpFormRepository() {

    val service: String = "CashSendPaymentFacade"
    val operation: String = "CashSendChangeAccessCode"

    var mockResponseFile: String = "express/cashSend/update_atm_pin_response.json"

    fun updateAtmPin(updateAtmPinRequest: CashSendUpdateAtmPinRequest): UnredeemedCashSendTransactionsResponse? {
        val httpFormRequest = HttpFormRequest.Builder()
                .addParameter("cashSendType", updateAtmPinRequest.cashSendType)
                .addParameter("instructionType", updateAtmPinRequest.instructionType)
                .addParameter("beneficiaryName", updateAtmPinRequest.beneficiaryName)
                .addParameter("beneficiarySurname", updateAtmPinRequest.beneficiarySurname)
                .addParameter("beneficiaryShortName", updateAtmPinRequest.beneficiaryShortName)
                .addParameter("recipientCellNo", updateAtmPinRequest.recipientCellphoneNumber)
                .addParameter("statementReference", updateAtmPinRequest.statementReference)
                .addParameter("transactionDateAndTime", updateAtmPinRequest.transactionDateAndTime)
                .addParameter("sourceAccount", updateAtmPinRequest.sourceAccount)
                .addParameter("paymentAmount", updateAtmPinRequest.paymentAmount)
                .addParameter("cifKey", updateAtmPinRequest.cifKey)
                .addParameter("tiebreaker", updateAtmPinRequest.tieBreaker)
                .addParameter("beneficiaryNumber", updateAtmPinRequest.beneficiaryNumber)
                .addParameter("applicationId", updateAtmPinRequest.applicationId)
                .addParameter("PIN", updateAtmPinRequest.pin)
                .addParameter("uniqueEFT", updateAtmPinRequest.uniqueEFT)
                .addParameter("paymentNumber", updateAtmPinRequest.paymentNumber.toString())
                .build()

        httpClient = ExpressHttpClient("${BuildConfig.nCipherBaseUrl}$service$operation.exp", true)
        if (BuildConfigHelper.STUB) {
            httpClient.useStubs = true
            httpClient.stubResponseFile = mockResponseFile
        }
        return httpClient.performRequest(httpFormRequest, UnredeemedCashSendTransactionsResponse::class.java)
    }
}