/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralService.Companion.OP2142_FETCH_VALIDATION_RULES
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class FlexiFuneralValidationRulesRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder(OP2142_FETCH_VALIDATION_RULES).build()
        mockResponseFile = "flexi_funeral/op2142_flexi_funeral_validation_rules.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = FlexiFuneralValidationRulesResponse::class.java as Class<T>

    override fun isEncrypted() = true
}