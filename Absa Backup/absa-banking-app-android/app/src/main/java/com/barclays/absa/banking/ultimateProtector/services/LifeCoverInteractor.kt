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

package com.barclays.absa.banking.ultimateProtector.services

import com.barclays.absa.banking.card.services.card.dto.creditCard.RetailAccountRequest
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.ultimateProtector.services.dto.*

class LifeCoverInteractor : AbstractInteractor(), LifeCoverService {

    override fun fetchLifeCoverQuotation(benefitCode: String, quotationExtendedResponseListener: ExtendedResponseListener<Quotation>) {
        val quotationRequest = QuotationRequest(benefitCode, quotationExtendedResponseListener)
        submitRequest(quotationRequest)
    }

    override fun fetchRetailAccount(retailAccountsExtendedResponseListener: ExtendedResponseListener<RetailAccountsResponse>) {
        val accountsToDebit = "savingsAccount|currentAccount|creditCard|chequeAccount"
        val retailAccountsRequest = RetailAccountRequest(retailAccountsExtendedResponseListener, accountsToDebit)
        submitRequest(retailAccountsRequest)
    }

    override fun applyForLifeCover(lifeCoverInfo: LifeCoverInfo, lifeCoverApplicationExtendedResponseListener: ExtendedResponseListener<LifeCoverApplicationResult>, callType: CallType) {
        val lifeCoverApplicationRequest = LifeCoverApplicationRequest(lifeCoverInfo, lifeCoverApplicationExtendedResponseListener, callType)
        submitRequest(lifeCoverApplicationRequest)
    }
}
