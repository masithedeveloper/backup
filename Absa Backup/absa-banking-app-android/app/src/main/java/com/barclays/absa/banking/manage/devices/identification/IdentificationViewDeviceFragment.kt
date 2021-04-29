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

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.databinding.IdentificationViewDeviceFragmentBinding
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus.Companion.shouldAllowIdentifyFlow
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.linking.ui.LinkingActivity
import com.barclays.absa.banking.linking.ui.LinkingResultFactory
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.ID_NUMBER
import com.barclays.absa.banking.manage.devices.VerificationDeviceAvailableActivity
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.DeviceUtils.isCurrentDevice
import com.barclays.absa.utils.extensions.viewBinding

class IdentificationViewDeviceFragment : IdentificationManageDevicesBaseFragment(R.layout.identification_view_device_fragment) {
    private lateinit var editMenuItem: MenuItem
    private lateinit var sureCheckDelegate: SureCheckDelegate

    private val binding by viewBinding(IdentificationViewDeviceFragmentBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageDevicesViewModel.changePrimaryDevice(manageDevicesViewModel.deviceDetails) }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (manageDevicesViewModel.currentDeviceIsTheVerificationDevice) {
            setHasOptionsMenu(true)
        }

        setToolBar(R.string.manage_device_device_title)
        initViews()
        initObservers()

