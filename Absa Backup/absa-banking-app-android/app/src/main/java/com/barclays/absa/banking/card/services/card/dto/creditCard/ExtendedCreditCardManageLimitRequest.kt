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

import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0121_CARD_FETCH_CHANGE_LIMIT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class ExtendedCreditCardManageLimitRequest<T>(creditCard: CreditCard,
                                              responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0121_CARD_FETCH_CHANGE_LIMIT)
                .put(Transaction.CARD_CHANGE_LIMIT_CARD_NUMBER, creditCard.accountNo)
                .put(Transaction.CARD_CHANGE_LIMIT_CARD_TYPE, "Credit")
                .build()

        mockResponseFile = "op0121_card_limit_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ManageCardLimitDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}