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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustHostActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsInfo
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustFundBaseActivity.Companion.FUND_PARCEL_KEY
import com.barclays.absa.utils.viewModel

class ViewUnitTrustHostActivity : BaseActivity() {

    private lateinit var binding: ViewUnitTrustHostActivityBinding
    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.view_unit_trust_host_activity, null, false)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar as Toolbar?)
        navController = findNavController(R.id.navigationHostFragment)
        viewUnitTrustViewModel = viewModel()
        fetchFund()
    }

    fun setStep(step: Int) {
        binding.progressIndicator.setNextStep(step)
    }

    fun hideProgressIndicator() {
        binding.progressIndicator.visibility = View.GONE
    }

    fun showProgressIndicator() {
        binding.progressIndicator.visibility = View.VISIBLE
    }

    private fun fetchFund() {
        val bundle: Bundle? = intent.extras
        bundle?.let {
            viewUnitTrustViewModel.buyMoreUnitsData.value = BuyMoreUnitsInfo()
            it.getParcelable<UnitTrustFund>(FUND_PARCEL_KEY)?.apply { viewUnitTrustViewModel.buyMoreUnitsData.value?.fund = this }
            viewUnitTrustViewModel.buyMoreUnitsData.value?.accountHolder = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_HOLDER).orEmpty()
            viewUnitTrustViewModel.buyMoreUnitsData.value?.accountNumber = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_NUMBER).orEmpty()
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.genericResultScreenFragment) {
            super.onBackPressed()
        }
    }

    companion object {
        const val BUY_MORE_UNITS_CHANNEL = "WIMI_UT_MoreUnits"
    }
}