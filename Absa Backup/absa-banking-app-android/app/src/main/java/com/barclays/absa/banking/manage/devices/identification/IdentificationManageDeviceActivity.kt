/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.manage.devices.identification

import android.os.Bundle
import com.barclays.absa.banking.databinding.IdentificationManageDeviceActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_KEY
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_CURRENT_DEVICE_VERIFICATION_DEVICE
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_NOMINATE_VERIFICATION_DEVICE
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.TOTAL_DEVICES_KEY
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.utils.viewModel

class IdentificationManageDeviceActivity : BaseActivity() {
    private val binding by viewBinding(IdentificationManageDeviceActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manageDevicesViewModel: ManageDevicesViewModel = viewModel()

        appCacheService.setFromManageDevicesFlow(true)

        intent.extras?.let { bundle ->
            manageDevicesViewModel.deviceDetails = bundle.getSerializable(DEVICE_KEY) as Device
            appCacheService.setCurrentDevice(manageDevicesViewModel.deviceDetails)
            manageDevicesViewModel.totalDevice = bundle.getInt(TOTAL_DEVICES_KEY)
            manageDevicesViewModel.isNominateVerificationDevice = bundle.getBoolean(IS_NOMINATE_VERIFICATION_DEVICE)
            manageDevicesViewModel.currentDeviceIsTheVerificationDevice = bundle.getBoolean(IS_CURRENT_DEVICE_VERIFICATION_DEVICE)
        }
    }

    override fun onBackPressed() {
        appCacheService.setFromManageDevicesFlow(false)

        super.onBackPressed()
    }
}