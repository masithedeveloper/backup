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

package com.barclays.absa.banking.manage.profile.ui.marketingConsent

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.services.dto.MarketingIndicator
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_edit_marketing_consent_fragment.*
import styleguide.forms.OnCheckedListener

class ManageProfileEditMarketingConsentFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_marketing_consent_fragment) {
    private lateinit var marketingIndicator: MarketingIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_hub_marketing_consent_title)

        setUpOnCheckListeners()
        setUpOnClickListener()
        enableContinueButton()

        manageProfileViewModel.customerInformation.value?.customerInformation?.marketingIndicator?.let {
            marketingIndicator = it
        }

        if (manageProfileViewModel.manageProfileFlow != null && manageProfileViewModel.manageProfileFlow == ManageProfileFlow.MISSING_MARKET_CONSENT) {
            manageProfileViewModel.marketingConsentDetailsToUpdate.apply {
                nonCreditIndicator = marketingIndicator.nonCreditIndicator
                nonCreditSmsIndicator = marketingIndicator.nonCreditSmsIndicator
                nonCreditEmailIndicator = marketingIndicator.nonCreditEmailIndicator
                nonCreditAutoVoiceIndicator = marketingIndicator.nonCreditAutoVoiceIndicator
                creditIndicator = marketingIndicator.creditIndicator
                creditSmsIndicator = marketingIndicator.creditSmsIndicator
                creditEmailIndicator = marketingIndicator.creditEmailIndicator
                creditTeleIndicator = marketingIndicator.creditTeleIndicator
            }

            marketingConsentTextView.visibility = View.VISIBLE
            marketingConsentDescriptionTextView.visibility = View.VISIBLE
            dividerView.visibility = View.VISIBLE

            continueButton.isEnabled = true
        }
        initData()
    }

    private fun initData() {
        if (NO.equals(manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditIndicator, true)) {
            electronicNoThanksCheckBoxView.isChecked = true
        } else {
            electronicEmailCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditEmailIndicator)
            electronicSmsCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditSmsIndicator)
            electronicVoiceRecordingCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditAutoVoiceIndicator)
        }

        if (NO.equals(manageProfileViewModel.marketingConsentDetailsToUpdate.creditIndicator, true)) {
            creditNoThanksCheckBoxView.isChecked = true
        } else {
            creditEmailCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.creditEmailIndicator)
            creditSmsCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.creditSmsIndicator)
            creditTelephoneCheckBoxView.isChecked = checkMarketingIndicator(manageProfileViewModel.marketingConsentDetailsToUpdate.creditTeleIndicator)
        }
    }

    private fun checkMarketingIndicator(indicator: String): Boolean {
        return indicator.equals(YES, true)
    }

    private fun setUpOnCheckListeners() {

        electronicNoThanksCheckBoxView.setOnCheckedListener { isChecked ->
            if (isChecked) {
                electronicEmailCheckBoxView.isChecked = false
                electronicSmsCheckBoxView.isChecked = false
                electronicVoiceRecordingCheckBoxView.isChecked = false
            }
            enableContinueButton()
        }

        creditNoThanksCheckBoxView.setOnCheckedListener { isChecked ->
            if (isChecked) {
                creditEmailCheckBoxView.isChecked = false
                creditSmsCheckBoxView.isChecked = false
                creditTelephoneCheckBoxView.isChecked = false
            }
            enableContinueButton()
        }

        val electronicNoThanksCheckedListener = OnCheckedListener { isChecked ->
            if (isChecked) {
                electronicNoThanksCheckBoxView.isChecked = false
            }
            enableContinueButton()
        }
        val creditNoThanksCheckedListener = OnCheckedListener { isChecked ->
            if (isChecked) {
                creditNoThanksCheckBoxView.isChecked = false
            }
            enableContinueButton()
        }

        electronicEmailCheckBoxView.setOnCheckedListener(electronicNoThanksCheckedListener)
        electronicSmsCheckBoxView.setOnCheckedListener(electronicNoThanksCheckedListener)
        electronicVoiceRecordingCheckBoxView.setOnCheckedListener(electronicNoThanksCheckedListener)
        creditEmailCheckBoxView.setOnCheckedListener(creditNoThanksCheckedListener)
        creditSmsCheckBoxView.setOnCheckedListener(creditNoThanksCheckedListener)
        creditTelephoneCheckBoxView.setOnCheckedListener(creditNoThanksCheckedListener)
    }

    private fun setUpOnClickListener() {
        continueButton.setOnClickListener {
            manageProfileViewModel.marketingConsentDetailsToUpdate.apply {
                nonCreditEmailIndicator = electronicEmailCheckBoxView.isChecked.booleanToString()
                nonCreditSmsIndicator = electronicSmsCheckBoxView.isChecked.booleanToString()
                nonCreditAutoVoiceIndicator = electronicVoiceRecordingCheckBoxView.isChecked.booleanToString()
                nonCreditIndicator = (!electronicNoThanksCheckBoxView.isChecked).booleanToString()
                creditEmailIndicator = creditEmailCheckBoxView.isChecked.booleanToString()
                creditSmsIndicator = creditSmsCheckBoxView.isChecked.booleanToString()
                creditTeleIndicator = creditTelephoneCheckBoxView.isChecked.booleanToString()
                creditIndicator = (!creditNoThanksCheckBoxView.isChecked).booleanToString()
            }
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EditMarketingConsentScreen_ContinueButtonClicked")
            findNavController().navigate(R.id.action_manageProfileEditMarketingConsentFragment_to_manageProfileMarketingConsentConfirmationFragment)
        }
    }

    private fun hasCheckChanged(): Boolean {
        return manageProfileViewModel.manageProfileFlow == ManageProfileFlow.MISSING_MARKET_CONSENT ||
                electronicEmailCheckBoxView.isChecked != marketingIndicator.nonCreditEmailIndicator.stringToBoolean() ||
                electronicSmsCheckBoxView.isChecked != marketingIndicator.nonCreditSmsIndicator.stringToBoolean() ||
                electronicVoiceRecordingCheckBoxView.isChecked != marketingIndicator.nonCreditAutoVoiceIndicator.stringToBoolean() ||
                electronicNoThanksCheckBoxView.isChecked == marketingIndicator.nonCreditIndicator.stringToBoolean() ||
                creditEmailCheckBoxView.isChecked != marketingIndicator.creditEmailIndicator.stringToBoolean() ||
                creditSmsCheckBoxView.isChecked != marketingIndicator.creditSmsIndicator.stringToBoolean() ||
                creditTelephoneCheckBoxView.isChecked != marketingIndicator.creditTeleIndicator.stringToBoolean() ||
                creditNoThanksCheckBoxView.isChecked == marketingIndicator.creditIndicator.stringToBoolean()
    }

    private fun String.stringToBoolean(): Boolean {
        return this.equals(YES, ignoreCase = true)
    }

    private fun Boolean.booleanToString(): String {
        return if (this) YES else NO
    }

    private fun enableContinueButton() {
        if ((!electronicEmailCheckBoxView.isChecked && !electronicSmsCheckBoxView.isChecked && !electronicVoiceRecordingCheckBoxView.isChecked && !electronicNoThanksCheckBoxView.isChecked) || (!creditEmailCheckBoxView.isChecked && !creditSmsCheckBoxView.isChecked && !creditTelephoneCheckBoxView.isChecked && !creditNoThanksCheckBoxView.isChecked)) {
            continueButton.isEnabled = false
        } else {
            continueButton.isEnabled = hasCheckChanged()
        }
    }
}