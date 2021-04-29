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

package com.barclays.absa.banking.express.transactionHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest
import com.barclays.absa.banking.express.transactionHistory.dto.TransactionHistoryResponse
import com.barclays.absa.banking.home.ui.AccountTypes
import kotlinx.coroutines.Dispatchers

class TransactionHistoryViewModel : ExpressBaseViewModel() {

    override val repository by lazy { TransactionHistoryRepository() }

    lateinit var transactionHistoryLiveData: LiveData<TransactionHistoryResponse>

    var filteringOptions: FilteringOptions = FilteringOptions()

    fun fetchTransactionHistory(historyRequest: HistoryRequest) {
        transactionHistoryLiveData = if (historyRequest.accountType == AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE) {
            liveData(Dispatchers.IO) { repository.fetchAvafTransactionHistory(historyRequest)?.let { emit(it) } }
        } else {
            liveData(Dispatchers.IO) { repository.fetchTransactionHistory(historyRequest)?.let { emit(it) } }
        }
    }

    fun isTransactionHistoryInitialized() = ::transactionHistoryLiveData.isInitialized
}