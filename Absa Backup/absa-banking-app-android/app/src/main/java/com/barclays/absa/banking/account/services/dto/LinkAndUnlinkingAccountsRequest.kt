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
package com.barclays.absa.banking.account.services.dto

import com.barclays.absa.banking.account.services.AccountRequestParameters
import com.barclays.absa.banking.account.services.AccountService.OP2075_LINK_UNLINK_ACCOUNTS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class LinkAndUnlinkingAccountsRequest<T>(accountOrderChanges: Boolean, accountLinkStatesChanges: Boolean,
                                         accounts: String, extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2075_LINK_UNLINK_ACCOUNTS)
                .put(AccountRequestParameters.HAS_ACCOUNT_LINKING_STATES_CHANGED.key, accountLinkStatesChanges.toString())
                .put(AccountRequestParameters.HAS_ORDER_CHANGED.key, accountOrderChanges.toString())
                .put(AccountRequestParameters.ACCOUNTS.key, accounts)
                .build()
        mockResponseFile = "account/op2075_link_unlinked_accounts.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ManageAccountsResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = false
}