        if (manageDevicesViewModel.isNominateVerificationDevice) {
            isSureCheckDeviceAvailableInvoked()
        }
    }

    private fun initObservers() {
        manageDevicesViewModel.delinkDeviceResult.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigate(IdentificationViewDeviceFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showSuccessFragment(R.string.device_delinked_success_message, -1, R.string.close)))
        })

        manageDevicesViewModel.changePrimaryDeviceResult.observe(viewLifecycleOwner, {
            dismissProgressDialog()

            if (it.deviceChanged) {
                dismissProgressDialog()
                showPrimaryDeviceChangeSuccessfulScreen()
            } else {
                val sureCheckFlag: String = it.sureCheckFlag ?: ""
                dismissProgressDialog()
                if (sureCheckFlag.isBlank()) {
                    val status: String = it.transactionStatus ?: ""
                    if (BMBConstants.FAILURE.equals(status, ignoreCase = true)) {
                        BaseAlertDialog.showErrorAlertDialog(it.transactionMessage)
                    }
                } else {
                    val verificationType = TransactionVerificationType.valueOf(sureCheckFlag)
                    appCacheService.setSureCheckReferenceNumber(it.referenceNumber)
                    appCacheService.setSureCheckCellphoneNumber(it.cellnumber)
                    appCacheService.setSureCheckEmail(it.email)
                    onReceivedSureCheckVerificationType(verificationType)
                }
            }
        })
    }

    private fun onReceivedSureCheckVerificationType(verificationType: TransactionVerificationType) {
        when (verificationType) {
            TransactionVerificationType.SURECHECKV2Required -> sureCheckDelegate.initiateV2CountDownScreen()
            TransactionVerificationType.SURECHECKV2_FALLBACK, TransactionVerificationType.SURECHECKV2_FALLBACKRequired -> sureCheckDelegate.initiateOfflineOtpScreen()
            TransactionVerificationType.NoPrimaryDevice -> showNoPrimaryDeviceScreen()
            else -> {
                MonitoringInteractor().logTechnicalEvent("IdentificationViewDeviceFragment", "TransactionVerification", verificationType.key.toString())
            }
        }
    }

    private fun initViews() {
        val isSelectedDeviceCurrentDevice = isCurrentDevice(manageDevicesViewModel.deviceDetails)
        val isSelectedDevicePrimaryDevice = manageDevicesViewModel.deviceDetails.isPrimarySecondFactorDevice

        binding.deviceNicknameTextView.text = manageDevicesViewModel.deviceDetails.nickname
        binding.deviceModelTextView.text = manageDevicesViewModel.deviceDetails.model

        if (getString(R.string.manage_device_manufacturer_name_apple).equals(manageDevicesViewModel.deviceDetails.manufacturer, ignoreCase = true)) {
            binding.deviceManufacturerImageView.setBackgroundResource(R.drawable.ic_device_apple)
        } else {
            binding.deviceManufacturerImageView.setBackgroundResource(R.drawable.ic_device_android)
        }

        binding.primaryDeviceOptionActionView.visibility = if (manageDevicesViewModel.deviceDetails.isPrimarySecondFactorDevice) View.VISIBLE else View.GONE
        binding.primaryDeviceOptionActionView.leftImageView.imageTintList = null
        binding.currentDeviceOptionActionView.visibility = if (isCurrentDevice(manageDevicesViewModel.deviceDetails)) View.VISIBLE else View.GONE

        if (manageDevicesViewModel.totalDevice > 1) {
            binding.makeDeviceVerificationDeviceButton.visibility = if (isSelectedDevicePrimaryDevice) View.GONE else View.VISIBLE
            when {
                isSelectedDevicePrimaryDevice && isSelectedDeviceCurrentDevice -> {
                    binding.delinkDeviceButton.visibility = View.GONE
                    binding.makeDeviceVerificationDeviceButton.visibility = View.GONE
                }
                isSelectedDevicePrimaryDevice && !isSelectedDeviceCurrentDevice -> {
                    binding.delinkDeviceButton.visibility = View.GONE
                }
                !isSelectedDevicePrimaryDevice && isSelectedDeviceCurrentDevice -> {
                    binding.makeDeviceVerificationDeviceButton.visibility = View.VISIBLE
                    binding.delinkDeviceButton.visibility = View.VISIBLE
                }
                else -> {
                    binding.makeDeviceVerificationDeviceButton.visibility = View.GONE
                    binding.delinkDeviceButton.visibility = View.GONE
                }
            }
        } else {
            binding.makeDeviceVerificationDeviceButton.visibility = if (isSelectedDevicePrimaryDevice) View.GONE else View.VISIBLE
            binding.delinkDeviceButton.visibility = View.GONE
        }
        binding.delinkDeviceButton.visibility = if (!isCurrentDevice(manageDevicesViewModel.deviceDetails) && !isSelectedDevicePrimaryDevice && manageDevicesViewModel.deviceDetails.isSecondFactorEnabled && manageDevicesViewModel.currentDeviceIsTheVerificationDevice) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (isCurrentDevice(manageDevicesViewModel.deviceDetails) && !isSelectedDevicePrimaryDevice) {
            binding.delinkDeviceButton.visibility = View.VISIBLE
        }

        if (isCurrentDevice(manageDevicesViewModel.deviceDetails) && manageDevicesViewModel.currentDeviceIsTheVerificationDevice) {
            binding.delinkDeviceButton.visibility = View.GONE
        }

        if (AccessPrivileges.instance.isOperator) {
            binding.delinkDeviceButton.visibility = View.GONE
            binding.makeDeviceVerificationDeviceButton.visibility = View.GONE
        }

        if (!CustomerProfileObject.instance.isTransactionalUser) {
            binding.makeDeviceVerificationDeviceButton.visibility = View.GONE
        }

        binding.delinkDeviceButton.setOnClickListener { displayDelinkPopup() }

        binding.makeDeviceVerificationDeviceButton.setOnClickListener {
            appCacheService.setChangePrimaryDeviceFlow(true)
            preventDoubleClick(it)
            appCacheService.setDelinkedPrimaryDevice(null)
            isSureCheckDeviceAvailableInvoked()
        }
    }

    private fun isSureCheckDeviceAvailableInvoked() {
        if (manageDevicesViewModel.currentDeviceIsTheVerificationDevice) {
            manageDevicesViewModel.changePrimaryDevice(manageDevicesViewModel.deviceDetails)
        } else if (manageDevicesViewModel.deviceDetails.isSecondFactorEnabled) {
            val isIdentification = featureSwitchingToggles.biometricVerification == FeatureSwitchingStates.ACTIVE.key
            if (isIdentification && shouldAllowIdentifyFlow(CustomerProfileObject.instance.biometricStatus)) {
                Intent(identificationManageDeviceActivity, LinkingActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
                    putExtra(ID_NUMBER, CustomerProfileObject.instance.idNumber)
                    startActivity(this)
                }
            } else {
                Intent(identificationManageDeviceActivity, VerificationDeviceAvailableActivity::class.java).apply {
                    putExtra(DEVICE_OBJECT, manageDevicesViewModel.deviceDetails)
                    startActivity(this)
                }
            }
        } else {
            baseActivity.toastLong(R.string.upgrade_device_to_2fa)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.manage_device_menu, menu)
        editMenuItem = menu.findItem(R.id.editMenuItem)
        editMenuItem.isVisible = !manageDevicesViewModel.isNominateVerificationDevice
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.editMenuItem) {
            navigate(IdentificationViewDeviceFragmentDirections.actionIdentificationViewDeviceFragmentToIdentificationEditDeviceNicknameFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayDelinkPopup() {
        val message = if (manageDevicesViewModel.deviceDetails.isPrimarySecondFactorDevice) getString(R.string.you_are_about_to_de_link_primary) else getString(R.string.device_delink_popup_msg)
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.remove_device_title))
                .message(message)
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    if (manageDevicesViewModel.deviceDetails.isPrimarySecondFactorDevice) {
                        appCacheService.setDelinkedPrimaryDevice(manageDevicesViewModel.deviceDetails)
                    } else {
                        appCacheService.setDelinkedPrimaryDevice(null)
                        val isCurrentDevice = isCurrentDevice(manageDevicesViewModel.deviceDetails)
                        manageDevicesViewModel.delinkDevice(manageDevicesViewModel.deviceDetails, isCurrentDevice)
                    }
                })
    }

    private fun showPrimaryDeviceChangeSuccessfulScreen() {
        navigate(IdentificationViewDeviceFragmentDirections.actionGlobalGenericResultFragment(LinkingResultFactory(this).showPrimaryDeviceChangedSuccessfullyResultFragment()))
    }
}