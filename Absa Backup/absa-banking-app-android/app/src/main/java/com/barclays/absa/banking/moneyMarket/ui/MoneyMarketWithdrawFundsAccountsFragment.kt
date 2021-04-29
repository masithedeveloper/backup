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
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.databinding.MoneyMarketWithdrawFundsAccountsFragmentBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.banking.transfer.AccountListItem
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorList
import styleguide.utils.extensions.toFormattedAmount

class MoneyMarketWithdrawFundsAccountsFragment : MoneyMarketBaseFragment(R.layout.money_market_withdraw_funds_accounts_fragment) {
    private val binding by viewBinding(MoneyMarketWithdrawFundsAccountsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.money_market_confirm_title)
        binding.nextButton.setOnClickListener { checkLimitAndNavigateToConfirmation() }
        buildTransactionalAccountList()
        binding.noticeTextView.text = getString(R.string.money_market_withdraw_funds_description, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.currentBalance.getAmount().toFormattedAmount())
    }

    private fun checkLimitAndNavigateToConfirmation() {
        DigitalLimitsHelper.checkTransferAmount(activity as BaseActivity, moneyMarketViewModel.moneyMarketFlowModel.moneyMarketAccount.currentBalance)
        DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, { digitalLimitState: DigitalLimitState ->
            dismissProgressDialog()
            if (digitalLimitState === DigitalLimitState.CHANGED || digitalLimitState === DigitalLimitState.UNCHANGED) {
                navigate(MoneyMarketWithdrawFundsAccountsFragmentDirections.actionMoneyMarketWithdrawFundsAccountsFragmentToMoneyMarketWithdrawFundsConfirmationFragment())
            }
        })
    }

    private fun buildTransactionalAccountList() {
        val accountList = moneyMarketViewModel.availableTransferAccounts()
        val debitOrderAccountList = SelectorList<AccountListItem>()
        accountList.mapTo(debitOrderAccountList) { account ->
            AccountListItem().apply {
                accountNumber = account.accountNumber
                accountType = account.description
                accountBalance = account.availableBalance.toString()
            }
        }

        var selectedAccount: AccountObject
        if (!accountList.isNullOrEmpty()) {
            selectedAccount = accountList.first()
            binding.accountSelectorNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)
            moneyMarketViewModel.moneyMarketFlowModel.moneyMarketDestinationAccount = selectedAccount
        }

        binding.accountSelectorNormalInputView.setList(debitOrderAccountList, getString(R.string.debit_order_select_account_number))
        binding.accountSelectorNormalInputView.setItemSelectionInterface(ItemSelectionInterface { index ->
            selectedAccount = accountList[index]
            binding.accountSelectorNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description, selectedAccount.accountNumber)
            moneyMarketViewModel.moneyMarketFlowModel.moneyMarketDestinationAccount = selectedAccount
        })
    }
}