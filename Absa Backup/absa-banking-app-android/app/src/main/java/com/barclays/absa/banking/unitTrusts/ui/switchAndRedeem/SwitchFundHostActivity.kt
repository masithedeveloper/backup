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
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.unitTrusts.services.dto.FundSwitchInfo
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustAccountsFragment
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustFundBaseActivity
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustViewModel
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.switch_funds_host_activity.*

class SwitchFundHostActivity : BaseActivity() {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.switch_funds_host_activity)
        setToolBarBack(R.string.switch_header)
        viewUnitTrustViewModel = viewModel()
        fetchUnitTrustAccountInfo()
    }

    private fun fetchUnitTrustAccountInfo() {
        intent.extras?.let {
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value = FundSwitchInfo()
            viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
                it.getParcelable<UnitTrustFund>(ViewUnitTrustFundBaseActivity.FUND_PARCEL_KEY)?.apply { unitTrustFund = this }
                unitTrustAccountHolderName = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_HOLDER).orEmpty()
                unitTrustAccountNumber = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_NUMBER).orEmpty()
            }
        }
    }

    override fun onBackPressed() {
        val currentDestination = findNavController(R.id.navigationHostFragment).currentDestination?.id
        if (currentDestination != R.id.genericResultScreenFragment) {
            super.onBackPressed()
        }
    }

    fun showProgressIndicator() {
        progressIndicator.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        progressIndicator.visibility = View.GONE
    }

    fun progressIndicatorStep(step: Int) = progressIndicator.setNextStep(step)
}