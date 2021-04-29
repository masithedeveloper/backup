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

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.Companion.OP2060_RETRIEVE_BUY_MORE_UNITS_LINKED_ACCOUNTS

class BuyMoreUnitsLinkedAccountsRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder().put(OP2060_RETRIEVE_BUY_MORE_UNITS_LINKED_ACCOUNTS).build()
        mockResponseFile = "unit_trust/op2060_retrieve_linked_accounts_for_unit_trust.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BuyMoreUnitsLinkedAccountsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}