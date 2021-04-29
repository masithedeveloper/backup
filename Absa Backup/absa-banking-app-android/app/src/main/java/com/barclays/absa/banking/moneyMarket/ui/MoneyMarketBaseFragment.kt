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
package com.barclays.absa.banking.moneyMarket.ui

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment

abstract class MoneyMarketBaseFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {
    protected val moneyMarketViewModel by activityViewModels<MoneyMarketViewModel>()
    protected lateinit var moneyMarketActivity: MoneyMarketActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        moneyMarketActivity = context as MoneyMarketActivity
    }

    fun getInvestmentAccountType(): String = if (isBusinessAccount) getString(R.string.money_market_absa_account) else getString(R.string.money_market_cash_account)
}