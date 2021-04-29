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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FilterAccountList
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fixed_deposit_new_fixed_deposit_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toRandAmount
import java.math.BigDecimal

class FixedDepositNewFixedDepositFragment : BaseFragment(R.layout.fixed_deposit_new_fixed_deposit_fragment) {

    private lateinit var rootView: View
    private lateinit var viewModel: FixedDepositViewModel
    private lateinit var fixedDepositData: FixedDepositData
    private lateinit var accountObjectWrappers: SelectorList<AccountObjectWrapper>
    private lateinit var selectedAccount: AccountObjectWrapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit New Fixed Deposit Screen")

        this.rootView = view
        (activity as FixedDepositActivity).setToolbarTitle(getString(R.string.fixed_deposit_new_fixed_deposit))
        (activity as FixedDepositActivity).showToolbar()

        initialiseViewModel()
        initViews()
        populateViews()
    }

    private fun initialiseViewModel() {
        viewModel = baseActivity.viewModel()
    }

    private fun initViews() {
        fixedDepositData = viewModel.fixedDepositData

        accountNameNormalInputView.selectedValue = getString(R.string.fixed_deposit_fixed_deposit)
        AbsaCacheManager.getInstance().accountsList.accountsList.let {
            for (account in it) {
                if (getString(R.string.fixed_deposit_fixed_deposit).equals(account.description, true)) {
                    accountNameNormalInputView.selectedValue = ""
                    break
                }
            }
        }

        accountNameNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                accountNameNormalInputView.clearError()
                AbsaCacheManager.getInstance().accountsList.accountsList.forEach {
                    if (s.toString().equals(it.description, true)) {
                        accountNameNormalInputView.setError(getString(R.string.fixed_deposit_account_name_exists, it.description))
                        return
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        investmentAmountNormalInputView.selectedValue = viewModel.fixedDepositData.amount.amountValue.toString()

        accountNameNormalInputView.addRequiredValidationHidingTextWatcher()
        investmentAmountNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateTransferAmount()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        fromAccountReferenceNormalInputView.addRequiredValidationHidingTextWatcher()
        toAccountReferenceNormalInputView.addRequiredValidationHidingTextWatcher()

        nextButton.setOnClickListener {
            if (isValidInput()) {
                fixedDepositData.name = accountNameNormalInputView.selectedValue
                fixedDepositData.amount = Amount(investmentAmountNormalInputView.selectedValueUnmasked)
                fixedDepositData.fromReference = fromAccountReferenceNormalInputView.selectedValue
                fixedDepositData.toReference = toAccountReferenceNormalInputView.selectedValue

                val activity = activity as BaseActivity
                DigitalLimitsHelper.checkTransferAmount(activity, fixedDepositData.amount)

                DigitalLimitsHelper.digitalLimitState.observe(activity, {
                    if (it === DigitalLimitState.CANCELLED) {
                        dismissProgressDialog()
                    } else if (it === DigitalLimitState.CHANGED || it === DigitalLimitState.UNCHANGED) {
                        viewModel.createAccountConfirmResponse = MutableLiveData()
                        viewModel.confirmAccount()
                        viewModel.createAccountConfirmResponse.observe(viewLifecycleOwner, {
                            dismissProgressDialog()
                            navigate(FixedDepositNewFixedDepositFragmentDirections.actionFixedDepositNewFixedDepositFragmentToFixedDepositNewFixedDepositDetailsFragment())
                            viewModel.createAccountConfirmResponse.removeObservers(this)
                        })
                    }
                })
            }
        }
    }

    private fun validateTransferAmount() {
        val selectedValueUnmasked: String = investmentAmountNormalInputView.selectedValueUnmasked
        val availableBalance = selectedAccount.accountObject.availableBalance.amountDouble.toBigDecimal()
        if (selectedValueUnmasked.isNotEmpty() && BigDecimal(selectedValueUnmasked) > availableBalance) {
            investmentAmountNormalInputView.setError(getString(R.string.amount_exceeds_available))
            return
        }
        investmentAmountNormalInputView.setDescription("${getString(R.string.available_balance)} ${availableBalance.toString().toRandAmount()}")
    }

    private fun populateViews() {
        val accounts: AccountList = AbsaCacheManager.getInstance().cachedAccountListObject
        selectAccount(accounts)
        accountRoundedSelectorView.setItemSelectionInterface {
            accountRoundedSelectorView.selectedValue = (accountObjectWrappers[it].formattedValue)
            investmentAmountNormalInputView.setDescription(getString(R.string.fixed_deposit_available_message, accountObjectWrappers[it].accountObject.availableBalanceFormated))
            viewModel.fixedDepositData.fromAccount = accountObjectWrappers[it].accountObject.accountNumber
            viewModel.fixedDepositData.accountDescription = accountObjectWrappers[it].accountObject.description
            selectedAccount = accountObjectWrappers[it]
            validateTransferAmount()
        }
    }

    private fun selectAccount(accountList: AccountList?) {
        accountObjectWrappers = SelectorList()
        if (accountList != null) {
            val transactionalAccounts = FilterAccountList.getTransactionalAndCreditAccounts(accountList.accountsList)
            if (transactionalAccounts != null) {
                for ((index, accountObject) in transactionalAccounts.withIndex()) {
                    accountObjectWrappers.add(AccountObjectWrapper(accountObject))
                    selectedAccount = (AccountObjectWrapper(accountObject))
                    if (accountRoundedSelectorView.selectedIndex == -1) {
                        accountRoundedSelectorView.selectedValue = selectedAccount.formattedValue
                        accountRoundedSelectorView.selectedIndex = index
                        investmentAmountNormalInputView.setDescription(getString(R.string.fixed_deposit_available_message, selectedAccount.accountObject.availableBalanceFormated))
                        viewModel.fixedDepositData.fromAccount = selectedAccount.accountObject.accountNumber
                        viewModel.fixedDepositData.accountDescription = selectedAccount.accountObject.description
                    }
                }
                accountRoundedSelectorView.setList(accountObjectWrappers, getString(R.string.select_account_toolbar_title))
            }
        }
    }

    private fun isValidInput(): Boolean {
        when {
            accountNameNormalInputView.selectedValueUnmasked.isEmpty() -> accountNameNormalInputView.setError(getString(R.string.fixed_deposit_account_name_error_message))
            accountNameNormalInputView.hasError() -> accountNameNormalInputView.focusAndShakeError()
            investmentAmountNormalInputView.selectedValueUnmasked.isEmpty() -> investmentAmountNormalInputView.setError(getString(R.string.fixed_deposit_investment_amount_error_message))
            investmentAmountNormalInputView.selectedValueUnmasked.toDouble() > accountObjectWrappers[accountRoundedSelectorView.selectedIndex].accountObject.availableBalance.amountDouble -> {
                investmentAmountNormalInputView.setError(getString(R.string.amount_exceeds_available))
            }
            investmentAmountNormalInputView.selectedValueUnmasked.toDouble() < 1000 -> investmentAmountNormalInputView.setError(getString(R.string.fixed_deposit_investment_amount_minimum_amount))
            fromAccountReferenceNormalInputView.selectedValue.isEmpty() -> fromAccountReferenceNormalInputView.setError(getString(R.string.fixed_deposit_from_account_reference_error_message))
            toAccountReferenceNormalInputView.selectedValue.isEmpty() -> toAccountReferenceNormalInputView.setError(getString(R.string.fixed_deposit_to_account_reference_error_message))
            accountRoundedSelectorView.selectedValue.isEmpty() -> accountRoundedSelectorView.setError(getString(R.string.fixed_deposit_select_account))
            else -> return true
        }
        return false
    }
}