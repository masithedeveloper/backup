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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.buy_unit_trust_host_activity.*

class BuyUnitTrustHostActivity : BaseActivity(R.layout.buy_unit_trust_host_activity), BuyUnitTrustView {

    companion object {
        const val UNIT_TRUST_DATA = "unitTrusts"
        const val IS_BUY_NEW_FUND = "isBuyNewFund"
        const val STEPS = 4
        private const val BUY_UNIT_TRUST_CHANNEL = "WIMI_UT_BuyNew"
        private const val UNIT_TRUST_BUY_NEW_FUND_CHANNEL = "WIMI_UT_BuyNewFund"
    }

    private lateinit var buyUnitTrustViewModel: BuyUnitTrustViewModel
    private lateinit var navController: NavController
    private lateinit var graph: NavGraph
    private var navGraphId: Int = R.navigation.buy_unit_trust_navigation_graph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buyUnitTrustViewModel = viewModel()
        setSupportActionBar(toolbar as? Toolbar)
        showToolbar()
        showToolBarBackArrow()
        navController = findNavController(R.id.buyUnitTrustNavigationHostFragment)
        buyUnitTrustViewModel.unitTrustData = intent?.extras?.getParcelable(UNIT_TRUST_DATA) ?: UnitTrustData()

        intent?.getBooleanExtra(IS_BUY_NEW_FUND, false)?.let { isBuyFund ->
            buyUnitTrustViewModel.isBuyNewFund = isBuyFund
            if (isBuyFund) {
                navGraphId = R.navigation.buy_new_fund_navigation_graph
                graph = navController.navInflater.inflate(navGraphId)
                graph.startDestination = if (buyUnitTrustViewModel.isAccountsExceedingOne()) {
                    R.id.accounts_fragment
                } else {
                    buyUnitTrustViewModel.selectFirstUnitTrustAccountNumber()
                    R.id.buyUnitTrustFundsFragment
                }
            } else {
                graph = navController.navInflater.inflate(navGraphId)
            }

            navController.graph = graph
        }
    }

    fun setToolBar(title: String) = setToolBarBack(title)

    fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    fun hideStepIndicator() {
        progressIndicator.visibility = View.GONE
    }

    fun setCurrentProgress(stepNumber: Int) = with(progressIndicator) {
        visibility = View.VISIBLE
        steps = STEPS
        setNextStep(stepNumber)
    }

    fun showToolBarBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun trackEvent(actionPerformed: String) = AnalyticsUtil.trackAction(UNIT_TRUST_BUY_NEW_FUND_CHANNEL, actionPerformed)

    override fun trackCurrentFragment(fragmentName: String) = AnalyticsUtils.getInstance().trackCustomScreen(fragmentName, BUY_UNIT_TRUST_CHANNEL, BMBConstants.TRUE_CONST)

    override fun trackFragmentAction(screenName: String, actionName: String) = AnalyticsUtils.getInstance().trackAppActionStart(screenName, actionName, BMBConstants.TRUE_CONST)

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.genericResultScreenFragment) {
            super.onBackPressed()
        }
    }
}