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
package com.barclays.absa.banking.manage.devices

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus.Companion.shouldAllowIdentifyFlow
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_KEY
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_CURRENT_DEVICE_VERIFICATION_DEVICE
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_NOMINATE_PRIMARY
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.IS_NOMINATE_VERIFICATION_DEVICE
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.NOMINATE_PRIMARY_DEVICE_REQUEST_CODE
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.TOTAL_DEVICES_KEY
import com.barclays.absa.banking.manage.devices.identification.IdentificationManageDeviceActivity
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse
import com.barclays.absa.banking.presentation.shared.widget.RecyclerViewClickListener
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.DeviceUtils
import kotlinx.android.synthetic.main.device_list_activity.*

class DeviceListActivity : BaseActivity(R.layout.device_list_activity), DeviceListView {
    private lateinit var presenter: DeviceListPresenter
    private lateinit var devices: List<Device>

    private var isNominateAnotherVerificationDevice = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.manage_device_device_list_title)

        presenter = DeviceListPresenter(this)
        mScreenName = BMBConstants.MANAGE_DEVICE
        mSiteSection = BMBConstants.SETTINGS_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST)
    }

    override fun onResume() {
        super.onResume()
        presenter.requestDeviceList()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val isNominatePrimary = intent?.extras?.getBoolean(IS_NOMINATE_PRIMARY) ?: false
        if (isNominatePrimary) {
            isNominateAnotherVerificationDevice = true
            captionContentAndLabelView.setContentText(getString(R.string.select_the_device_you_would_like_to_make_your_primary_device))
        }
    }

    override fun populateDeviceList(deviceList: DeviceListResponse) {
        devices = sortDeviceWithCurrentDeviceTop(deviceList.deviceList)

        with(devicesRecyclerView) {
            startAnimation(AnimationUtils.loadAnimation(this@DeviceListActivity, R.anim.fade_activity_in))
            adapter = DeviceListItemAdapter(devices, isNominateAnotherVerificationDevice)
            adapter?.notifyDataSetChanged()
            addOnItemTouchListener(RecyclerViewClickListener(this@DeviceListActivity) { _, position ->
                val device = devices[position]
                if (isNominateAnotherVerificationDevice && device.isPrimarySecondFactorDevice) {
                    toastLong(R.string.already_verification_device)
                    return@RecyclerViewClickListener
                }

                val isVerificationDevice = isVerificationDevice(SecureUtils.getDeviceID())
                val intent = getDeviceIntent(device, isNominateAnotherVerificationDevice, isVerificationDevice)
                if (isNominateAnotherVerificationDevice) {
                    startActivityForResult(intent, NOMINATE_PRIMARY_DEVICE_REQUEST_CODE)
                } else {
                    startActivity(intent)
                }
            })
        }

        if (shouldAllowIdentifyFlow(instance.biometricStatus)) {
            appCacheService.setHasPrimaryDevice(devices.any { it.isPrimarySecondFactorDevice })
        }
    }

    private fun sortDeviceWithCurrentDeviceTop(devices: List<Device>?): List<Device> = devices?.sortedByDescending { device -> DeviceUtils.isCurrentDevice(device) } ?: emptyList()

    private fun isVerificationDevice(deviceUniqueIdentifier: String): Boolean = devices.any { device -> device.imei == deviceUniqueIdentifier && device.isPrimarySecondFactorDevice }

    private fun getDeviceIntent(device: Device, isNominatedVerificationDevice: Boolean, isCurrentDeviceVerificationDevice: Boolean): Intent {
        return if (featureSwitchingToggles.biometricVerification == FeatureSwitchingStates.ACTIVE.key && shouldAllowIdentifyFlow(instance.biometricStatus)) {
            Intent(this, IdentificationManageDeviceActivity::class.java).apply {
                putExtra(DEVICE_KEY, device)
                putExtra(TOTAL_DEVICES_KEY, devices.size)
                putExtra(IS_NOMINATE_VERIFICATION_DEVICE, isNominatedVerificationDevice)
                putExtra(IS_CURRENT_DEVICE_VERIFICATION_DEVICE, isCurrentDeviceVerificationDevice)
            }
        } else {
            Intent(this, ManageDeviceActivity::class.java).apply {
                putExtra(DEVICE_KEY, device)
                putExtra(TOTAL_DEVICES_KEY, devices.size)
                putExtra(IS_NOMINATE_VERIFICATION_DEVICE, isNominatedVerificationDevice)
                putExtra(IS_CURRENT_DEVICE_VERIFICATION_DEVICE, isCurrentDeviceVerificationDevice)
            }
        }
    }
}