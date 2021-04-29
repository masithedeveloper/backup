/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.services.card.dto

import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP2052_CREDIT_CARD_HUB
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

internal class CreditCardHubRequest<T>(cardNumber: String, fromDate: String?, toDate: String?,
                                       responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2052_CREDIT_CARD_HUB)
                .put(CreditCardRequestParameters.CARD_NUMBER.key, cardNumber)
                .put(CreditCardRequestParameters.FROM_DATE.key, fromDate)
                .put(CreditCardRequestParameters.TO_DATE.key, toDate)
                .build()

        printRequest()
        mockResponseFile = "card/op2052_credit_card_hub.json"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditCardResponseObject::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}