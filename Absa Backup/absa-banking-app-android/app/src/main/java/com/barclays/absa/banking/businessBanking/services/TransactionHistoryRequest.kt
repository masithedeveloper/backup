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
package com.barclays.absa.banking.businessBanking.services

import com.barclays.absa.banking.boundary.model.AccountDetail
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

import com.barclays.absa.banking.home.services.HomeScreenService.OP1301_ACCOUNT_DETAILS

class TransactionHistoryRequest<T>(account: AccountObject, isUnclearedTransactions: Boolean,
                                   fromDate: String, toDate: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP1301_ACCOUNT_DETAILS)
                .put(Transaction.REDEMPTION_ACCOUNT_NO, account.accountNumber)
                .put(Transaction.SERVICE_ACCOUNT_TYPE, account.accountType)
                .put(Transaction.SERVICE_FRM_DT, fromDate)
                .put(Transaction.SERVICE_TO_DT, toDate)
                .put(Transaction.SERVICE_SORT_PARAM, BMBConstants.SORT_ON_DATE)
                .put(Transaction.SERVICE_SORT_ORDER, BMBConstants.SORT_IN_DESC)
                .put(Transaction.ACCOUNT_DESCRIPTION, account.description)
                .put(Transaction.IS_BALANCED_MASKED, "false")
                .put(Transaction.UNCLEARED_EFFECTS_ENABLED_FLAG, if (isUnclearedTransactions) "true" else "false")
                .build()

        mockResponseFile = MockFactory.accountDetails()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AccountDetail::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}