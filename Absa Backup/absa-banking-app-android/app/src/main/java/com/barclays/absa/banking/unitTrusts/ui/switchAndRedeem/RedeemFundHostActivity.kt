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

package com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.SellUnitTrustFundHostActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.unitTrusts.services.dto.FundRedemptionInfo
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustRedemptionAccountDetails
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustAccountsFragment
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustFundBaseActivity
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel

class RedeemFundHostActivity : BaseActivity() {

    private lateinit var binding: SellUnitTrustFundHostActivityBinding
    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var navController: NavController

    companion object {
        private const val REDEMPTION_ACCOUNT_PARCEL_KEY = "redemptionAccountParcel"
        private const val REDEEM_FUND_CHANNEL = "WIMI_UT_RedeemFund"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.sell_unit_trust_fund_host_activity, null, false)
        setContentView(binding.root)
        setToolBarBack(R.string.redeem_fund_redemption_details_header)
        navController = findNavController(R.id.navigationHostFragment)
        viewUnitTrustViewModel = this.viewModel()
        fetchRedemptionAccount()
    }

    private fun fetchRedemptionAccount() {
        val bundle: Bundle? = intent.extras
        bundle?.let {
            viewUnitTrustViewModel.unitTrustRedemptionAccountData.value = FundRedemptionInfo()
            viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.apply {
                it.getParcelable<UnitTrustRedemptionAccountDetails>(REDEMPTION_ACCOUNT_PARCEL_KEY)?.apply { redemptionAccountDetail = this }
                it.getParcelable<UnitTrustFund>(ViewUnitTrustFundBaseActivity.FUND_PARCEL_KEY)?.apply { unitTrustFund = this }
                unitTrustAccountHolder = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_HOLDER).orEmpty()
                unitTrustAccountNumber = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_NUMBER).orEmpty()
            }
        }
    }

    fun showProgressIndicator() {
        binding.progressIndicator.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        binding.progressIndicator.visibility = View.GONE
    }

    fun progressIndicatorStep(step: Int) = binding.progressIndicator.setNextStep(step)

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.genericResultScreenFragment) {
            super.onBackPressed()
        }
    }

    fun trackEvent(actionPerformed: String) {
        AnalyticsUtil.trackAction(REDEEM_FUND_CHANNEL, actionPerformed)
    }
}