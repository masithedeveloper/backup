/*
 *  Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.accountHub

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Transaction

class AccountHubViewModel : ViewModel() {
    val activateSearchView = MutableLiveData<Boolean>()
    val showSearchAndFilterView = MutableLiveData<Boolean>()
    val searchKeyword = MutableLiveData<String>()
    val shouldReloadHub = MutableLiveData<Boolean>()

    var allTransactions = ArrayList<Transaction>()
    var isSearchActive: Boolean = false
    var appBarStateExpanded: Boolean = true
    var initialTransactionHistoryDateRange: Int = -7
    var accountObject: AccountObject = AccountObject()

    fun filterMoneyIn(): List<Transaction> = allTransactions.filter { transaction -> transaction.creditAmount?.getAmount() != "0.00" && !transaction.isUnclearedTransaction }

    fun filterMoneyOut(): List<Transaction> = allTransactions.filter { transaction -> transaction.debitAmount?.getAmount() != "0.00" && !transaction.isUnclearedTransaction }

    fun filterMoneyUncleared(): List<Transaction> = allTransactions.filter { transaction -> transaction.isUnclearedTransaction }
}