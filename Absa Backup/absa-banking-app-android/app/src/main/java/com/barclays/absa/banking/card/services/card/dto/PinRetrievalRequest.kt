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
package com.barclays.absa.banking.card.services.card.dto

import com.barclays.absa.banking.boundary.model.CardPin
import com.barclays.absa.banking.card.services.card.dto.CreditCardService.OP0124_RETRIEVE_PIN
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants

class PinRetrievalRequest<T>(cardNumber: String, cardIndex: String, pinRetrievalResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(BuildConfigHelper.pinRetrievalServerPath, pinRetrievalResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0124_RETRIEVE_PIN)
                .put(CreditCardRequestParameters.CARD_INDEX.key, cardIndex)
                .put(CreditCardRequestParameters.CARD_NUMBER.key, cardNumber)
                .put(BMBConstants.IVALUE, BMBApplication.getInstance().iVal)
                .build()

        mockResponseFile = "card/op0124_card_pin_retrieval.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CardPin::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}