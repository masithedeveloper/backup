/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.getProgressOfChallenge.BehaviouralRewardsProgressOfChallengeViewModel
import com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.dto.BehaviouralRewardsFetchMarketingConsentDetailsRequest
import com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.fetchMarketingConsentDetails.BehaviouralRewardsFetchCurrentMarketingConsentDetailsViewModel
import com.barclays.absa.banking.express.behaviouralRewards.marketingConsentDetails.updateMarketingConsentDetails.BehaviouralRewardsUpdateMarketingConsentDetailsViewModel
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_marketing_consent_fragment.*

class BehaviouralRewardsMarketingConsentFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_marketing_consent_fragment), OnBackPressedInterface {

    private lateinit var challenge: Challenge
    private lateinit var fetchMarketingConsentViewModel: BehaviouralRewardsFetchCurrentMarketingConsentDetailsViewModel
    private lateinit var updateMarketingConsentViewModel: BehaviouralRewardsUpdateMarketingConsentDetailsViewModel
    private lateinit var behaviouralRewardsProgressOfChallengeViewModel: BehaviouralRewardsProgressOfChallengeViewModel
    private var behaviouralRewardsFetchMarketingConsentDetailsRequest = BehaviouralRewardsFetchMarketingConsentDetailsRequest()

    companion object {
        const val CONSENT_ALLOWED = "Y"
        const val CONSENT_DENIED = "N"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fetchMarketingConsentViewModel = viewModel()
        updateMarketingConsentViewModel = viewModel()
        behaviouralRewardsProgressOfChallengeViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_marketing_consent_title))

        arguments?.let {
            challenge = BehaviouralRewardsUpdatePersonalDetailsFragmentArgs.fromBundle(it).behaviouralRewardsChallenge
        }

        fetchMarketingConsentIndicators()
        setUpListeners()
        showToolBar()

        electronicMarketingBulletItemView.setSubTextView(getString(R.string.behavioural_rewards_marketing_consent_electronic_description))
        creditMarketingBulletItemView.setSubTextView(getString(R.string.behavioural_rewards_marketing_consent_credit_description))

        trackAnalytics("UpdateMarketingConsent_ScreenDisplayed")
    }

    private fun fetchMarketingConsentIndicators() {
        fetchMarketingConsentViewModel.fetchCurrentMarketingConsentDetails()
        fetchMarketingConsentViewModel.currentMarketingConsentDetailsResponseLiveData.observe(viewLifecycleOwner, {
            emailCheckBoxView.isChecked = it.creditEmail == CONSENT_ALLOWED
            smsCheckBoxView.isChecked = it.creditSMS == CONSENT_ALLOWED
            behaviouralRewardsFetchMarketingConsentDetailsRequest.apply {
                option = "UPDATE"
                nonCreditIndicator = it.nonCreditIndicator
                nonCreditEmail = it.nonCreditEmail
                nonCreditSMS = it.nonCreditSMS
                creditIndicator = it.creditIndicator
                creditEmail = it.creditEmail
                creditSMS = it.creditSMS
                nonCreditAVoice = it.nonCreditAVoice
                nonCreditPost = it.nonCreditPost
                nonCreditTel = it.nonCreditTel
                creditAVoice = it.creditAVoice
                creditPost = it.creditPost
                creditTel = it.creditTel
            }
            dismissProgressDialog()
        })
    }

    private fun setUpListeners() {
        emailCheckBoxView.setOnCheckedListener {
            behaviouralRewardsFetchMarketingConsentDetailsRequest.creditEmail = if (it) {
                CONSENT_ALLOWED
            } else {
                CONSENT_DENIED
            }
            behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditEmail = behaviouralRewardsFetchMarketingConsentDetailsRequest.creditEmail
            clearCheckBoxViewErrors()
        }

        smsCheckBoxView.setOnCheckedListener {
            behaviouralRewardsFetchMarketingConsentDetailsRequest.creditSMS = if (it) {
                CONSENT_ALLOWED
            } else {
                CONSENT_DENIED
            }
            behaviouralRewardsFetchMarketingConsentDetailsRequest.nonCreditSMS = behaviouralRewardsFetchMarketingConsentDetailsRequest.creditSMS
            clearCheckBoxViewErrors()
        }

        confirmButton.setOnClickListener {
            if (isValidInput()) {
                updateMarketingConsentViewModel.updateMarketingConsentDetails(behaviouralRewardsFetchMarketingConsentDetailsRequest)
                updateMarketingConsentViewModel.currentMarketingConsentDetailsResponseLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    navigate(BehaviouralRewardsMarketingConsentFragmentDirections.actionBehaviouralRewardsMarketingConsentFragmentToBehaviouralRewardsResultFragment(challenge, getString(R.string.behavioural_rewards_marketing_consent_success_description)))
                })
            } else {
                smsCheckBoxView.setErrorMessage(getString(R.string.behavioural_rewards_please_agree_one))
                emailCheckBoxView.setErrorMessage(getString(R.string.behavioural_rewards_please_agree_one))
            }

            trackAnalytics("UpdateMarketingConsent_ConfirmButtonClicked")
        }
    }

    private fun clearCheckBoxViewErrors() {
        emailCheckBoxView.clearError()
        smsCheckBoxView.clearError()
    }

    private fun isValidInput() = emailCheckBoxView.isChecked || smsCheckBoxView.isChecked

    override fun onBackPressed(): Boolean {
        trackAnalytics("UpdateMarketingConsent_BackButtonClicked")
        return false
    }
}