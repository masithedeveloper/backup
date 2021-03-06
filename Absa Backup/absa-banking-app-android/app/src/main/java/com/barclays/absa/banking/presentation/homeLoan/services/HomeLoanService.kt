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

package com.barclays.absa.banking.presentation.homeLoan.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.homeLoan.services.dto.HomeLoanDetails

interface HomeLoanService {
    companion object {
        const val OP2159_GET_HOME_LOAN_ACCOUNT_DETAILS = "OP2159"
    }

    fun fetchHomeLoanAccountDetails(accountNumber: String, extendedResponseListener: ExtendedResponseListener<HomeLoanDetails>)
}