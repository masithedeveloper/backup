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

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.shared.ItemPagerFragment
import kotlinx.android.synthetic.main.rewards_hub_details_fragment.*

class BehaviouralRewardsHubDetailsFragment : ItemPagerFragment(R.layout.rewards_hub_details_fragment) {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        @JvmStatic
        fun newInstance(description: String?): BehaviouralRewardsHubDetailsFragment {
            return BehaviouralRewardsHubDetailsFragment().apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        BaseActivity.mScreenName = BMBConstants.ABSA_REWARDS_MEMBERSHIP_DETAILS
        BaseActivity.mSiteSection = BMBConstants.ABSA_REWARDS
        AnalyticsUtils.getInstance().trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
    }

    private fun initViews() {
        rewardsCacheService.getRewardsDetails()?.let {
            totalEarningsLineItemView.setLineItemViewContent(it.earningsToDate?.toString() ?: "")
            totalRewardsRedeemedLineItemView.setLineItemViewContent(it.redeemedToDate?.toString() ?: "")
        }

        manageDebiOrderOptionActionButtonView.setOnClickListener {
            findNavController().navigate(BehaviouralRewardsHubFragmentDirections.actionBehaviouralRewardsHubFragmentToBehaviouralRewardsManageDebitOrderFragment())
        }

        emailCentreContactView.setOnClickListener {
            EmailUtil.email(activity, getString(R.string.banking_app_support_email))
        }
    }

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: "Details"
}