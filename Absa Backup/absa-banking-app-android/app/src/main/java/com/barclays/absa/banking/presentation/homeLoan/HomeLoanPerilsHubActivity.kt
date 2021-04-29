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
package com.barclays.absa.banking.presentation.homeLoan

import android.os.Bundle
import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.home.ui.InsuranceUtils
import com.barclays.absa.banking.presentation.homeLoan.services.dto.HomeLoanDTO
import com.barclays.absa.banking.shared.GenericStatementFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericStatementView
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHubActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.bars.FragmentPagerItem

class HomeLoanPerilsHubActivity : GenericTransactionHubActivity(), GenericStatementView {
    lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel
    private lateinit var homeLoanViewModel: HomeLoanViewModel
    private var accountObject: AccountObject = AccountObject()
    private val featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles
    private var policyDetail: PolicyDetail? = null
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    companion object {
        const val POLICY_DETAIL = "policyDetail"
        const val ACCOUNT_DETAIL = "accountDetail"
        const val HOME_LOAN_HUB = "homeLoanHub"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ACCOUNT_OBJECT, accountObject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.home_loan))

        homeLoanViewModel = viewModel()

        if (savedInstanceState != null) {
            homeLoanViewModel.homeLoanDetails = MutableLiveData<HomeLoanDTO>()
            accountObject = savedInstanceState.getSerializable(ACCOUNT_OBJECT) as AccountObject
        }

        if (intent.extras != null) {
            val bundle = intent.extras
            accountObject = bundle?.getSerializable(ACCOUNT_OBJECT) as AccountObject
            homeCacheService.setHomeLoanPerilsAccount(accountObject)
        } else {
            accountObject = homeCacheService.getHomeLoanPerilsAccount() ?: AccountObject()
        }

        setTransactionHistoryInitialDateRange(-30)
        genericTransactionHistoryViewModel = viewModel()
        genericTransactionHistoryViewModel.accountDetail = accountObject

        initViews()
    }

    private fun initViews() {
        policyDetail = if (InsuranceUtils.hasHomeOwnerPolicy()) appCacheService.getPolicyDetail() else null

        if (featureSwitchingToggles.homeLoanHubInformationTab == FeatureSwitchingStates.ACTIVE.key && accountObject.accountNumber.isNotEmpty()) {
            homeLoanViewModel.fetchHomeLoanDetails(accountObject.accountNumber)
        } else {
            setUpTabs(accountObject, policyDetail, false)
        }

        homeLoanViewModel.homeLoanDetails.observe(this, {
            dismissProgressDialog()
            val wasServiceCallSuccessful = !it.accountNumber.isBlank()
            setUpTabs(accountObject, policyDetail, wasServiceCallSuccessful)
            homeLoanViewModel.homeLoanDetails.removeObservers(this)
        })

        homeLoanViewModel.failureResponse.observe(this, {
            dismissProgressDialog()
            setUpTabs(accountObject, policyDetail, false)
            homeLoanViewModel.failureResponse.removeObservers(this)
        })
    }

    fun setUpTabs(accountObject: AccountObject, policyDetail: PolicyDetail?, wasServiceCallSuccessful: Boolean) {
        val sparseArray: SparseArray<FragmentPagerItem> = SparseArray()
        val fragmentItems: ArrayList<FragmentPagerItem> = ArrayList()
        val featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles

        fragmentItems.add(transactionHistoryFragment)
        if (featureSwitchingToggles.homeLoanHubInformationTab == FeatureSwitchingStates.ACTIVE.key && !accountObject.accountNumber.isNullOrEmpty() && wasServiceCallSuccessful) {
            fragmentItems.add(HomeLoansInformationFragment.newInstance(getString(R.string.info)))
        }
        if (featureSwitchingToggles.homeLoanArchivedStatements == FeatureSwitchingStates.ACTIVE.key) {
            fragmentItems.add(GenericStatementFragment.newInstance(getString(R.string.statements), AccountTypes.HOME_LOAN))
        }
        fragmentItems.add(HomeLoanAccountInsuranceFragment.newInstance(getString(R.string.insure), policyDetail))

        collapsingAppbarView.addHeaderView(HomeLoanPerilsHubHeaderFragment.newInstance())
        collapsingAppbarView.setBackground(R.drawable.gradient_light_purple_warm_purple)

        fragmentItems.forEachIndexed { index, fragmentPagerItem -> sparseArray.append(index, fragmentPagerItem) }

        val homeLoanInformationActive = featureSwitchingToggles.homeLoanHubInformationTab == FeatureSwitchingStates.ACTIVE.key
        val homeLoanStatementsActive = featureSwitchingToggles.homeLoanArchivedStatements == FeatureSwitchingStates.ACTIVE.key

        collapsingAppbarView.setOnPageSelectionListener { _, position ->
            if (position == 0) {
                AnalyticsUtil.trackAction(HOME_LOAN_HUB, "HomeLoanAccountHub_HubScreen_TransactionHistoryDisplayed")
            } else if (homeLoanInformationActive && position == 1) {
                AnalyticsUtil.trackAction(HOME_LOAN_HUB, "HomeLoanAccountHub_HubScreen_InformationTabDisplayed")
            } else if (homeLoanStatementsActive && position == 1 || (homeLoanInformationActive && !homeLoanStatementsActive && position == 2)) {
                AnalyticsUtil.trackAction(HOME_LOAN_HUB, "HomeLoanAccountHub_HubScreen_DownloadStatementDisplayed")
            }
        }

        collapsingAppbarView.setUpTabs(this, sparseArray)
    }

    override fun statementViewModel(): GenericTransactionHistoryViewModel {
        if (!::genericTransactionHistoryViewModel.isInitialized) {
            genericTransactionHistoryViewModel = viewModel()
        }
        return genericTransactionHistoryViewModel
    }
}