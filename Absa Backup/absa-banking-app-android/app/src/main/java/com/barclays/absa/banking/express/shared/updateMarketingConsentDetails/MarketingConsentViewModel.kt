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

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.express.ExpressBaseViewModel
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentRequest
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentResponse
import kotlinx.coroutines.Dispatchers

class MarketingConsentViewModel : ExpressBaseViewModel() {
    lateinit var marketingConsentLiveData: LiveData<MarketingConsentResponse>
    override val repository by lazy { MarketingConsentRepository() }

    fun updateMarketingConsent(marketingConsentRequest: MarketingConsentRequest) {
        marketingConsentLiveData = liveData(Dispatchers.IO) {
            repository.updateMarketingConsent(marketingConsentRequest)?.let { emit(it) }
        }
    }
}