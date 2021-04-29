/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.cashSend.unredeemedCashSendTransactions.dto.CashSendUnredeemedTransactionsResponse
import kotlinx.coroutines.Dispatchers

class CashSendUnredeemedTransactionsViewModel : ExpressBaseViewModel() {
    override val repository by lazy { CashSendUnredeemedTransactionsRepository() }

    lateinit var cashSendUnredeemedTransactionsResponse: MutableLiveData<CashSendUnredeemedTransactionsResponse>

    fun fetchUnredeemedTransactions(sourceAccount: String) {
        cashSendUnredeemedTransactionsResponse = liveData(Dispatchers.IO) {
            repository.fetchUnredeemedTransactions(sourceAccount)?.let { emit(it) }
        } as MutableLiveData<CashSendUnredeemedTransactionsResponse>
    }
}