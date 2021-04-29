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

package com.barclays.absa.banking.unitTrusts.ui.buy

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.services.dto.LinkedAccountsWrapper

class LinkedAccountsExtendedResponseListener(private val buyUnitTrustViewModel: BuyUnitTrustViewModel) : ExtendedResponseListener<LinkedAccountsWrapper>() {
    override fun onSuccess(linkedAccountsWrapper: LinkedAccountsWrapper) {

        val filteredAccounts = linkedAccountsWrapper.accounts.filter { linkedAccount ->
            var accountTypeToCheck = ""
            linkedAccount.accountType?.let { accountTypeToCheck = it }
            !accountTypeToCheck.contains("absareward", true)
        }

        buyUnitTrustViewModel.linkedAccountsLiveData.value = filteredAccounts
    }
}