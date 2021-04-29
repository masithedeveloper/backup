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
import com.barclays.absa.banking.databinding.MoneyMarketChooseProductFragmentBinding
import com.barclays.absa.banking.moneyMarket.service.dto.MoneyMarketFlowStates
import com.barclays.absa.utils.extensions.viewBinding

class MoneyMarketChooseProductFragment : MoneyMarketBaseFragment(R.layout.money_market_choose_product_fragment) {
    private val binding by viewBinding(MoneyMarketChooseProductFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolBar(R.string.money_market_fund_closure)
        setUpListeners()

        with(binding) {
            convertRadioButtonView.setSecondaryLinearLayout(View.inflate(requireContext(), R.layout.money_market_choose_convert_layout, null))
            withdrawRadioButtonView.setSecondaryLinearLayout(View.inflate(requireContext(), R.layout.money_market_choose_withdraw_funds_layout, null))
            convertRadioButtonView.getRadioButton().text = getString(R.string.money_market_choose_product_convert_title, getInvestmentAccountType())
            introductionTextView.text = getString(R.string.money_market_choose_product_introduction, getInvestmentAccountType())
            moneyMarketNoticeTextView.text = getString(R.string.money_market_choose_product_notice, getInvestmentAccountType())

            if (moneyMarketViewModel.availableTransferAccounts().isEmpty()) {
                withdrawRadioButtonView.visibility = View.GONE
            }
        }
    }

    private fun setUpListeners() {
        with(binding) {
            convertRadioButtonView.getRadioButton().setOnClickListener {
                convertRadioButtonView.setChecked(true)
                withdrawRadioButtonView.setChecked(false)
            }

            withdrawRadioButtonView.getRadioButton().setOnClickListener {
                convertRadioButtonView.setChecked(false)
                withdrawRadioButtonView.setChecked(true)
            }

            nextButton.setOnClickListener {
                when {
                    convertRadioButtonView.isChecked() -> {
                        navigate(MoneyMarketChooseProductFragmentDirections.actionMoneyMarketChooseProductFragmentToMoneyMarketConvertToCashConfirmationFragment())
                        moneyMarketViewModel.moneyMarketFlowModel.moneyMarketFlow = MoneyMarketFlowStates.CONVERT
                    }
                    withdrawRadioButtonView.isChecked() -> {
                        navigate(MoneyMarketChooseProductFragmentDirections.actionMoneyMarketChooseProductFragmentToMoneyMarketWithdrawFundsAccountsFragment())
                        moneyMarketViewModel.moneyMarketFlowModel.moneyMarketFlow = MoneyMarketFlowStates.WITHDRAW
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isConvertFlow = moneyMarketViewModel.moneyMarketFlowModel.moneyMarketFlow == MoneyMarketFlowStates.CONVERT
        binding.withdrawRadioButtonView.setChecked(!isConvertFlow)
        binding.convertRadioButtonView.setChecked(isConvertFlow)
    }
}