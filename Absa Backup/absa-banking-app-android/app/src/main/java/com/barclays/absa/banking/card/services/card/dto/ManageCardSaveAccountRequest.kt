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

import com.barclays.absa.banking.boundary.model.ManageCardSaveAccount
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0123_CARD_CHANGE_LIMITS_RESULT
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class ManageCardSaveAccountRequest<T>(reference: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0123_CARD_CHANGE_LIMITS_RESULT)
                .put(Transaction.CARD_CONFIRM_TXN_REF_ID, reference)
                .build()

        mockResponseFile = "manage_cards/op123_card_change_limits_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ManageCardSaveAccount::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}