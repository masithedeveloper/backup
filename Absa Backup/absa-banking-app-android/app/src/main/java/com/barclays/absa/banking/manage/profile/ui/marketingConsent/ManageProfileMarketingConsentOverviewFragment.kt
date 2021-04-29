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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.services.dto.MarketingIndicator
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_marketing_consent_overview_fragment.*

class ManageProfileMarketingConsentOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_marketing_consent_overview_fragment) {
    private lateinit var marketingIndicator: MarketingIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_hub_marketing_consent_title)
        initData()
        initOnClickListeners()
    }

    private fun initData() {
        marketingIndicator = manageProfileViewModel.customerInformation.value?.customerInformation?.marketingIndicator ?: MarketingIndicator()

        if (YES.equals(marketingIndicator.nonCreditIndicator, true)) {
            manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditIndicator = YES
            electronicPreferredCommunicationMethodContentAndLabelView.setContentText(preferredCommunicationMethod(marketingIndicator.nonCreditEmailIndicator, marketingIndicator.nonCreditSmsIndicator, marketingIndicator.nonCreditAutoVoiceIndicator, ""))
        } else {
            manageProfileViewModel.marketingConsentDetailsToUpdate.nonCreditIndicator = NO
            electronicPreferredCommunicationMethodContentAndLabelView.setContentText(getString(R.string.manage_profile_marketing_consent_preferred_communication_method_none))
        }

        if (YES.equals(marketingIndicator.creditIndicator, true)) {
            manageProfileViewModel.marketingConsentDetailsToUpdate.creditIndicator = YES
            creditPreferredCommunicationMethodContentAndLabelView.setContentText(preferredCommunicationMethod(marketingIndicator.creditEmailIndicator, marketingIndicator.creditSmsIndicator, "", marketingIndicator.creditTeleIndicator))
        } else {
            manageProfileViewModel.marketingConsentDetailsToUpdate.creditIndicator = NO
            creditPreferredCommunicationMethodContentAndLabelView.setContentText(getString(R.string.manage_profile_marketing_consent_preferred_communication_method_none))
        }

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
    }

    private fun preferredCommunicationMethod(email: String, sms: String, voiceRecording: String, telephone: String): String {
        val stringBuilder = StringBuilder()
        if (YES.equals(email, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_email)).appendLine()
        }
        if (YES.equals(sms, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_sms)).appendLine()
        }
        if (YES.equals(voiceRecording, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_voice_recording)).appendLine()
        }
        if (YES.equals(telephone, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_telephone)).appendLine()
        }
        return stringBuilder.toString().substringBeforeLast('\n')
    }

    private fun initOnClickListeners() {
        marketingConsentActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_MarketingConsentSummaryScreen_EditMarketingConsentDetailsButtonClicked")
            navigate(ManageProfileMarketingConsentOverviewFragmentDirections.actionManageProfileMarketingConsentOverviewFragmentToManageProfileEditMarketingConsentFragment())
        }
    }
}