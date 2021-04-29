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
package com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import styleguide.utils.extensions.removeSpaces
import za.co.absa.networking.hmac.service.BaseRequest

class GetSecondaryCardMandateRepository : Repository() {
    lateinit var primaryCardNumber: String
    override val apiService = createXTMSService()

    override val service = "SecondaryCardManagementFacade"
    override val operation = "RetrieveSecondaryCardMandate"

    override var mockResponseFile = "express/secondaryCard/get_secondary_card_mandates.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder) = baseRequest
            .addParameter("accountNumber", primaryCardNumber.removeSpaces())
            .addParameter("action", "INQ")
            .build()

    suspend fun getSecondaryCardMandate(primaryCardNumber: String): GetSecondaryCardMandateResponse? {
        this.primaryCardNumber = primaryCardNumber
        return submitRequest()
    }
}