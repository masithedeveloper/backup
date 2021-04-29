/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.cluster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.Header
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.explore.ui.ExploreHubRecyclerViewAdapter
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.business.ExploreHubBusinessFixedDepositOffer
import com.barclays.absa.banking.explore.ui.offers.retail.*
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountTypes.FIXED_DEPOSIT
import com.barclays.absa.banking.home.ui.AccountTypes.NOTICE_DEPOSIT
import com.barclays.absa.banking.home.ui.AccountTypes.SAVINGS_ACCOUNT
import com.barclays.absa.banking.home.ui.AccountTypes.UNIT_TRUST_ACCOUNT
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.barclays.absa.banking.unitTrusts.ui.view.UnitTrustAccountView
import com.barclays.absa.banking.unitTrusts.ui.view.UnitTrustAccountsPresenter
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustBaseActivity
import java.lang.ref.WeakReference

class InvestmentClusterActivity : BaseClusterActivity(), ClusterView, UnitTrustAccountView {

    private lateinit var unitTrustPresenter: UnitTrustAccountsPresenter
    private lateinit var offerArrayList: ArrayList<ExploreHubBaseOffer>
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClusterViews()
        initAdapter()
        unitTrustPresenter = UnitTrustAccountsPresenter(WeakReference<UnitTrustAccountView>(this))
    }

    private fun initClusterViews() {
        binding.claimIconButtonView.visibility = View.GONE
        setClusterImageView(R.drawable.ic_fixed_deposit_small)
        setClusterTypeTextView(getString(R.string.cluster_save_and_invest_heading))
        setNumberOfAccounts(homeCacheService.getSavingsAndInvestmentsAccounts().size)
    }

    private fun initAdapter() {
        val savingsAndInvestmentAccounts = homeCacheService.getSavingsAndInvestmentsAccounts().toMutableList().apply {
            add(0, Header(getString(R.string.investment_cluster_investment_accounts_heading)))
        }
        binding.accountsRecyclerView.adapter = ClusterAdapter(this, savingsAndInvestmentAccounts)
    }

    private fun setNumberOfAccounts(numberOfAccounts: Int) {
        when (numberOfAccounts) {
            1 -> binding.numberActivePoliciesTextView.text = getString(R.string.investment_cluster_only_one_account, numberOfAccounts)
            else -> binding.numberActivePoliciesTextView.text = getString(R.string.investment_cluster_multiple_accounts, numberOfAccounts)
        }
    }

    override fun buildOffers(offersResponseObject: OffersResponseObject) {
        offerArrayList = ArrayList<ExploreHubBaseOffer>().apply {
            if (!isBusinessAccount) {
                add(ExploreHubUnitTrustOffer(this@InvestmentClusterActivity, offersResponseObject.applyUnitTrustData, offersResponseObject.hasUnitTrustAccount, populateUnitTrustData(offersResponseObject)))
                add(ExploreHubFixedDepositOffer(this@InvestmentClusterActivity))
            } else {
                add(ExploreHubBusinessFixedDepositOffer(this@InvestmentClusterActivity))
            }
        }
        if (!offerArrayList.isNullOrEmpty()) {
            binding.resultImageView.visibility = View.GONE
            binding.getMoreOffersRecyclerView.visibility = View.VISIBLE
            binding.getMoreOffersRecyclerView.adapter = ExploreHubRecyclerViewAdapter(offerArrayList.filter { exploreHubBaseOffer -> exploreHubBaseOffer.isEnabled() })

        } else {
            noOffersAvailable()
        }
    }

    override fun onCardClicked(entry: Entry) {
        val accountObject = entry as AccountObject
        when (accountObject.accountType) {
            NOTICE_DEPOSIT -> startActivity(IntentFactory.getAccountActivity(this, accountObject))
            FIXED_DEPOSIT -> startActivity(IntentFactory.getFixedDepositHubActivity(this, accountObject))
            UNIT_TRUST_ACCOUNT -> {
                if (FeatureSwitchingCache.featureSwitchingToggles.viewUnitTrusts == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_unit_trust))))
                } else {
                    homeCacheService.setUnitTrustAccountObject(accountObject)
                    unitTrustPresenter.onUnitTrustAccountsHubCreated()
                }
            }
            SAVINGS_ACCOUNT -> {
                startActivity(IntentFactory.getAccountActivity(this, accountObject))
            }
        }
    }

    override fun displayUnitTrustAccount(successResponse: UnitTrustAccountsWrapper) {
        val viewUnitTrustIntent = Intent(this, ViewUnitTrustBaseActivity::class.java)
        homeCacheService.setUnitTrustResponseModel(successResponse)
        startActivity(viewUnitTrustIntent)
    }

    override fun displaySomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message))
    }

    private fun populateUnitTrustData(successResponse: OffersResponseObject): UnitTrustData {
        return UnitTrustData().apply {
            if (successResponse.allFundsMinDebitOrderAmt != null && successResponse.allFundsMinLumpSumAmt != null) {
                allFundsMinDebitOrderAmt = successResponse.allFundsMinDebitOrderAmt.toString()
                allFundsMinLumpSumAmt = successResponse.allFundsMinLumpSumAmt.toString()
                hasUnitTrustAccount = successResponse.hasUnitTrustAccount
                validateClientStatus = successResponse.validateClientStatus
            }
        }
    }
}