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
 */

package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.acceptChallenge.BehaviouralRewardsAcceptChallengeViewModel
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviourRewardsChallengesType.*
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_challenge_details_fragment.*
import styleguide.utils.extensions.toTitleCase

class BehaviouralRewardsChallengeDetailsFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_challenge_details_fragment), OnBackPressedInterface {
    private var challenge: Challenge = Challenge()
    private lateinit var acceptChallengeViewModel: BehaviouralRewardsAcceptChallengeViewModel
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        const val REWARDS_PARTNER_URL = "https://www.absa.co.za/personal/bank/absa-rewards/cash-rewards-from-partners/"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        acceptChallengeViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpToolbar()
        arguments?.let {
            challenge = BehaviouralRewardsChallengeDetailsFragmentArgs.fromBundle(it).behaviouralRewardsChallenge
        }

        if (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key) {
            acceptChallenge()
        }
        hideToolBar()
        behaviouralRewardsActivity.hideRewardsToolBarSeparator()

        populateComponents()
        setUpButtons()

        trackAnalytics("ChallengeDetails_ScreenDisplayed")

        when (challenge.challengeId) {
            MARKETING_CONSENT.key -> trackAnalytics("StayAhead_ScreenDisplayed")
            UPDATE_DETAILS.key -> trackAnalytics("StayConnected_ScreenDisplayed")
            ONLINE_SPEND.key -> trackAnalytics("ShopOnline_ScreenDisplayed")
            POINT_OF_SALES.key -> trackAnalytics("ShopSmarter_ScreenDisplayed")
            CASHSEND.key -> trackAnalytics("GoDigital_ScreenDisplayed")
        }
    }

    private fun acceptChallenge() {
        acceptChallengeViewModel.failureLiveData.observe(viewLifecycleOwner, {
            navigate(BehaviouralRewardsChallengeDetailsFragmentDirections.actionBehaviouralRewardsChallengeDetailsFragmentToBehaviouralRewardsHubFragment())
            dismissProgressDialog()
        })

        acceptChallengeViewModel.acceptChallenge(challenge.challengeId)
        acceptChallengeViewModel.acceptChallengeResponseLiveData.observe(viewLifecycleOwner, {
            rewardsCacheService.clearBehaviouralRewardsChallenges()
            dismissProgressDialog()
        })
    }

    private fun setUpToolbar() {
        fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = VISIBLE
            title = getString(R.string.behavioural_rewards_challenge_toolbar_title).toTitleCase()
            setNavigationOnClickListener {
                behaviouralRewardsActivity.onBackPressed()
            }
        }
    }

    private fun populateComponents() {
        when (challenge.challengeId) {
            MARKETING_CONSENT.key -> setUpLayout(R.drawable.ic_rewards_challenge_marketing_consent_header, R.string.behavioural_rewards_challenge_stay_ahead, R.string.behavioural_rewards_challenge_marketing_consent_description, R.string.behavioural_rewards_challenge_marketing_consent_what_is_it_description, false)
            UPDATE_DETAILS.key -> setUpLayout(R.drawable.ic_rewards_challenge_update_details_header, R.string.behavioural_rewards_stay_connected, R.string.behavioural_rewards_challenge_view_details_description, R.string.behavioural_rewards_challenge_view_details_why_description, false)
            ONLINE_SPEND.key -> setUpLayout(R.drawable.ic_rewards_challenge_online_spend_header, R.string.behavioural_rewards_shop_online, R.string.behavioural_rewards_challenge_online_store_description, R.string.behavioural_rewards_challenge_online_spend_how_it_works_description, true)
            CASHSEND.key -> setUpLayout(R.drawable.ic_rewards_challenge_cashsend_header, R.string.behavioural_rewards_cash_send_title, R.string.behavioural_rewards_challenge_cashsend_description, R.string.behavioural_rewards_cash_send_stub_description, true)
            POINT_OF_SALES.key -> setUpLayout(R.drawable.ic_rewards_challenge_point_of_sale_header, R.string.behavioural_rewards_point_of_sale_title, R.string.behavioural_rewards_challenge_point_of_sale_description, R.string.behavioural_rewards_challenge_point_of_sale_in_progress_description, true)
        }

        val challengeEndDate = DateUtils.formatDate(challenge.challengeEndDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.SLASHED_DATE_PATTERN)
        challengeEndTextView.text = getString(R.string.behavioural_rewards_challenges_ends, challengeEndDate)
    }

    private fun setUpLayout(@DrawableRes backgroundResource: Int, @StringRes title: Int, @StringRes description: Int, @StringRes stubDescription: Int, hasProgress: Boolean) {
        progressLinearLayout.visibility = GONE
        challengeDetailsImageView.setImageResource(backgroundResource)
        titleAndDescriptionView.title = getString(title)

        val rewardsPartnerList = getRewardsPartnerList()
        when (challenge.challengeId) {
            POINT_OF_SALES.key -> {
                stubDescriptionTextView.text = getString(stubDescription, rewardsPartnerList)
                titleAndDescriptionView.description = getString(description, rewardsPartnerList, "")
                setPartnerStoreText(getString(stubDescription, rewardsPartnerList))
            }
            else -> {
                stubDescriptionTextView.text = getString(stubDescription)
                titleAndDescriptionView.description = getString(description, rewardsPartnerList)
            }
        }

        if (challenge.customerChallengeStatus.status != BehaviourRewardsChallengesStatus.NEW.key) {
            if (hasProgress) {
                progressLinearLayout.visibility = VISIBLE
                behaviouralProgressView.setProgress(challenge.customerChallengeStatus.progress)
            }

            when (challenge.challengeId) {
                MARKETING_CONSENT.key, UPDATE_DETAILS.key -> {
                    stubHeadingTextView.visibility = GONE
                    stubDescriptionTextView.visibility = GONE
                }
                ONLINE_SPEND.key, CASHSEND.key -> {
                    titleAndDescriptionView.descriptionTextView.visibility = GONE
                    stubHeadingTextView.visibility = GONE
                    stubDescriptionTextView.text = getString(description, rewardsPartnerList)
                }
                POINT_OF_SALES.key -> {
                    titleAndDescriptionView.descriptionTextView.visibility = GONE
                    stubHeadingTextView.visibility = GONE
                    setPartnerStoreText(getString(description, rewardsPartnerList, "\n\n${getString(R.string.behavioural_rewards_shop_smart_view_more)}"))
                }
            }
        }
    }

    private fun setPartnerStoreText(text: String) {
        CommonUtils.makeTextClickable(context, text, getString(R.string.behavioural_rewards_partner_stores), object : ClickableSpan() {
            override fun onClick(view: View) {
                val partnerUrlIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(REWARDS_PARTNER_URL))
                startActivity(partnerUrlIntent)
            }
        }, stubDescriptionTextView, R.color.white)
    }

    private fun getRewardsPartnerList(): String {
        fetchAvailableVouchersViewModel.availableVouchersResponseLiveData.value?.let {
            val rewardsPartnerList = StringBuilder().apply {
                val lastInsertIndex = it.voucherPartners.size - 2
                it.voucherPartners.forEachIndexed { index, voucher ->
                    val partnerName = fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.value?.voucherPartnerMetaData?.find { metaData -> metaData.partnerId == voucher.partnerId }?.partnerName ?: ""
                    if (partnerName.isNotEmpty()) {
                        append(partnerName)
                        if (index <= lastInsertIndex) {
                            if (index == lastInsertIndex) {
                                append(" ").append(getString(R.string.behavioural_rewards_or)).append(" ")
                            } else {
                                append(", ")
                            }
                        }
                    }
                }
            }
            return rewardsPartnerList.toString()
        }
        return ""
    }

    private fun setUpButtons() {
        when (challenge.challengeId) {
            MARKETING_CONSENT.key -> {
                if (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key) {
                    actionButton.text = getString(R.string.behavioural_rewards_challenge_marketing_consent_button_text)
                }
                actionButton.visibility = VISIBLE
            }
            UPDATE_DETAILS.key -> {
                if (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key) {
                    actionButton.text = getString(R.string.behavioural_rewards_challenge_view_details_update)
                }
                actionButton.visibility = VISIBLE
            }
            CASHSEND.key -> {
                if (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key) {
                    actionButton.text = getString(R.string.behavioural_rewards_challenge_make_a_cashsend)
                }
                actionButton.visibility = VISIBLE
            }
            else -> actionButton.visibility = GONE
        }

        actionButton.setOnClickListener {
            when (challenge.challengeId) {
                MARKETING_CONSENT.key -> {
                    trackAnalytics("StayAhead_CountMeInButtonClicked")
                    navigate(BehaviouralRewardsChallengeDetailsFragmentDirections.actionBehaviouralRewardsChallengeDetailsFragmentToBehaviouralRewardsMarketingConsentFragment(challenge))
                }
                UPDATE_DETAILS.key -> {
                    trackAnalytics("StayConnected_UpdatePersonalDetailsButtonClicked")
                    if (appCacheService.isPrimarySecondFactorDevice()) {
                        navigate(BehaviouralRewardsChallengeDetailsFragmentDirections.actionBehaviouralRewardsChallengeDetailsFragmentToBehaviouralRewardsUpdatePersonalDetailsFragment(challenge))
                    } else {
                        showCustomAlertDialog(AlertInfo().apply {
                            title = getString(R.string.behavioural_rewards_requires_primary_title)
                            message = getString(R.string.behavioural_rewards_requires_primary_description)
                            positiveButtonText = getString(R.string.behavioural_rewards_got_it)
                            positiveDismissListener = DialogInterface.OnClickListener { _, _ ->
                                behaviouralRewardsActivity.resetAppBarState()
                                navigate(BehaviouralRewardsChallengeDetailsFragmentDirections.actionBehaviouralRewardsChallengeDetailsFragmentToBehaviouralRewardsHubFragment())
                            }
                        })
                    }
                }
                CASHSEND.key -> {
                    trackAnalytics("GoDigital_SendMoneyViaCashSendButtonClicked")
                    navigateToCashSend()
                }
            }

            trackAnalytics("ChallengeDetails_ContinueButtonClicked")
        }
    }

    private fun navigateToCashSend() {
        startActivity(Intent(activity, CashSendActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(CashSendActivity.IS_CASH_SEND_PLUS, false)
            putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
            putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND)
        })
    }

    override fun onBackPressed(): Boolean {
        navigate(BehaviouralRewardsChallengeDetailsFragmentDirections.actionBehaviouralRewardsChallengeDetailsFragmentToBehaviouralRewardsHubFragment())
        return true
    }
}
