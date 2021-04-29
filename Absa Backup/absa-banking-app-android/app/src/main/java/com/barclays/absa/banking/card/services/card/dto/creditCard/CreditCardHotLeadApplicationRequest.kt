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

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.card.services.card.CardMockFactory
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP2135_APPLY_FOR_CREDIT_CARD
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.shared.BaseModel

class CreditCardHotLeadApplicationRequest<T>(cellphoneNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder(OP2135_APPLY_FOR_CREDIT_CARD)
                .put("cellNumber", cellphoneNumber)
                .build()

        mockResponseFile = CardMockFactory.applyForHotLead()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = CreditCardHotLeadApplicationResponse::class.java as Class<T>

    override fun isEncrypted() = true
}

class CreditCardHotLeadApplicationResponse : TransactionResponse() {
    var applyForCreditCardDetails: CreditCardHotLeadApplicationDetails = CreditCardHotLeadApplicationDetails()
}

class CreditCardHotLeadApplicationDetails : BaseModel {
    var responseCode: String = ""
}