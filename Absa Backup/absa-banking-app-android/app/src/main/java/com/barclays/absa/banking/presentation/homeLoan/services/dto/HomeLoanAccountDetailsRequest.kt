/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.presentation.homeLoan.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.presentation.homeLoan.services.HomeLoanService.Companion.OP2159_GET_HOME_LOAN_ACCOUNT_DETAILS

class HomeLoanAccountDetailsRequest<T>(accountNumber: String, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2159_GET_HOME_LOAN_ACCOUNT_DETAILS)
                .put("accountNumber", accountNumber)
                .build()
        mockResponseFile = "home_loan/op2159_get_home_loan_account_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = HomeLoanDetails::class.java as Class<T>
    override fun isEncrypted() = true
}