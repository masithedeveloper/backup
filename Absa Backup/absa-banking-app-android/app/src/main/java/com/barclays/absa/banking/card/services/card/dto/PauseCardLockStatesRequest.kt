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

import com.barclays.absa.banking.card.services.card.CardService.OP2019_ENQUIRE_PAUSE_CARD_STATES
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class PauseCardLockStatesRequest<T>(cardNumber: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2019_ENQUIRE_PAUSE_CARD_STATES)
                .put(CardRequestParameters.CARD_NUMBER.key, cardNumber.replace(" ", ""))
                .build()

        mockResponseFile = "card/op2019_enquire_pause_card.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = EnquirePauseState::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}