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
package com.barclays.absa.banking.card.services.card.dto.creditCard

import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0846_DISPLAY_OVERDRAFT_SNOOZE
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class OverdraftOfferRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0846_DISPLAY_OVERDRAFT_SNOOZE)
                .put(Transaction.IS_CALL_FROM_MOBILE, "true")
                .put(Transaction.DECOUPLE_CHANNEL_IND, "S")
                .build()

        mockResponseFile = "vcl/op0846_display_overdraft_service.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditCardOverdraft::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}