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

package com.barclays.absa.banking.express.getAllBalances

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.getAllBalances.dto.BalanceResponse
import kotlinx.coroutines.Dispatchers

class GetAllBalancesViewModel : ExpressBaseViewModel() {

    override val repository by lazy { GetAllBalancesRepository() }

    lateinit var balancesLiveData: LiveData<BalanceResponse>

    fun fetchAllBalances(cacheHeader: CacheHeader) {
        balancesLiveData = liveData(Dispatchers.IO) { repository.getAllBalances(cacheHeader)?.let { emit(it) } }
    }
}