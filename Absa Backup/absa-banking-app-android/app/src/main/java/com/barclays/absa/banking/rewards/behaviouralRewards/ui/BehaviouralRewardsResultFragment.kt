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
 */package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.bold
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.getProgressOfChallenge.BehaviouralRewardsProgressOfChallengeViewModel
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_result_screen_fragment.*

class BehaviouralRewardsResultFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_result_screen_fragment), OnBackPressedInterface {

    private lateinit var challenge: Challenge
    private lateinit var behaviouralRewardsProgressOfChallengeViewModel: BehaviouralRewardsProgressOfChallengeViewModel
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behaviouralRewardsProgressOfChallengeViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideToolBar()

        arguments?.let {
            challenge = BehaviouralRewardsResultFragmentArgs.fromBundle(it).behaviouralRewardsChallenge

            val descriptionText = BehaviouralRewardsResultFragmentArgs.fromBundle(it).descriptionText
            val textToMakeBold = getString(R.string.behavioural_rewards_success_you_have_five_days_description)
            contentDescriptionView.setDescription(boldText(descriptionText, textToMakeBold))
        }

        claimVoucherButton.setOnClickListener {
            checkProgressAndNavigate()
        }

        if (challenge.challengeId == BehaviourRewardsChallengesType.MARKETING_CONSENT.key) {
            trackAnalytics("UpdateMarketingConsentResult_ScreenDisplayed")
        } else if (challenge.challengeId == BehaviourRewardsChallengesType.UPDATE_DETAILS.key) {
            trackAnalytics("UpdatePersonalDetailsResult_ScreenDisplayed")
        }
    }

    @Suppress("SameParameterValue")
    private fun boldText(text: String, textToBold: String): SpannableStringBuilder {
        val lastIndexOf = text.lastIndexOf(textToBold)
        return SpannableStringBuilder().append(text.substring(0, lastIndexOf)).bold { append(textToBold) }.append(text.substring(lastIndexOf + textToBold.length))
    }

    private fun checkProgressAndNavigate() {
        rewardsCacheService.clearBehaviouralRewardsChallenges()
        val topMostActivity = BMBApplication.getInstance().topMostActivity as BaseActivity

        behaviouralRewardsProgressOfChallengeViewModel.failureLiveData.observe(topMostActivity, {
            dismissProgressDialog()
            findNavController().navigate(R.id.behaviouralRewardsHubFragment)
        })

        behaviouralRewardsProgressOfChallengeViewModel.fetchProgressOfChallenge(challenge.challengeId)
        behaviouralRewardsProgressOfChallengeViewModel.progressOfChallengeResponseLiveData.observe(topMostActivity, {
            dismissProgressDialog()
            behaviouralRewardsActivity.resetAppBarState()
            if (BehaviouralRewardsChallengesFragment.OFFER_AVAILABLE == it.challenge.customerChallengeStatus.voucherAllocationStatus) {
                findNavController().navigate(R.id.behaviouralRewardsClaimRewardFragment, Bundle().apply { putParcelable(BehaviouralRewardsClaimRewardFragment.CHALLENGE_BUNDLE, challenge) })
            } else {
                findNavController().navigate(R.id.behaviouralRewardsHubFragment)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}