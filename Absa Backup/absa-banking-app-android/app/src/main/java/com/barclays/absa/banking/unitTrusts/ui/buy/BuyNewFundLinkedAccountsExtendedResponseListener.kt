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
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsLinkedAccountsResponse
import com.barclays.absa.banking.unitTrusts.services.dto.LinkedAccount

class BuyNewFundLinkedAccountsExtendedResponseListener(private val viewModel: BuyUnitTrustViewModel) : ExtendedResponseListener<BuyMoreUnitsLinkedAccountsResponse>() {
    override fun onSuccess(successResponse: BuyMoreUnitsLinkedAccountsResponse) {
        val buyMoreUnitsLinkedAccounts = successResponse.buyMoreUnitsLinkedAccounts.accounts
        val linkedAccounts = mutableListOf<LinkedAccount>()
        buyMoreUnitsLinkedAccounts?.mapTo(linkedAccounts, transform = {
            val account = LinkedAccount()
            account.accountHolder = it.accountHolderName
            account.accountNumber = it.accountNumber
            account.accountType = it.accountType
            account.bankName = it.bankName
            account.branchCode = it.branchCode
            account.description = it.desc
            account
        })

        val filteredAccounts = linkedAccounts.filter { linkedAccount ->
            var accountTypeToCheck = ""
            linkedAccount.accountType?.let { accountTypeToCheck = it }
            !accountTypeToCheck.contains("absareward", true)
        }

        viewModel.linkedAccountsLiveData.value = filteredAccounts
    }
}