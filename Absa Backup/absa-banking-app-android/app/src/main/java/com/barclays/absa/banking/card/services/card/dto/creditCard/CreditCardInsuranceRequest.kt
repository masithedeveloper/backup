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

import com.barclays.absa.banking.boundary.model.creditCardInsurance.CreditCardInsurance
import com.barclays.absa.banking.card.services.card.CardMockFactory
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0863_VIEW_POLICY_DETAILS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class CreditCardInsuranceRequest<T>(creditCard: CreditCard, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0863_VIEW_POLICY_DETAILS)
                .put(Transaction.ACCOUNT_NO, creditCard.accountNo)
                .put(Transaction.ACCOUNT_TYPE, "creditCard")
                .build()

        mockResponseFile = CardMockFactory.creditCardInsurancePolicy()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreditCardInsurance::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}