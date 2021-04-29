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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0885_VALIDATE_FOR_HOLIDAYS_AND_TIME
import com.barclays.absa.utils.DeviceUtils

class ValidateForHolidaysAndTimeRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0885_VALIDATE_FOR_HOLIDAYS_AND_TIME)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .build()

        printRequest()
        mockResponseFile = "international_payments/op0885_validate_holidays_and_time.json"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = ValidateForHolidaysAndTimeResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}