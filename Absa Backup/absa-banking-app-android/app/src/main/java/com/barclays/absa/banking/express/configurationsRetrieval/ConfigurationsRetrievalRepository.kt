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

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigDetail
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigurationsRetrieveMultipleResponse
import com.barclays.absa.banking.express.configurationsRetrieval.dto.ConfigurationsRetrieveSingleResponse
import za.co.absa.networking.hmac.service.BaseRequest

class ConfigurationsRetrievalRepository : Repository() {

    companion object {
        private const val CONFIG_CATEGORY = "configCategory"
        private const val CONFIG_KEY = "configKey"
    }

    private var configList: List<ConfigDetail> = emptyList()
    private var configCategory: String = ""
    private var configKey: String = ""

    override var apiService = createXTMSService()

    override val service = "ConfigurationsRetrievalManagementFacade"

    override lateinit var mockResponseFile: String

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest {
        if (configList.isNotEmpty()) {
            val configDetailList = mutableListOf<Map<String, String>>()
            configList.forEach { configDetail ->
                configDetailList.apply {
                    add(mapOf(CONFIG_CATEGORY to configDetail.configCategory, CONFIG_KEY to configDetail.configKey))
                }
            }

            baseRequest.operation("GetMultipleConfigValues")
                    .addListParameter("listOfConfigDetail", configDetailList)
        } else {
            val configDetail = mutableMapOf<String, String>().apply {
                put(CONFIG_CATEGORY, configCategory)
                put(CONFIG_KEY, configKey)
            }

            baseRequest.operation("GetASingleConfigValue")
                    .addDictionaryParameter("configDetail", configDetail)
        }
        return baseRequest.build()
    }

    suspend fun fetchConfigValue(configCategory: String, configKey: String): ConfigurationsRetrieveSingleResponse? {
        mockResponseFile = "express/config_detail_response.json"

        this.configCategory = configCategory
        this.configKey = configKey
        return submitRequest()
    }

    suspend fun fetchConfigList(configList: List<ConfigDetail>): ConfigurationsRetrieveMultipleResponse? {
        mockResponseFile = "express/config_detail_list_response.json"

        this.configList = configList
        return submitRequest()
    }
}