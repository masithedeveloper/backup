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

package com.barclays.absa.banking.cashSendPlus.services

import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusService.Companion.OP2173_CHECK_CASH_SEND_PLUS_REGISTRATION_STATUS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class CheckCashSendPlusRegistrationStatusRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2173_CHECK_CASH_SEND_PLUS_REGISTRATION_STATUS).build()

        mockResponseFile = "cash_send_plus/op2173_check_registration_status_success.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CheckCashSendPlusRegistrationStatusResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}