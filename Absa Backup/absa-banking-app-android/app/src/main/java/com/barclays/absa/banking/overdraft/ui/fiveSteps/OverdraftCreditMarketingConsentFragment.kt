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
package com.barclays.absa.banking.overdraft.ui.fiveSteps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.OverdraftCreditMarketingConsentFragmentBinding
import com.barclays.absa.banking.directMarketing.DirectMarketingViewModel
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicatorResponse
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.overdraft.ui.OverdraftBaseFragment
import com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class OverdraftCreditMarketingConsentFragment : OverdraftBaseFragment<OverdraftCreditMarketingConsentFragmentBinding>() {

    private lateinit var viewModel: DirectMarketingViewModel
    private lateinit var marketingIndicators: MarketingIndicators
    private val creditMarketingConsentOptions = SelectorList<StringItem>()
    private var checkMarketingConsent: Boolean = false

    companion object {
        const val DIRECT_MARKETING_INDICATORS = "DIRECT_MARKETING_INDICATORS"
        const val YES = "Y"
        const val NO = "N"

        fun newInstance(marketingIndicatorResponse: MarketingIndicatorResponse): OverdraftCreditMarketingConsentFragment {
            val fragment = OverdraftCreditMarketingConsentFragment()
            val bundle = Bundle()
            bundle.putParcelable(DIRECT_MARKETING_INDICATORS, marketingIndicatorResponse.marketingIndicator)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.overdraft_credit_marketing_consent_fragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.credit_marketing_consent)) { parentActivity.onBackPressed() }

        arguments?.getParcelable<MarketingIndicators?>(DIRECT_MARKETING_INDICATORS)?.let {
            marketingIndicators = it
            initializeView()
        }

        binding.nextButton.setOnClickListener { navigateToNextScreen() }
        AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_ScreenDisplayed")
    }

    private fun initializeView() {
        creditMarketingConsentOptions.add(StringItem(getString(R.string.yes)))
        creditMarketingConsentOptions.add(StringItem(getString(R.string.no)))
        binding.creditMarketingConsentOptionsRadioGroup.setDataSource(creditMarketingConsentOptions, -1)

        binding.creditMarketingConsentOptionsRadioGroup.setItemCheckedInterface { index ->
            if (index == 0) {
                checkMarketingConsent = true
                binding.marketingCommunicationLayout.visibility = View.VISIBLE
                marketingIndicators.creditIndicator = YES
                selectDirectMarketingChannels()
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_YesRadioButtonSelected")
            }
            if (index == 1) {
                checkMarketingConsent = false
                binding.marketingCommunicationLayout.visibility = View.GONE
                marketingIndicators.creditIndicator = NO
                marketingIndicators.creditTeleIndicator = NO
                marketingIndicators.creditEmailIndicator = NO
                marketingIndicators.creditSmsIndicator = NO
                marketingIndicators.creditAutoVoiceIndicator = NO
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_NoRadioButtonSelected")
            }
        }
    }

    fun navigateToNextScreen() {
        if (binding.creditMarketingConsentOptionsRadioGroup.selectedValue == null) {
            animate(binding.creditMarketingConsentOptionsRadioGroup, R.anim.shake)
            return
        }
        if (checkMarketingConsent && (!binding.telephoneCheckbox.isChecked && !binding.emailCheckbox.isChecked && !binding.smsCheckbox.isChecked)) {
            animate(binding.marketingCommunicationLayout, R.anim.shake)
        } else
            if (checkMarketingConsent && (binding.telephoneCheckbox.isChecked
                            || binding.smsCheckbox.isChecked
                            || binding.emailCheckbox.isChecked)) {
                viewModel.updateMarketingIndicators(marketingIndicators)
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_OverdraftApproved")
                startActivity(IntentFactory.getSuccessfulResultScreen(activity, R.string.overdraft_approved,
                        R.string.overdraft_will_be_available_in_your_absa, true))
            }

        if (!checkMarketingConsent) {
            viewModel.updateMarketingIndicators(marketingIndicators)
            AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_OverdraftApproved")
            startActivity(IntentFactory.getSuccessfulResultScreen(activity, R.string.overdraft_approved,
                    R.string.overdraft_will_be_available_in_your_absa, true))
        }
    }

    private fun selectDirectMarketingChannels() {
        binding.emailCheckbox.setOnCheckedListener { isChecked ->
            marketingIndicators.creditEmailIndicator = if (isChecked) YES else NO
            if (isChecked) {
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_EmailCheckBoxChecked")
            }
        }
        binding.smsCheckbox.setOnCheckedListener { isChecked ->
            marketingIndicators.creditSmsIndicator = if (isChecked) YES else NO
            if (isChecked) {
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_SMSCheckBoxChecked")
            }
        }
        binding.telephoneCheckbox.setOnCheckedListener { isChecked ->
            marketingIndicators.creditTeleIndicator = if (isChecked) YES else NO
            if (isChecked) {
                AnalyticsUtil.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_TelephoneCheckBoxChecked")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModel()
    }
}