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
 *
 */

package com.barclays.absa.banking.saveAndInvest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.SaveAndInvestMarketingConsentFragmentBinding
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.PreferredMarketingChannels
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.MarketingConsentViewModel
import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentRequest
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import styleguide.screens.GenericResultScreenFragment
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenProperties

abstract class SaveAndInvestMarketingConsentFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_marketing_consent_fragment) {

    private val viewModel by viewModels<MarketingConsentViewModel>()
    private val binding by viewBinding(SaveAndInvestMarketingConsentFragmentBinding::bind)
    private val marketingConsentRequest by lazy { MarketingConsentRequest() }

    abstract fun navigateOnMarketingConsentUpdate()
    abstract fun navigateToFailure()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCurrentSelections()
        attachSelectionListeners()
        binding.nextButton.setOnClickListener {
            validateAndUpdateMarketingConsent()
        }
    }

    private fun setupCurrentSelections() {
        with(saveAndInvestViewModel.customerDetails.marketingConsent) {
            binding.smsCheckBoxView.isChecked = nonCreditSMS.equals(YES, ignoreCase = true)
            binding.emailCheckBoxView.isChecked = nonCreditEmail.equals(YES, ignoreCase = true)
            binding.voiceRecordingCheckBoxView.isChecked = nonCreditAutoVoice.equals(YES, ignoreCase = true)
        }
    }

    private fun clearNoThanksSelection(isChecked: Boolean) {
        if (isChecked) {
            binding.errorTextView.visibility = View.GONE
            binding.noThanksCheckBoxView.isChecked = false
            marketingConsentRequest.nonCreditIndicator = YES
        }
    }

    private fun attachSelectionListeners() {
        with(binding) {
            emailCheckBoxView.setOnCheckedListener { isChecked -> clearNoThanksSelection(isChecked) }
            smsCheckBoxView.setOnCheckedListener { isChecked -> clearNoThanksSelection(isChecked) }
            voiceRecordingCheckBoxView.setOnCheckedListener { isChecked -> clearNoThanksSelection(isChecked) }
            noThanksCheckBoxView.setOnCheckedListener { isChecked ->
                if (isChecked) {
                    errorTextView.visibility = View.GONE
                    clearAllMarketingChannelSelections()
                    marketingConsentRequest.nonCreditIndicator = NO
                }
            }
        }
    }

    private fun clearAllMarketingChannelSelections() {
        with(binding) {
            smsCheckBoxView.isChecked = false
            emailCheckBoxView.isChecked = false
            voiceRecordingCheckBoxView.isChecked = false
        }
    }

    private fun validateAndUpdateMarketingConsent() {
        if (!binding.noThanksCheckBoxView.isChecked && !binding.emailCheckBoxView.isChecked && !binding.smsCheckBoxView.isChecked && !binding.voiceRecordingCheckBoxView.isChecked) {
            saveAndInvestActivity.animate(binding.marketingConsentLayout, R.anim.shake)
            binding.errorTextView.visibility = View.VISIBLE
        } else {
            with(viewModel) {
                updateMarketingConsent(marketingConsentRequest.apply {
                    nonCreditSMS = if (binding.smsCheckBoxView.isChecked) YES else NO
                    nonCreditEmail = if (binding.emailCheckBoxView.isChecked) YES else NO
                    nonCreditAutoVoice = if (binding.voiceRecordingCheckBoxView.isChecked) YES else NO
                    nonCreditTelephone = YES
                    nonCreditPost = YES
                })
                marketingConsentLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    if (it.header.resultMessages.isEmpty()) {
                        saveMarketingConsentData()
                        navigateOnMarketingConsentUpdate()
                    } else {
                        navigateToFailure()
                    }
                })
                failureLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    navigateToFailure()
                })
            }
        }
    }

    private fun saveMarketingConsentData() {
        with(saveAndInvestViewModel.dataSharingAndMarketingConsent) {
            preferredMarketingChannels = PreferredMarketingChannels().apply {
                viaSMS = binding.smsCheckBoxView.isChecked.toString()
                viaEmail = binding.emailCheckBoxView.isChecked.toString()
                viaTelephone = true.toString()
                viaPost = true.toString()
            }
            marketingConsentFlag = binding.noThanksCheckBoxView.isChecked.toString()
        }
    }

    protected fun buildFailureResultScreenProperties(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.unable_to_save_changes))
                .setDescription(getString(R.string.unable_to_save_changes_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}