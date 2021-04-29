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
 *
 */
package com.barclays.absa.banking.express.accountBalances

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse
import kotlinx.coroutines.Dispatchers

class AccountBalancesViewModel : ExpressBaseViewModel() {
    override val repository by lazy { AccountBalancesRepository() }

    lateinit var accountBalanceLiveData: LiveData<AccountBalanceResponse>

    fun fetchAccountBalance(accountNumber: String, showProgressDialog: Boolean) {
        accountBalanceLiveData = liveData(Dispatchers.IO) {
            repository.showProgressDialog = showProgressDialog
            repository.getAccountBalance(accountNumber)?.let {
                emit(it)
            }
        }
    }

    fun isInitialised(): Boolean = ::accountBalanceLiveData.isInitialized
}