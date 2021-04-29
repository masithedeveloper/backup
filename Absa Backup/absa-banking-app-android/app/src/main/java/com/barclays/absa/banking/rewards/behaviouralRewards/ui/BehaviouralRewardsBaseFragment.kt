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

import android.R.color
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.customerVouchers.BehaviouralRewardsCustomerVoucherViewModel
import com.barclays.absa.banking.express.behaviouralRewards.fetchAllChallenges.BehaviouralRewardsFetchAllChallengesViewModel
import com.barclays.absa.banking.express.behaviouralRewards.fetchAvailableVouchers.BehaviouralRewardsFetchAvailableVouchersViewModel
import com.barclays.absa.banking.express.behaviouralRewards.fetchVoucherPartnerMetaData.BehaviouralRewardsFetchVoucherPartnerMetaDataViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel

abstract class BehaviouralRewardsBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    protected lateinit var behaviouralRewardsActivity: BehaviouralRewardsActivity
    protected lateinit var customerVoucherViewModel: BehaviouralRewardsCustomerVoucherViewModel
    protected lateinit var fetchAllChallengesViewModel: BehaviouralRewardsFetchAllChallengesViewModel
    protected lateinit var rewardsViewModel: RewardsViewModel
    protected lateinit var fetchVoucherPartnerMetaDataViewModel: BehaviouralRewardsFetchVoucherPartnerMetaDataViewModel
    protected lateinit var fetchAvailableVouchersViewModel: BehaviouralRewardsFetchAvailableVouchersViewModel

    var isAbsaRewardsCustomer: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behaviouralRewardsActivity = context as BehaviouralRewardsActivity
        customerVoucherViewModel = viewModel()
        fetchAllChallengesViewModel = viewModel()
        rewardsViewModel = behaviouralRewardsActivity.viewModel()
        fetchVoucherPartnerMetaDataViewModel = behaviouralRewardsActivity.viewModel()
        fetchAvailableVouchersViewModel = behaviouralRewardsActivity.viewModel()
    }

    fun getSearchView() = behaviouralRewardsActivity.getSearchView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentFragment = behaviouralRewardsActivity.currentFragment
        if (currentFragment is BehaviouralRewardsHubFragment || currentFragment is BehaviouralRewardsClaimRewardFragment) {
            behaviouralRewardsActivity.hubToolbar.background = ColorDrawable(ContextCompat.getColor(behaviouralRewardsActivity, R.color.behavioural_rewards_hub_background))
            behaviouralRewardsActivity.hideRewardsToolBarSeparator()
        } else {
            behaviouralRewardsActivity.hubToolbar.background = ColorDrawable(ContextCompat.getColor(behaviouralRewardsActivity, color.transparent))
            behaviouralRewardsActivity.showRewardsToolBarSeparator()
        }
    }

    fun trackAnalytics(action: String) {
        if (isAbsaRewardsCustomer) {
            AnalyticsUtil.trackAction("Behavioural Rewards", "BehaviouralRewards_$action")
        } else {
            AnalyticsUtil.trackAction("Behavioural Rewards", "BehaviouralRewardsNonAbsa_$action")
        }
    }
}