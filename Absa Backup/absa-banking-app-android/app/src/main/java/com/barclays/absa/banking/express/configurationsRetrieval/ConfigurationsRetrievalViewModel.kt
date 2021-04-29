/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.express.configurationsRetrieval

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigDetail
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigurationsRetrieveMultipleResponse
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigurationsRetrieveSingleResponse
import kotlinx.coroutines.Dispatchers

class ConfigurationsRetrievalViewModel : ExpressBaseViewModel() {

    override val repository by lazy { ConfigurationsRetrievalRepository() }
    lateinit var configSingleLiveData: LiveData<ConfigurationsRetrieveSingleResponse>
    lateinit var configMultipleLiveData: LiveData<ConfigurationsRetrieveMultipleResponse>

    fun fetchSingleConfigValue(configCategory: String, configKey: String) {
        configSingleLiveData = liveData(Dispatchers.IO) {
            repository.fetchConfigValue(configCategory, configKey)?.let {
                emit(it)
            }
        }
    }

    fun fetchMultipleConfigList(configList: List<ConfigDetail>) {
        configMultipleLiveData = liveData(Dispatchers.IO) {
            repository.fetchConfigList(configList)?.let {
                emit(it)
            }
        }
    }
}