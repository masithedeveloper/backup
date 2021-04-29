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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MoneyMarketConvertToCashConfirmationFragmentBinding
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toFormattedAmount

class MoneyMarketConvertToCashConfirmationFragment : MoneyMarketBaseFragment(R.layout.money_market_convert_to_cash_confirmation_fragment) {
    private val binding by viewBinding(MoneyMarketConvertToCashConfirmationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.money_market_confirm_title)
        with(binding) {
            convertToCashTitleTextView.text = getString(R.string.money_market_convert_to_cash_title, getInvestmentAccountType())
            accountNumberBulletItemView.setContentTextView(getString(R.string.money_market_convert_to_cash_account_number, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.accountNumber.toFormattedAccountNumber()))
            balanceBulletItemView.setContentTextView(getString(R.string.money_market_convert_to_cash_balance_moved, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.currentBalance.getAmount().toFormattedAmount()))
            continueButton.setOnClickListener { navigate(MoneyMarketConvertToCashConfirmationFragmentDirections.actionMoneyMarketConvertToCashConfirmationFragmentToMoneyMarketTermsAndConditionsFragment()) }
        }
    }
}