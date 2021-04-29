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

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.ManageDeviceResult
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor
import com.barclays.absa.banking.manage.devices.services.ManageDevicesService
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class ManageDevicesViewModel : BaseViewModel() {
    private var isCurrentDeviceDeleted: Boolean = false
    private val manageDevicesInteractor: ManageDevicesService by lazy { ManageDevicesInteractor() }

    private val manageDeviceUpdateResultExtendedResponseListener: ManageDeviceUpdateResultExtendedResponseListener by lazy { ManageDeviceUpdateResultExtendedResponseListener(this) }
    private val changePrimaryDeviceExtendedResponseListener: ChangePrimaryDeviceExtendedResponseListener by lazy { ChangePrimaryDeviceExtendedResponseListener(this) }
    private val delinkDeviceExtendedResponseListener: DelinkDeviceExtendedResponseListener by lazy { DelinkDeviceExtendedResponseListener(this) }

    var manageDeviceUpdateResult: MutableLiveData<ManageDeviceResult> = MutableLiveData()
    var delinkDeviceResult: MutableLiveData<ManageDeviceResult> = MutableLiveData()
    var changePrimaryDeviceResult: MutableLiveData<ChangePrimaryDeviceResponse> = MutableLiveData()

    var deviceDetails: Device = Device()
    var totalDevice: Int = 0
    var isNominateVerificationDevice: Boolean = false
    var currentDeviceIsTheVerificationDevice: Boolean = false

    fun changeDeviceNickname(deviceDetails: Device, deviceNickname: String) {
        manageDevicesInteractor.editDeviceNickname(deviceDetails, deviceNickname, manageDeviceUpdateResultExtendedResponseListener)
    }

    fun changePrimaryDevice(newPrimaryDevice: Device) {
        manageDevicesInteractor.changePrimaryDevice(newPrimaryDevice, changePrimaryDeviceExtendedResponseListener)
    }

    fun delinkDevice(deviceDetails: Device, isCurrentDevice: Boolean) {
        isCurrentDeviceDeleted = isCurrentDevice
        manageDevicesInteractor.delinkDevice(deviceDetails, delinkDeviceExtendedResponseListener)
    }
}