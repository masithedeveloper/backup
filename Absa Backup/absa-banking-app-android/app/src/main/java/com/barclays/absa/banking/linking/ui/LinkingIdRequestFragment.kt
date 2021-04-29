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

package com.barclays.absa.banking.linking.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.databinding.LinkingIdRequestFragmentBinding
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.extensions.viewBinding
import com.oneidentity.sdk.android.OneIdentity
import styleguide.forms.validation.SouthAfricanIdNumberValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.screens.GenericResultScreenFragment

class LinkingIdRequestFragment : LinkingBaseFragment(R.layout.linking_id_request_fragment) {
    private val binding by viewBinding(LinkingIdRequestFragmentBinding::bind)

    companion object {
        private const val WE_COULD_NOT_VERIFY_YOUR_IDENTITY = "We could not verify your identity"
        private const val BIOMETRICS_DISABLED = "Biometrics disabled"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.linking_identity_fragment_toolbar_title)) {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_LetsGetStartedScreen_CloseButtonClicked")
            baseActivity.onBackPressed()
        }

        binding.idNormalInputView.addValidationRule(SouthAfricanIdNumberValidationRule(R.string.id_number_errormessage))
        setupListeners()
    }

    private fun setupListeners() {
        binding.noSaIdCheckBoxView.setOnCheckedListener {
            if (it) {
                KeyboardUtils.hideKeyboard(linkingActivity)
            }
            binding.idNormalInputView.visibility = if (it) View.GONE else View.VISIBLE
        }

        binding.nextButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_LetsGetStartedScreen_NextButtonClicked")

            if (binding.noSaIdCheckBoxView.isChecked) {
                AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_LetsGetStartedScreen_IDontHaveAnSAIDButtonClicked")
                startActivity(Intent(context, AccountLoginActivity::class.java))
                return@setOnClickListener
            }

            if (!isInputValid()) {
                return@setOnClickListener
            }
            BMBLogger.d("DeviceProfiling: PreLoginCSID used", appCacheService.getCustomerSessionId())
            linkingViewModel.fetchLinkedProfiles(binding.idNormalInputView.selectedValue.trim())
        }

        linkingViewModel.linkedProfileResponse = MutableLiveData()
        linkingViewModel.linkedProfileResponse.observe(viewLifecycleOwner, {
            linkingViewModel.linkingReferenceNumber = it.referenceNumber
            dismissProgressDialog()

            when {
                it.transactionMessage.equals(BIOMETRICS_DISABLED) -> {
                    appCacheService.setShouldRevertToOldLinkingFlow(true)
                    navigateToAccountLogin()
                }
                it.referenceNumber.isNotBlank() -> {
                    FacialBiometricUtil.startSDK(requireContext(), it.referenceNumber)
                    MonitoringInteractor().logBiometricCheckEvent("${it.referenceNumber} OneIdentitySDK - Started (LinkingIdRequestFragment)")

                    if (OneIdentity.getInstance().isValidIDNumber(binding.idNormalInputView.selectedValueUnmasked.trim(), true)) {
                        appCacheService.setCustomerIdNumber(binding.idNormalInputView.selectedValueUnmasked.trim())
                        MonitoringInteractor().logBiometricCheckEvent("${it.referenceNumber} isValidIDNumber - true (LinkingIdRequestFragment)")
                        appCacheService.setIsIdentificationAndVerificationFlow(true)
                        appCacheService.setIsIdentificationAndVerificationLinkingFlow(true)
                        navigate(LinkingIdRequestFragmentDirections.actionIdRequestFragmentToIdVerificationRequestFragment(null))
                    }
                }
                FAILURE.equals(it.transactionStatus, ignoreCase = true) && (it.transactionMessage?.contains(WE_COULD_NOT_VERIFY_YOUR_IDENTITY) == true) -> {
                    MonitoringInteractor().logBiometricCheckEvent(it.transactionMessage ?: WE_COULD_NOT_VERIFY_YOUR_IDENTITY)
                    val genericResultScreenProperties = LinkingResultFactory(this).showFailureScreenFragment(getString(R.string.linking_we_could_not_verify_your_identity), getString(R.string.linking_we_could_not_verify_your_identity_masked_error), getString(R.string.ok)) {
                        linkingActivity.finish()
                    }
                    findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
                FAILURE.equals(it.transactionStatus, ignoreCase = true) -> {
                    MonitoringInteractor().logBiometricCheckEvent("Fetch Services failed")
                    val genericResultScreenProperties = LinkingResultFactory(this).showFailureScreenFragment(getString(R.string.linking_we_could_not_verify_your_identity), it.transactionMessage.toString(), getString(R.string.done)) {
                        linkingActivity.finish()
                    }
                    findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
                else -> {
                    MonitoringInteractor().logBiometricCheckEvent("No Biometric reference number available")
                    val genericResultScreenProperties = LinkingResultFactory(this).showFailureScreenFragment(R.string.something_went_wrong, R.string.something_went_wrong_message, R.string.done) {
                        linkingActivity.finish()
                    }
                    findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
            }
        })
    }

    private fun isInputValid(): Boolean = binding.idNormalInputView.validate()
}