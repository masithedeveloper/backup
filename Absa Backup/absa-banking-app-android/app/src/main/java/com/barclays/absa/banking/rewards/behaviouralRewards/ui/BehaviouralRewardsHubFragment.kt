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
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.rewards.ui.rewardsHub.RewardsHubTransactionsFragment
import com.barclays.absa.banking.rewards.ui.rewardsHub.RewardsTransactionView
import com.barclays.absa.banking.rewards.ui.rewardsHub.State
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.KeyboardUtils
import kotlinx.android.synthetic.main.behavioural_rewards_hub_fragment.*
import styleguide.bars.StateChangedListener
import styleguide.bars.TabBarFragment

class BehaviouralRewardsHubFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_hub_fragment), RewardsTransactionView, OnBackPressedInterface {
    private lateinit var behaviouralRewardsHeaderFragment: BehaviouralRewardsHeaderFragment
    private lateinit var transactionHistoryFragment: RewardsHubTransactionsFragment
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_hub_title))
        showToolBar()
        isAbsaRewardsCustomer = rewardsCacheService.hasRewards()
        behaviouralRewardsHeaderFragment = BehaviouralRewardsHeaderFragment()
        behaviouralRewardsCollapsingAppBarView.addHeaderView(behaviouralRewardsHeaderFragment)
        behaviouralRewardsCollapsingAppBarView.setAppBarState(behaviouralRewardsActivity.appBarStateExpanded)

        if (rewardsCacheService.getRewardsAccount() != null && (rewardsCacheService.getTransactions() == null || rewardsCacheService.getRewardsDetails() == null || rewardsCacheService.getRewardsRedemption() == null)) {
            rewardsViewModel.fireAllRewardsDataRequest()

            rewardsViewModel.state.observe(viewLifecycleOwner, { state: State ->
                if (state == State.QUEUE_COMPLETED) {
                    dismissProgressDialog()
                    if (rewardsCacheService.getRewardsAccount() != null) {
                        behaviouralRewardsHeaderFragment.updateHeader()
                    } else {
                        showGenericErrorMessageThenFinish()
                    }
                    setupBottomSheetFragment()
                }
            })
        } else {
            setupBottomSheetFragment()
        }

        trackAnalytics("RewardsHub_ScreenDisplayed")
    }

    private fun setupBottomSheetFragment() {
        val tabBars = mutableListOf<TabBarFragment>().apply {
            if (FeatureSwitchingCache.featureSwitchingToggles.behaviouralRewards == FeatureSwitchingStates.ACTIVE.key) {
                add(TabBarFragment(BehaviouralRewardsChallengesFragment.newInstance(), getString(R.string.behavioural_rewards_hub_tab_advantage)))
            }
        }

        if (rewardsCacheService.getRewardsAccount() != null) {
            transactionHistoryFragment = RewardsHubTransactionsFragment.newInstance(this)
            val rewardsHubTransactionsTab = TabBarFragment(transactionHistoryFragment, getString(R.string.transaction))
            val rewardsHubDetailsTab = TabBarFragment(BehaviouralRewardsHubDetailsFragment.newInstance(getString(R.string.behavioural_rewards_account_details)), getString(R.string.behavioural_rewards_account_details))

            tabBars.add(rewardsHubTransactionsTab)
            tabBars.add(rewardsHubDetailsTab)
        }

        behaviouralRewardsCollapsingAppBarView.addToolBarAndFragments(requireActivity(), tabBars, object : StateChangedListener {
            override fun onStateChanged(isExpanded: Boolean) {
                behaviouralRewardsActivity.appBarStateExpanded = isExpanded
                if (isExpanded && behaviouralRewardsActivity.isSearchActive) {
                    hideSearchView()
                }
            }

            override fun onTabChanged(position: Int) {
                if (behaviouralRewardsActivity.isSearchActive) {
                    hideSearchView()
                }
            }
        })
    }

    override fun collapseAppBarView() {
        behaviouralRewardsCollapsingAppBarView.collapseToolbar()
    }

    private fun showSearchView() {
        val searchView = getSearchView()
        if (!searchView.isVisible) {
            searchView.visibility = View.VISIBLE
            searchView.requestFocus()
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        behaviouralRewardsActivity.isSearchActive = true
    }

    fun hideSearchView() {
        transactionHistoryFragment.showCalendarFilterBar()
        val searchView = getSearchView()
        if (searchView.isVisible) {
            searchView.setQuery("", true)
            searchView.visibility = View.GONE
            KeyboardUtils.hideKeyboard(behaviouralRewardsActivity)
        }
        behaviouralRewardsActivity.isSearchActive = false
    }

    override fun attachSearchViewCallbacks() {
        showSearchView()
        val searchView = getSearchView()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                transactionHistoryFragment.searchTransactionHistory(newText)
                return true
            }
        })
        searchView.setOnCloseListener {
            hideSearchView()
            false
        }
    }

    override fun onBackPressed(): Boolean {
        if (getSearchView().isVisible) {
            hideSearchView()
        } else {
            trackAnalytics("RewardsHub_BackButtonClicked")
            baseActivity.finish()
        }
        return true
    }
}