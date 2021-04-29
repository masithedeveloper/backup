/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.relationshipBanking.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingSiteDetailsResponse

class FilteredSiteDetailsListener(private val filteredSiteLiveData: MutableLiveData<List<SiteFilteredDetailsVO>>) : ExtendedResponseListener<BusinessBankingSiteDetailsResponse>() {
    override fun onSuccess(successResponse: BusinessBankingSiteDetailsResponse) {
        filteredSiteLiveData.value = successResponse.siteDetails
    }
}