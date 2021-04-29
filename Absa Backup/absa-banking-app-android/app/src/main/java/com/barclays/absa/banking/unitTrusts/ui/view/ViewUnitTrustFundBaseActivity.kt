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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.os.Bundle
import android.util.SparseArray
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.bars.CollapsingAppBarView
import styleguide.bars.FragmentPagerItem

class ViewUnitTrustFundBaseActivity : BaseActivity() {
    private var tabs: SparseArray<FragmentPagerItem>? = null
    private lateinit var binding: ViewUnitTrustActivityBinding
    private lateinit var collapsingAppBarView: CollapsingAppBarView
    private lateinit var unitTrustFund: UnitTrustFund
    private var accountHolder: String = ""
    private var accountNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.view_unit_trust_activity, null, false)
        setContentView(binding.root)

        intent.let {
            unitTrustFund = it.getParcelableExtra(FUND_PARCEL_KEY) ?: UnitTrustFund()
            accountHolder = it.getStringExtra(UNIT_TRUST_ACCOUNT_HOLDER) ?: ""
            accountNumber = it.getStringExtra(UNIT_TRUST_ACCOUNT_NUMBER) ?: ""
        }

        if (!unitTrustFund.fundName.isNullOrEmpty()) {
            setToolBarBack(unitTrustFund.fundName.toString())
        }
        getCollapsingHeader()
    }

    private fun getCollapsingHeader() {
        tabs = SparseArray()
        collapsingAppBarView = binding.viewUnitAppBarView
        collapsingAppBarView.addHeaderView(ViewUnitTrustFundsHeaderFragment.newInstance(unitTrustFund, accountHolder, accountNumber))
        tabs?.append(0, ViewUnitTrustFundsOverViewFragment.newInstance(unitTrustFund, getString(R.string.fund_overview)))
        tabs?.append(1, ViewUnitTrustFundsInformationFragment.newInstance(unitTrustFund, getString(R.string.fund_information)))
        collapsingAppBarView.setUpTabs(this, tabs)
        collapsingAppBarView.setBackground(R.drawable.gradient_light_purple_warm_purple)
        collapsingAppBarView.setOnPageSelectionListener { _, position ->
            when (position) {
                0 -> AnalyticsUtil.trackAction("WIMI_UT_CHANNEL", "WIMI_UT_View_FundOverview")
                1 -> AnalyticsUtil.trackAction("WIMI_UT_CHANNEL", "WIMI_UT_View_FundInformation")
            }
        }
    }

    companion object {
        const val FUND_PARCEL_KEY = "fundParcel"
        const val UNIT_TRUST_ACCOUNT_NUMBER = "accountNumber"
        const val UNIT_TRUST_ACCOUNT_HOLDER = "accountHolder"
    }
}