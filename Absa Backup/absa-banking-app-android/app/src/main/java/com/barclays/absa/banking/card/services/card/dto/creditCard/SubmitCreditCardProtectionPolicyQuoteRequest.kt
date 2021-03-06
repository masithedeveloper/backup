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

import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditProtectionQuote
import com.barclays.absa.banking.card.services.card.CardMockFactory
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0865_APPLY_CREDIT_CARD_PROTECTION_INSURANCE
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class SubmitCreditCardProtectionPolicyQuoteRequest<T>(creditCardNumber: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0865_APPLY_CREDIT_CARD_PROTECTION_INSURANCE)
                .put(Transaction.ACCOUNT_NO, creditCardNumber)
                .build()

        mockResponseFile = CardMockFactory.submitCreditProtection()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditProtectionQuote::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}