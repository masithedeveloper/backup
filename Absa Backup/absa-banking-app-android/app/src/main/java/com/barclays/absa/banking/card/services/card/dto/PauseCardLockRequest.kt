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

import com.barclays.absa.banking.card.services.card.CardService.OP2020_UPDATE_PAUSE_CARD_STATES
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class PauseCardLockRequest<T>(pauseCardStates: PauseStates, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    companion object {
        private const val NO_LOCK = "N"
    }

    init {
        params = RequestParams.Builder(OP2020_UPDATE_PAUSE_CARD_STATES)
                .put(CardRequestParameters.SEND_RECEIVE_INDICATOR.key, "send")
                .put(CardRequestParameters.ALL_TRANSACTIONS.key, pauseCardStates.allTransactions ?: NO_LOCK)
                .put(CardRequestParameters.INTERNATIONAL_ATM_TRANSACTIONS.key, pauseCardStates.internationalAtmTransactions ?: NO_LOCK)
                .put(CardRequestParameters.INTERNATIONAL_POINT_OF_SALE_TRANSACTIONS.key, pauseCardStates.internationalPointOfSaleTransactions ?: NO_LOCK)
                .put(CardRequestParameters.LOCAL_ATM_TRANSACTIONS.key, pauseCardStates.localAtmTransactions ?: NO_LOCK)
                .put(CardRequestParameters.LOCAL_POINT_OF_SALE_TRANSACTIONS.key, pauseCardStates.localPointOfSaleTransactions ?: NO_LOCK)
                .put(CardRequestParameters.ONLINE_PURCHASE.key, pauseCardStates.onlinePurchases ?: NO_LOCK)
                .put(CardRequestParameters.CARD_NUMBER.key, pauseCardStates.cardNumber ?: NO_LOCK)
                .put(CardRequestParameters.DIGITAL_WALLET.key, pauseCardStates.digitalWallet ?: if ("P".equals(pauseCardStates.allTransactions, ignoreCase = true)) "Y" else NO_LOCK)
                .build()

        mockResponseFile = "card/op2020_update_pause_card_status.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = UpdatePauseCard::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}