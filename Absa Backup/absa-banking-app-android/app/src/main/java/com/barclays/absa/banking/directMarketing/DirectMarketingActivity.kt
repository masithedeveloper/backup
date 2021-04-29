/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.directMarketing

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.activity_direct_marketing.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class DirectMarketingActivity : BaseActivity(R.layout.activity_direct_marketing) {

    private val YES = "Y"
    private val NO = "N"

    private lateinit var marketingIndicators: MarketingIndicators
    private lateinit var viewModel: DirectMarketingViewModel
    private var marketingConsentOptions = SelectorList<StringItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.stay_in_touch)
        AnalyticsUtil.trackAction("Direct Marketing", "Direct Marketing Stay In Touch Screen")

        viewModel = viewModel()
        marketingIndicators = intent.getParcelableExtra("DIRECT_MARKETING_INDICATORS") ?: MarketingIndicators()
        initiateViews()
    }

    private fun initiateViews() {
        marketingConsentOptions.add(StringItem(getString(R.string.yes)))
        marketingConsentOptions.add(StringItem(getString(R.string.no)))
        creditMarketingConsentOptionsRadioGroup.setDataSource(marketingConsentOptions, -1)
        creditMarketingConsentOptionsRadioGroup.setItemCheckedInterface { index ->
            creditMarketingConsentOptionsRadioGroup.hideError()
            if (index == 0) {
                marketingCommunicationLayout.visibility = View.GONE
                yesToAllDirectMarketingChannels()
            }
            if (index == 1) {
                marketingCommunicationLayout.visibility = View.VISIBLE
                selectDirectMarketingChannels()
            }
        }
        nextButton.setOnClickListener { validateFields() }
    }

    private fun yesToAllDirectMarketingChannels() {
        marketingIndicators.apply {
            nonCreditIndicator = YES
            nonCreditAutoVoiceIndicator = YES
            nonCreditEmailIndicator = YES
            nonCreditSmsIndicator = YES
        }
    }

    private fun selectDirectMarketingChannels() {
        emailCheckbox.setOnCheckedListener { isChecked ->
            if (isChecked) {
                noThanksCheckbox.isChecked = false
            }
            marketingIndicators.nonCreditEmailIndicator = if (isChecked) YES else NO
        }
        smsCheckbox.setOnCheckedListener { isChecked ->
            if (isChecked) {
                noThanksCheckbox.isChecked = false
            }
            marketingIndicators.nonCreditSmsIndicator = if (isChecked) YES else NO
        }
        voiceRecordingCheckbox.setOnCheckedListener { isChecked ->
            if (isChecked) {
                noThanksCheckbox.isChecked = false
            }
            marketingIndicators.nonCreditAutoVoiceIndicator = if (isChecked) YES else NO
        }
        noThanksCheckbox.setOnCheckedListener { isChecked ->
            if (isChecked) {
                clearSelection()
                marketingIndicators.nonCreditIndicator = NO
                marketingIndicators.nonCreditAutoVoiceIndicator = NO
                marketingIndicators.nonCreditEmailIndicator = NO
                marketingIndicators.nonCreditSmsIndicator = NO
            }
        }
    }

    private fun clearSelection() {
            smsCheckbox.isChecked = false
            emailCheckbox.isChecked = false
            voiceRecordingCheckbox.isChecked = false
    }

    private fun validateFields() {
        if (creditMarketingConsentOptionsRadioGroup.selectedValue == null) {
            animate(creditMarketingConsentOptionsRadioGroup, R.anim.shake)
            creditMarketingConsentOptionsRadioGroup.setErrorMessage(getString(R.string.overdraft_select_option))
        } else if (creditMarketingConsentOptionsRadioGroup.selectedIndex == 1 && (!noThanksCheckbox.isChecked &&
                        !emailCheckbox.isChecked && !smsCheckbox.isChecked && !voiceRecordingCheckbox.isChecked)) {
            animate(marketingCommunicationLayout, R.anim.shake)
        } else {
            AnalyticsUtil.trackAction("Direct Marketing Next Button")
            viewModel.updateMarketingIndicators(marketingIndicators)
            viewModel.marketingIndicatorResponse.observe(this, {
                dismissProgressDialog()
                if (BMBConstants.SUCCESS.equals(it?.transactionStatus, true)) {
                    startActivity(IntentFactory.getSuccessfulResultScreen(this, getString(R.string.your_details_have_been_updated), ""))
                } else if (BMBConstants.FAILURE.equals(it?.transactionStatus, true)) {
                    startActivity(IntentFactory.getFailureResultScreen(this, R.string.unable_to_save_changes, getString(R.string.unable_to_save_changes_description)))
                }
            })
        }
    }
}