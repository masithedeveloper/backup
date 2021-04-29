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

package com.barclays.absa.banking.express.shared.updateMarketingConsentDetails

import com.barclays.absa.banking.express.Repository
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentRequest
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentResponse
import za.co.absa.networking.hmac.service.BaseRequest

class MarketingConsentRepository : Repository() {
    private lateinit var marketingConsentRequest: MarketingConsentRequest

    override val apiService = createXSMSService()
    override val service = "CustomerInformationFacade"
    override val operation = "UpdateMarketingConsentDetails"
    override var mockResponseFile: String = "express/shared/update_marketing_consent.json"

    override fun buildRequest(baseRequest: BaseRequest.Builder): BaseRequest =
        baseRequest.addObjectParameter("marketingConsentDetailsVO", marketingConsentRequest).build()

    suspend fun updateMarketingConsent(marketingConsentRequest: MarketingConsentRequest): MarketingConsentResponse? {
        this.marketingConsentRequest = marketingConsentRequest
        return submitRequest()
    }
}