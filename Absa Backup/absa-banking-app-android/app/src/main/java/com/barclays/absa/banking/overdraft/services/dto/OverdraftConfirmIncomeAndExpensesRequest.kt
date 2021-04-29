/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.overdraft.services.dto

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftIncomeAndExpensesConfirmationResponse
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.overdraft.services.OverdraftMockFactory
import com.barclays.absa.banking.overdraft.services.OverdraftService.*

class OverdraftConfirmIncomeAndExpensesRequest<T>(overdraftDetails: OverdraftQuoteDetailsObject, overdraftScoreResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(overdraftScoreResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2006_OVERDRAFT_QUOTE_DETAILS)
                .put(OVERDRAFT_ACCOUNT_NUMBER, overdraftDetails.accountNumber)
                .put(OVERDRAFT_TYPE, "Fixed")
                .put(TOTAL_MONTHLY_GROSS_INCOME, overdraftDetails.totalMonthlyGrossIncome)
                .put(TOTAL_MONTHLY_NET_INCOME, overdraftDetails.totalMonthlyNetIncome)
                .put(TOTAL_MONTHLY_LIVING_EXPENSES, overdraftDetails.totalMonthlyLivingExpenses)
                .put(CUSTOMER_BUREA_COMMITMENTS, overdraftDetails.customerBureauCommitments)
                .put(CUSTOMER_MAINTENANCE_EXPENSES, overdraftDetails.customerMaintenanceExpenses)
                .put(TOTAL_MONTHLY_DISABLE_INCOME, overdraftDetails.totalMonthlyDisposableIncome)
                .put(CHECK_AGREE_QUOTE, overdraftDetails.checkAgreeQuote)
                .build()

        mockResponseFile = OverdraftMockFactory.overdraftQuoteDetails()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = OverdraftIncomeAndExpensesConfirmationResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}