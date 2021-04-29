/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.activity_view_more_fund_accounts.*

class ViewMoreFundAccountsActivity : BaseActivity(R.layout.activity_view_more_fund_accounts), AllFundsAdapter.AllFundsItemClickListener {

    private var accountNumber = ""
    private var accountHolder = ""
    private var unitTrustFundList = listOf<UnitTrustFund>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.all_funds_in_account))

        intent.extras?.let {
            unitTrustFundList = it.getParcelableArrayList(ViewUnitTrustAccountsFragment.LIST_OF_FUNDS_KEY) ?: listOf()
            accountHolder = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_HOLDER).orEmpty()
            accountNumber = it.getString(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_NUMBER).orEmpty()

            fundsRecyclerView.adapter = AllFundsAdapter(unitTrustFundList, this)
        }
        AnalyticsUtil.trackAction("WIMI_UT_CHANNEL", "WIMI_UT_View_ShowAllFund")
    }

    override fun onFundNameClicked(unitTrustFund: UnitTrustFund) = startActivity(Intent(this, ViewUnitTrustFundBaseActivity::class.java).apply {
        putExtra(ViewUnitTrustFundBaseActivity.FUND_PARCEL_KEY, unitTrustFund)
        putExtra(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
        putExtra(ViewUnitTrustAccountsFragment.UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
    })
}