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

package com.barclays.absa.banking.debiCheck.ui

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.core.util.isEmpty
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.utils.viewModel
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener
import kotlinx.android.synthetic.main.debicheck_activity.*
import styleguide.bars.FragmentPagerItem
import styleguide.bars.TabPager

class DebiCheckActivity : BaseActivity() {
    private lateinit var debiCheckViewModel: DebiCheckViewModel
    private lateinit var debitOrderViewModel: DebitOrderViewModel
    private var isInitialLoad = true
    var debitOrderResponse: DebitOrderList? = null
    var tabCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debicheck_activity)
        debiCheckViewModel = viewModel()
        debitOrderViewModel = viewModel()
        setToolBarBack(getString(R.string.debicheck_debit_orders))
        initializeTabs()

        setUpObservers()
        showProgressDialog()
        when (FeatureSwitchingStates.ACTIVE.key) {
            FeatureSwitchingCache.featureSwitchingToggles.debiCheck -> debiCheckViewModel.fetchPendingDebitOrders()
            FeatureSwitchingCache.featureSwitchingToggles.debitOrderHubWithMinimumValue -> debitOrderViewModel.fetchDebitOrderTransactionList()
        }
    }

    private fun setUpObservers() {
        debiCheckViewModel.pendingMandateResponse.observe(this, {
            if (FeatureSwitchingCache.featureSwitchingToggles.debitOrderHubWithMinimumValue == FeatureSwitchingStates.ACTIVE.key) {
                debitOrderViewModel.fetchDebitOrderTransactionList()
            } else {
                dismissProgressDialog()
            }
        })

        debitOrderViewModel.debitOrdersList.observe(this, {
            if (tabCount == 2 && FeatureSwitchingCache.featureSwitchingToggles.debitOrderHubWithMinimumValue == FeatureSwitchingStates.ACTIVE.key) {
                debiCheckViewModel.pendingMandateResponse.value?.mandates?.let { debiCheckMandates ->
                    if (isInitialLoad) {
                        if (debiCheckMandates.isEmpty() && !isBusinessAccount) {
                            tabBarView.getTabAt(1)?.select()
                        } else {
                            tabBarView.getTabAt(0)?.select()
                        }
                        isInitialLoad = false
                    }
                }
            }
            dismissProgressDialog()
        })
    }

    private fun initializeTabs() {
        val tabs = SparseArray<FragmentPagerItem>().apply {
            if (FeatureSwitchingCache.featureSwitchingToggles.debiCheck == FeatureSwitchingStates.ACTIVE.key) {
                append(0, DebiCheckPagerItemFragment.newInstance(getString(R.string.debicheck_title)))
                tabCount++
            }

            if (FeatureSwitchingCache.featureSwitchingToggles.debitOrderHubWithMinimumValue == FeatureSwitchingStates.ACTIVE.key && !isBusinessAccount) {
                append(tabCount, DebitOrdersPagerItemFragment.newInstance(getString(R.string.debicheck_debit_orders)))
                tabCount++
            }
        }

        if (tabs.isEmpty()) {
            tabBarView.visibility = View.GONE
            viewPager.visibility = View.GONE
            noDebitOrderTextView.visibility = View.VISIBLE
        } else {
            tabBarView.addTabs(tabs)
            viewPager.adapter = TabPager(supportFragmentManager, tabs)
            tabBarView.addOnTabSelectedListener(ViewPagerOnTabSelectedListener(viewPager))
            viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabBarView))
        }
    }
}