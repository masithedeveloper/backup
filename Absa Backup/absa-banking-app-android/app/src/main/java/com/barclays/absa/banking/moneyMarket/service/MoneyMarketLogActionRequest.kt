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
package com.barclays.absa.banking.moneyMarket.service

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketInteractor.Companion.OP2202_LOG_ORBIT

class MoneyMarketLogActionRequest(action: String, accountNumber: String, responseListener: ExtendedResponseListener<ResponseObject>) : ExtendedRequest<ResponseObject>(responseListener) {
    init {
        params = RequestParams.Builder(OP2202_LOG_ORBIT)
                .put("accountNumber", accountNumber)
                .put("action", action)
                .build()
        mockResponseFile = "money_market/op2202_log_orbit.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = ResponseObject::class.java

    override fun isEncrypted() = true
}
