/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.lotto.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.lotto.services.dto.LottoGameRulesResponse

class LottoGameRulesRequest(responseListener: ExtendedResponseListener<LottoGameRulesResponse>) : ExtendedRequest<LottoGameRulesResponse>(responseListener) {

    init {
        params = RequestParams.Builder("OP2126")
                .build()
        mockResponseFile = "lotto/op2126_lotto_fetch_game_rules.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = LottoGameRulesResponse::class.java

    override fun isEncrypted() = true
}