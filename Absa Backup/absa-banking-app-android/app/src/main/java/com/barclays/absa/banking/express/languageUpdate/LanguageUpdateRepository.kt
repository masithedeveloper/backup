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
package com.barclays.absa.banking.express.languageUpdate

import com.barclays.absa.banking.express.Repository
import za.co.absa.networking.dto.BaseResponse
import za.co.absa.networking.hmac.service.BaseRequest

class LanguageUpdateRepository : Repository() {

    private lateinit var language: String

    override val apiService = createXTMSService()

    override val service = "LanguageFacade"
    override val operation = "ChangeLanguagePreference"

    override var mockResponseFile: String = "express/language_update.json"

    suspend fun updateLanguage(language: String): BaseResponse? {
        this.language = language
        return submitRequest()
    }

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("preferredLanguage", language)
            .build()
}