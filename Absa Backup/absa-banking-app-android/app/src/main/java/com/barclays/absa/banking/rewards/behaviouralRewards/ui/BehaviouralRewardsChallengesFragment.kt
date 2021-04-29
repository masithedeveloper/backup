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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.View.VISIBLE
import androidx.core.text.bold
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.customerVouchers.dto.CustomerVouchersResponse
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.dto.BehaviouralRewardsAllChallengesResponse
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DASHED_DATETIME_PATTERN
import kotlinx.android.synthetic.main.behavioural_rewards_challenges_fragment.*

class BehaviouralRewardsChallengesFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_challenges_fragment) {

    private var termOfUseAccepted: Boolean = false
    private var buttonTaggingStatus = "ViewChallenge"
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        const val OFFER_AVAILABLE = "AVAILABLE"

        fun newInstance() = BehaviouralRewardsChallengesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.value == null) {
            fetchVoucherMetaData()
        } else {
            fetchAllChallenges()
        }

        termsAndConditionsOptionActionButtonView.setOnClickListener {
            trackAnalytics("RewardsHub_TermsAndConditionsClicked")
            navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsChallengeTermsAndConditionsFragment(Challenge()))
        }
    }

    private fun fetchVoucherMetaData() {
        fetchVoucherPartnerMetaDataViewModel.failureLiveData.observe(viewLifecycleOwner, {
            fetchAllChallenges()
        })

        fetchVoucherPartnerMetaDataViewModel.fetchVoucherPartnersMetaData()
        fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.observe(viewLifecycleOwner, {
            fetchAllChallenges()
        })
    }

    private fun fetchAllChallenges() {

        fetchAllChallengesViewModel.failureLiveData.observe(viewLifecycleOwner, {
            termsAndConditionsOptionActionButtonView.visibility = VISIBLE
            noticeTextView.visibility = VISIBLE
            emptyStateCardView.visibility = VISIBLE
            fetchCustomerVouchers()
        })

        if (rewardsCacheService.getBehaviouralRewardsChallenges() == null) {
            fetchAllChallengesViewModel.fetchAllChallenges()

            fetchAllChallengesViewModel.allChallengesLiveData.observe(viewLifecycleOwner, {
                rewardsCacheService.setBehaviouralRewardsChallenges(it)
                handleChallengesResponse(it)
                fetchCustomerVouchers()
            })
        } else {
            rewardsCacheService.getBehaviouralRewardsChallenges()?.let { handleChallengesResponse(it) }
            fetchCustomerVouchers()
        }
    }

    private fun handleChallengesResponse(behaviouralRewardsAllChallengesResponse: BehaviouralRewardsAllChallengesResponse) {
        termsAndConditionsOptionActionButtonView.visibility = VISIBLE
        if (behaviouralRewardsAllChallengesResponse.challenges.isEmpty()) {
            noticeTextView.visibility = VISIBLE
            emptyStateCardView.visibility = VISIBLE
            return
        }

        val claimVoucherAvailable = behaviouralRewardsAllChallengesResponse.challenges.firstOrNull { challenge -> (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.COMPLETE.key || challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.EXPIRED.key) && OFFER_AVAILABLE.equals(challenge.customerChallengeStatus.voucherAllocationStatus, ignoreCase = true) }
        val newOrExistingChallenge = behaviouralRewardsAllChallengesResponse.challenges.firstOrNull { challenge -> challenge.active && (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key || challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.ACCEPTED.key || challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.IN_PROGRESS.key) }

        when {
            claimVoucherAvailable != null -> {
                buttonTaggingStatus = "ClaimVoucherClicked"
                buildRewardsCompletedBanner(claimVoucherAvailable.challengeId, DateUtils.getFormattedDate(claimVoucherAvailable.customerChallengeStatus.customerChallengeEndDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN_FULL))
                setUpOnBannerClickListener(claimVoucherAvailable)
            }
            newOrExistingChallenge != null -> {
                termOfUseAccepted = newOrExistingChallenge.customerChallengeStatus.status != BehaviourRewardsChallengesStatus.NEW.key
                showVoucherBanner(newOrExistingChallenge)
                setUpOnBannerClickListener(newOrExistingChallenge)
            }
            else -> {
                noticeTextView.visibility = VISIBLE
                emptyStateCardView.visibility = VISIBLE
            }
        }
    }

    private fun fetchCustomerVouchers() {
        customerVoucherViewModel.fetchCustomerVouchers()
        customerVoucherViewModel.customerVoucherResponseLiveData.observe(viewLifecycleOwner, { customerVouchersResponse ->
            if (customerVouchersResponse.customerVoucherHistory.isNotEmpty()) {
                dividerView.visibility = VISIBLE
                yourRewardsTextView.visibility = VISIBLE
                vouchersRecyclerView.visibility = VISIBLE
                vouchersRecyclerView.adapter = BehaviouralRewardsVoucherAdapter(buildVoucherList(customerVouchersResponse), object : RewardsVoucherClick {
                    override fun onRewardsVoucherClicked(voucher: CustomerHistoryVoucher) {
                        navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsVoucherDetailsFragment(voucher))
                    }
                })
            }

            dismissProgressDialog()
        })

        customerVoucherViewModel.failureLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })
    }

    private fun setUpOnBannerClickListener(challenge: Challenge) {
        voucherButton.setOnClickListener {
            if (fetchAvailableVouchersViewModel.availableVouchersResponseLiveData.value == null) {
                fetchAvailableVouchersViewModel.fetchAvailableVouchers()
                fetchAvailableVouchersViewModel.availableVouchersResponseLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    continueWithNavigation(challenge)
                })
            } else {
                continueWithNavigation(challenge)
            }
        }
    }

    private fun continueWithNavigation(challenge: Challenge) {
        when (challenge.customerChallengeStatus.status) {
            BehaviourRewardsChallengesStatus.COMPLETE.key -> navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsClaimRewardFragment(challenge))
            else -> navigateToChallengeDetailScreen(challenge)
        }
        trackAnalytics("RewardsHub_$buttonTaggingStatus")
    }

    private fun navigateToChallengeDetailScreen(challenge: Challenge) {
        if (termOfUseAccepted) {
            navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsChallengeDetailsFragment(challenge))
        } else {
            navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsChallengeTermsAndConditionsFragment(challenge))
        }
    }

    private fun showVoucherBanner(challenge: Challenge) {
        noticeTextView.visibility = VISIBLE
        val bannerButtonText: Int
        if (challenge.customerChallengeStatus.status == BehaviourRewardsChallengesStatus.NEW.key) {
            bannerButtonText = R.string.behavioural_rewards_challenges_accept_challenge
            noticeTextView.text = getString(R.string.behavioural_rewards_challenges_new_notice_text)
            buttonTaggingStatus = "AcceptChallengeClicked"
        } else {
            bannerButtonText = R.string.behavioural_rewards_view_challenge
            noticeTextView.text = getString(R.string.behavioural_rewards_challenges_in_progress_notice_text)
            buttonTaggingStatus = "ViewChallengeClicked"
        }
        val challengeEndDate = DateUtils.getFormattedDate(challenge.challengeEndDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN_FULL)

        when (challenge.challengeId) {
            BehaviourRewardsChallengesType.MARKETING_CONSENT.key -> buildRewardsChallenge(ChallengeBannerItem("marketing_consent.json", R.string.behavioural_rewards_marketing_consent_challenge_banner_title, description = R.string.behavioural_rewards_challenges_marketing_consent_banner_description, buttonText = bannerButtonText, endDate = challengeEndDate))
            BehaviourRewardsChallengesType.UPDATE_DETAILS.key -> buildRewardsChallenge(ChallengeBannerItem("update_details.json", R.string.behavioural_rewards_update_details_challenge_banner_title, description = R.string.behavioural_rewards_challenges_update_details_banner_description, buttonText = bannerButtonText, endDate = challengeEndDate))
            BehaviourRewardsChallengesType.ONLINE_SPEND.key -> buildRewardsChallenge(ChallengeBannerItem("online_spend.json", R.string.behavioural_rewards_shop_online_challenge_banner_title, description = R.string.behavioural_rewards_challenges_spend_online_banner_description, buttonText = bannerButtonText, endDate = challengeEndDate))
            BehaviourRewardsChallengesType.CASHSEND.key -> buildRewardsChallenge(ChallengeBannerItem("cash_send.json", R.string.behavioural_rewards_cashsend_challenge_banner_title, description = R.string.behavioural_rewards_challenges_cash_send_banner_description, buttonText = bannerButtonText, endDate = challengeEndDate))
            BehaviourRewardsChallengesType.POINT_OF_SALES.key -> buildRewardsChallenge(ChallengeBannerItem("point_of_sale.json", R.string.behavioural_rewards_partner_store_challenge_banner_title, description = R.string.behavioural_rewards_challenges_show_at_banner_description, buttonText = bannerButtonText, endDate = challengeEndDate))
            else -> emptyStateCardView.visibility = VISIBLE
        }
    }

    private fun buildRewardsChallenge(challengeItem: ChallengeBannerItem) {
        rewardsVoucherCardView.visibility = VISIBLE

        with(challengeItem) {
            rewardsBannerLottoAnimationView.setAnimation("behavioural_rewards/${challengeItem.bannerIcon}")
            voucherOfferTitleTextView.text = getString(title)
            val descriptionText = getString(description, endDate)
            voucherOfferContentTextView.text = if (challengeItem.isComplete) boldText(descriptionText, challengeItem.endDate) else boldText(descriptionText, getString(R.string.behavioural_rewards_meal_voucher_bold_text))
            voucherButton.text = getString(buttonText)
        }
    }

    private fun buildRewardsCompletedBanner(challengesType: String, voucherEndDate: String) {
        noticeTextView.visibility = VISIBLE
        noticeTextView.text = getString(R.string.behavioural_rewards_challenges_complete_notice_text)
        when (challengesType) {
            BehaviourRewardsChallengesType.ONLINE_SPEND.key, BehaviourRewardsChallengesType.POINT_OF_SALES.key, BehaviourRewardsChallengesType.CASHSEND.key -> buildRewardsChallenge(ChallengeBannerItem("transactional_challenge_completed.json", R.string.behavioural_rewards_challenge_choose_voucher, R.string.behavioural_rewards_challenge_marketing_consent_complete, R.string.behavioural_rewards_challenge_claim_voucher, voucherEndDate, true))
            else -> buildRewardsChallenge(ChallengeBannerItem("non_transactional_challenge_completed.json", R.string.behavioural_rewards_challenge_choose_voucher, R.string.behavioural_rewards_challenge_marketing_consent_complete, R.string.behavioural_rewards_challenge_claim_voucher, voucherEndDate, true))
        }
    }

    @Suppress("SameParameterValue")
    private fun boldText(text: String, textToBold: String): SpannableStringBuilder {
        val lastIndexOf = text.lastIndexOf(textToBold)
        return if (lastIndexOf > 0) SpannableStringBuilder().append(text.substring(0, lastIndexOf)).bold { append(textToBold) }.append(text.substring(lastIndexOf + textToBold.length)) else SpannableStringBuilder(text)
    }

    private fun buildVoucherList(customerVouchersResponse: CustomerVouchersResponse): ArrayList<CustomerHistoryVoucher> {
        val voucherPartnerMetaData = fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.value?.voucherPartnerMetaData

        val vouchers: ArrayList<CustomerHistoryVoucher> = arrayListOf()
        customerVouchersResponse.customerVoucherHistory.forEach { customerVoucherHistory ->
            customerVoucherHistory.vouchers.forEach { voucher ->
                vouchers.add(CustomerHistoryVoucher().apply {
                    partnerId = customerVoucherHistory.partnerId
                    offerDescription = voucher.offerDescription
                    offerExpiryDateTime = voucher.offerExpiryDateTime
                    rewardPinVoucher = voucher.rewardPinVoucher
                    voucherImage = voucherPartnerMetaData?.find { it.partnerId == partnerId }?.partnerImage ?: ""
                    redemptionDate = DateUtils.getDate(voucher.redemptionDateTime, DASHED_DATETIME_PATTERN)
                    offerStatus = voucher.offerStatus
                    termsAndConditions = voucherPartnerMetaData?.find { it.partnerId == partnerId }?.partnerTermsAndConditions ?: ""
                })
            }
        }

        vouchers.sortByDescending { it.redemptionDate }
        return vouchers
    }
}

data class ChallengeBannerItem(val bannerIcon: String = "", val title: Int, val description: Int = -1, val buttonText: Int = R.string.behavioural_rewards_challenges_accept_challenge, val endDate: String = "", val isComplete: Boolean = false)