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

package com.barclays.absa.banking.atmAndBranchLocator.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.atmAndBranchLocator.services.AtmBranchLocatorInteractor
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchLocatorResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class AtmBranchLocatorViewModel : BaseViewModel() {

   var atmBranchLocatorInteractor = AtmBranchLocatorInteractor()

    private val atmBranchLocatorExtendedResponseListener: ExtendedResponseListener<AtmBranchLocatorResponse> by lazy { AtmBranchLocatorExtendedResponseListener(this) }
    var atmBranchLocatorExtendedResponse = MutableLiveData<AtmBranchLocatorResponse>()

    fun fetchBranchLocations(latitude: String, longitude: String) {
        atmBranchLocatorInteractor.fetchAtmBranchDetails(latitude, longitude, 5000, atmBranchLocatorExtendedResponseListener)
    }
}