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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.IdentificationEditDeviceNicknameFragmentBinding
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule

class IdentificationEditDeviceNicknameFragment : IdentificationManageDevicesBaseFragment(R.layout.identification_edit_device_nickname_fragment) {
    private val binding by viewBinding(IdentificationEditDeviceNicknameFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        manageDevicesViewModel.manageDeviceUpdateResult.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            identificationManageDeviceActivity.finish()
        })
    }

    private fun initViews() {
        binding.nicknameNormalInputView.selectedValue = manageDevicesViewModel.deviceDetails.nickname

        binding.saveDetailsButton.setOnClickListener {
            binding.nicknameNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.manage_device_nickname_error))

            if (binding.nicknameNormalInputView.validate()) {
                manageDevicesViewModel.changeDeviceNickname(manageDevicesViewModel.deviceDetails, binding.nicknameNormalInputView.selectedValue)
            }
        }
    }
}