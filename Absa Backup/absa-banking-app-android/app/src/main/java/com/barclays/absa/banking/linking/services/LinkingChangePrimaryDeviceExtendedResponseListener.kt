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
 */

package com.barclays.absa.banking.linking.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.ui.LinkingViewModel
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse

class LinkingChangePrimaryDeviceExtendedResponseListener(val linkingViewModel: LinkingViewModel) : ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
    private val appCacheService: IAppCacheService = getServiceInterface()
    override fun onRequestStarted() {

        if (!appCacheService.isBioAuthenticated()) {
            super.onRequestStarted()
        }
    }

    override fun onSuccess(successResponse: ChangePrimaryDeviceResponse) {
        linkingViewModel.changePrimaryDeviceSureCheckResult.value = successResponse
    }
}