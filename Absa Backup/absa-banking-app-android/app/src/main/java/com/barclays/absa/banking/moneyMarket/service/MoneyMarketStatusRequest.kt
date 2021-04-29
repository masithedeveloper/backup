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
import com.barclays.absa.banking.moneyMarket.service.MoneyMarketInteractor.Companion.OP2199_GET_ORBIT_STATUS
import com.barclays.absa.banking.moneyMarket.service.dto.MoneyMarketStatus

class MoneyMarketStatusRequest(responseListener: ExtendedResponseListener<MoneyMarketStatus>) : ExtendedRequest<MoneyMarketStatus>(responseListener) {
    init {
        params = RequestParams.Builder(OP2199_GET_ORBIT_STATUS)
                .build()
        mockResponseFile = "money_market/op2199_get_orbit_status.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = MoneyMarketStatus::class.java

    override fun isEncrypted() = true
}