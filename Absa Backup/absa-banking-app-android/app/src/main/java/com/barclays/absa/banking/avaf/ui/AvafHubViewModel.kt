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
package com.barclays.absa.banking.avaf.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.avaf.accountInformation.AccountInformationRepository
import com.barclays.absa.banking.express.avaf.accountInformation.dto.AbsaVehicleAndAssetFinanceDetailResponse
import kotlinx.coroutines.Dispatchers

class AvafHubViewModel : ExpressBaseViewModel() {
    lateinit var avafAccountDetailLiveData: LiveData<AbsaVehicleAndAssetFinanceDetailResponse>

    override val repository: AccountInformationRepository by lazy { AccountInformationRepository() }

    fun fetchAvafAccountDetail(accountNumber: String) {
        avafAccountDetailLiveData = liveData(Dispatchers.IO) {
            repository.fetchAccountInformation(accountNumber)?.let {
                emit(it)
            }
        }
    }

}