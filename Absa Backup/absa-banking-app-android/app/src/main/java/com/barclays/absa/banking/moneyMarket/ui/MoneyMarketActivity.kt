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

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.databinding.MoneyMarketActivityBinding
import com.barclays.absa.banking.framework.BaseActivity

const val ACTIVE_ORBIT_STATUS = "ACTIVE"
const val MONEY_MARKET_ACCOUNT = "MONEY_MARKET_ACCOUNT"

class MoneyMarketActivity : BaseActivity() {
    private val binding by viewBinding(MoneyMarketActivityBinding::inflate)
    private val viewModel by viewModels<MoneyMarketViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.moneyMarketFlowModel.moneyMarketAccount = intent?.extras?.getSerializable(MONEY_MARKET_ACCOUNT) as AccountObject
    }

    fun hideToolbarSeparator() {
        binding.dividerView.visibility = View.GONE
    }
}