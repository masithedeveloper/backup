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

package com.barclays.absa.banking.personalLoan.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.personalLoan.services.PersonalLoansService.Companion.OP2133_GET_PERSONAL_LOAN_INFORMATION
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubInformation

class PersonalLoanInformationRequest<T>(val accountNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2133_GET_PERSONAL_LOAN_INFORMATION).put("accountNumber", accountNumber).build()
        mockResponseFile = "personal_loan/op2133_personal_loan_information.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = PersonalLoanHubInformation::class.java as Class<T>

    override fun isEncrypted() = true
}