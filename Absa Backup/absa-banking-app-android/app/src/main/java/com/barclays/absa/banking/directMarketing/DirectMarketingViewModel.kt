/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.directMarketing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.directMarketing.services.DirectMarketingInteractor
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.directMarketing.services.dto.UpdateMarketingIndicatorResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener

class DirectMarketingViewModel : ViewModel() {
    private val updateMarketingIndicatorExtendedResponseListener: ExtendedResponseListener<UpdateMarketingIndicatorResponse?> by lazy { UpdateDirectMarketingIndicatorsExtendedResponseListener(this) }
    var directMarketingInteractor: DirectMarketingInteractor = DirectMarketingInteractor()
    val marketingIndicatorResponse = MutableLiveData<UpdateMarketingIndicatorResponse?>()

    fun updateMarketingIndicators(marketingIndicators: MarketingIndicators) {
        directMarketingInteractor.updateMarketingIndicators(marketingIndicators, updateMarketingIndicatorExtendedResponseListener)
    }
}