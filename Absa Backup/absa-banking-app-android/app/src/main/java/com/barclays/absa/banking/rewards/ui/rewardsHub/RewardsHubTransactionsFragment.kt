/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.rewards.ui.rewardsHub

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.rewards.TransactionWrapper
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsBaseFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.DatePickerFragment
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.rewards_hub_transactions_fragment.*
import java.util.*

class RewardsHubTransactionsFragment : BehaviouralRewardsBaseFragment(R.layout.rewards_hub_transactions_fragment) {
    private lateinit var transactionsAdapter: RewardsTransactionsAdapter
    private lateinit var rewardsTransactionView: RewardsTransactionView
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionsRecyclerView.setHasFixedSize(true)
        transactionsFilterAndSearchView.setEditable(false)
        transactionsFilterAndSearchView.setOnSearchClickListener {
            hideCalendarFilterBar()
            rewardsTransactionView.collapseAppBarView()
            rewardsTransactionView.attachSearchViewCallbacks()
        }
        updateViews()
        hookAnalytics()
    }

    private fun updateViews() {
        val transactionWrapper = rewardsCacheService.getTransactions()
        if (transactionWrapper != null) {
            transactionsAdapter = RewardsTransactionsAdapter(transactionWrapper.transactionItemList)
            transactionsRecyclerView.adapter = transactionsAdapter
            val date = DateUtils.formatDate(transactionWrapper.fromDate, SRC_DATE_PATTERN, DEST_DATE_PATTERN) + " - " + DateUtils.formatDate(transactionWrapper.toDate, SRC_DATE_PATTERN, DEST_DATE_PATTERN)
            if (transactionsFilterAndSearchView != null) {
                transactionsFilterAndSearchView.setSearchText(date)
                transactionsFilterAndSearchView.setOnCalendarLayoutClickListener { filterTransactionsUsingDateRange(transactionWrapper.fromDate, transactionWrapper.toDate) }
            }
        }
    }

    private fun hookAnalytics() {
        BaseActivity.mScreenName = BMBConstants.ABSA_REWARDS_HUB
        BaseActivity.mSiteSection = BMBConstants.ABSA_REWARDS
        AnalyticsUtils.getInstance().trackCustomScreenView(BaseActivity.mScreenName, BaseActivity.mSiteSection, BMBConstants.TRUE_CONST)
    }

    private fun filterTransactionsUsingDateRange(fromDate: String?, toDate: String?) {
        val datePickerFragment = DatePickerFragment.newInstance(fromDate, toDate)
        datePickerFragment.setOnDateRangeSelectionListener { startDate: Date?, endDate: Date? ->
            val date = DateUtils.format(startDate, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.format(endDate, DateUtils.DATE_DISPLAY_PATTERN)
            transactionsFilterAndSearchView.setSearchText(date)
            val startDateParam = DateUtils.format(startDate, DateUtils.DASHED_DATE_PATTERN)
            val endDateParam = DateUtils.format(endDate, DateUtils.DASHED_DATE_PATTERN)
            rewardsViewModel.getFilteredTransactions(startDateParam, endDateParam)
            rewardsViewModel.transactionsMutableLiveData.observe(viewLifecycleOwner, { transactionWrapper: TransactionWrapper? ->
                transactionWrapper?.transactionItemList?.let {
                    transactionsAdapter.updateAllTransactions(it)
                }
                dismissProgressDialog()
            })
        }
        datePickerFragment.show(childFragmentManager, "datePickerFragment")
    }

    fun searchTransactionHistory(query: String?) {
        if (::transactionsAdapter.isInitialized) {
            transactionsAdapter.search(query)
        }
    }

    fun showCalendarFilterBar() {
        transactionsFilterAndSearchView?.visibility = View.VISIBLE
    }

    private fun hideCalendarFilterBar() {
        transactionsFilterAndSearchView?.visibility = View.GONE
    }

    companion object {
        private const val SRC_DATE_PATTERN = "yyyy-MM-dd"
        private const val DEST_DATE_PATTERN = "dd MMM yyyy"

        fun newInstance(rewardsTransactionView: RewardsTransactionView): RewardsHubTransactionsFragment {
            val rewardsHubTransactionsFragment = RewardsHubTransactionsFragment()
            rewardsHubTransactionsFragment.rewardsTransactionView = rewardsTransactionView
            return rewardsHubTransactionsFragment
        }
    }
}

interface RewardsTransactionView {
    fun collapseAppBarView()
    fun attachSearchViewCallbacks()
}
