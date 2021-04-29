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
 */package com.barclays.absa.banking.moneyMarket.service

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class MoneyMarketWithdrawRequest(sourceAccountNumber: String, destinationAccountNumber: String, responseListener: ExtendedResponseListener<SureCheckResponse>) : ExtendedRequest<SureCheckResponse>(responseListener) {
    init {
        params = RequestParams.Builder(MoneyMarketInteractor.OP2203_WITHDRAW_ORBIT)
                .put("sourceAccountNumber", sourceAccountNumber)
                .put("destAccountNumber", destinationAccountNumber)
                .build()
        mockResponseFile = "money_market/op2203_withdraw_orbit.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = SureCheckResponse::class.java

    override fun isEncrypted() = true
}