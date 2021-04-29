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

import android.os.Bundle
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.rewards.behaviouralRewards.models.BehaviouralRewardsCarouselModel
import kotlinx.android.synthetic.main.behavioural_rewards_hub_header_fragment.*

class BehaviouralRewardsHeaderFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_hub_header_fragment), OnClickInterface {

    private lateinit var primaryAction: () -> Unit
    private lateinit var secondaryAction: () -> Unit
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateHeader()
    }

    fun updateHeader() {
        val tierLevel = rewardsCacheService.getMembershipTier()
        val carouselItems = ArrayList<BehaviouralRewardsCarouselModel>()

        val rewardsAccount = rewardsCacheService.getRewardsAccount()
        if (rewardsAccount != null) {
            val rewardsMembershipInformation = BehaviouralRewardsCarouselModel().apply {
                title = rewardsAccount.currentBalance.toString()
                if (tierLevel != null) {
                    description = getString(R.string.behavioural_rewards_available_cash, tierLevel)
                }
                actions = arrayListOf(getString(R.string.behavioural_rewards_spend), getString(R.string.behavioural_rewards_earn))
                imageResource = arrayListOf(R.drawable.ic_pay_light, R.drawable.ic_buy_hub_shopping)
                primaryAction = { navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsSpendFragment()) }
                secondaryAction = { navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToRewardsEarnRatesFragment()) }
            }

            carouselItems.add(rewardsMembershipInformation)
        } else {
            val rewardsMembershipInformation = BehaviouralRewardsCarouselModel().apply {
                title = getString(R.string.behavioural_rewards_hub_join_rewards_program)
                description = getString(R.string.behavioural_rewards_hub_miss_out)
                actions = arrayListOf(getString(R.string.behavioural_rewards_hub_see_more))
                primaryAction = { navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsWaysToApplyFragment()) }
            }

            carouselItems.add(rewardsMembershipInformation)
        }
        val adapter = BehaviouralRewardsCashBackPagerAdapter(carouselItems, this@BehaviouralRewardsHeaderFragment)

        with(behaviouralRewardsCashBackViewPager) {
            this.adapter = adapter
            offscreenPageLimit = 3

            val marginStartEnd = resources.getDimensionPixelSize(R.dimen.view_pager_padding)
            setPadding(marginStartEnd, 0, marginStartEnd, 0)
            setPageTransformer(MarginPageTransformer(resources.getDimensionPixelSize(R.dimen.extra_large_space)))
        }
    }

    inner class BehaviouralRewardsCashBackPagerAdapter(private val behaviouralRewardsMockCarouselModels: List<BehaviouralRewardsCarouselModel>, fragment: BehaviouralRewardsHeaderFragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = BehaviouralRewardsCashBackItemFragment.newInstance(behaviouralRewardsMockCarouselModels[position])
        override fun getItemCount() = behaviouralRewardsMockCarouselModels.size
    }

    override fun primaryOnClick() {
        primaryAction.invoke()
    }

    override fun secondaryOnClick() {
        secondaryAction.invoke()
    }
}