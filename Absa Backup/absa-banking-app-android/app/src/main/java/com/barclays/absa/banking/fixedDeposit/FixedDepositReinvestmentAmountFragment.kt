/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FilterAccountList
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.extensions.toSelectorList
import kotlinx.android.synthetic.main.fixed_deposit_maintenance_activity.*
import kotlinx.android.synthetic.main.fixed_deposit_reinvestment_amount_fragment.*
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.toTitleCase

class FixedDepositReinvestmentAmountFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_reinvestment_amount_fragment) {

    private lateinit var balance: String
    private var additionalAmount = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_custom_instruction)
        fixedDepositMaintenanceActivity.progressIndicatorView.visibility = View.VISIBLE
        fixedDepositMaintenanceActivity.setProgressStep(1)

        balance = fixedDepositViewModel.renewalInstructionResponse.value?.renewalInstructionDetails?.depositBalance ?: ""
        amountInputView.selectedValue = balance
        setupAccountSelector()
        setupTextWatchers()

        nextButton.setOnClickListener {
            if (hasValidFields()) {
                fixedDepositViewModel.renewalInstructionData.apply {
                    amount = amountInputView.selectedValueUnmasked
                    if (additionalFundsConstraintLayout.isVisible) {
                        fundAmount = additionalAmount
                        fromAccountStatementDescription = fromAccountReferenceNormalInputView.selectedValue
                        toAccountStatementDescription = toAccountReferenceNormalInputView.selectedValue
                    }
                }
                fixedDepositViewModel.investmentTerm = MutableLiveData<String>()
                navigate(FixedDepositReinvestmentAmountFragmentDirections.actionFixedDepositReinvestmentAmountFragmentToFixedDepositReinvestmentTermFragment())
            }
        }
    }

    private fun setupTextWatchers() {
        fromAccountNormalInputView.addRequiredValidationHidingTextWatcher()
        fromAccountReferenceNormalInputView.addRequiredValidationHidingTextWatcher()
        toAccountReferenceNormalInputView.addRequiredValidationHidingTextWatcher()
        amountInputView.addRequiredValidationHidingTextWatcher {
            if (amountInputView.selectedValueUnmasked.toDouble() > balance.toDouble()) {
                additionalAmount = (amountInputView.selectedValueUnmasked.toDouble() - balance.toDouble()).toString()
                amountInputView.setDescription(getString(R.string.fixed_deposit_additional_funds, TextFormatUtils.formatBasicAmountAsRand(additionalAmount)))
                additionalFundsConstraintLayout.visibility = View.VISIBLE
            } else {
                amountInputView.showDescription(false)
                additionalFundsConstraintLayout.visibility = View.GONE
            }
        }
    }

    private fun setupAccountSelector() {
        val accounts: AccountList = AbsaCacheManager.getInstance().cachedAccountListObject
        val transactionalAccounts = FilterAccountList.getTransactionalAndCreditAccounts(accounts.accountsList)
        val transactionalAccountList = transactionalAccounts.toSelectorList { accountObject -> AccountObjectWrapper(accountObject) }
        fromAccountNormalInputView.setList(transactionalAccountList, getString(R.string.select_account_toolbar_title))
        fromAccountNormalInputView.setItemSelectionInterface {
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDeposit_CustomInstructionStep1Screen_AccountSelected")
            val selectedAccount = transactionalAccountList[it].accountObject
            fromAccountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description.toTitleCase(), selectedAccount.accountNumber)
            fixedDepositViewModel.renewalInstructionData.fromAccount = selectedAccount.accountNumber
            if (selectedAccount.availableBalance.amountDouble < additionalAmount.toDouble()) {
                fromAccountNormalInputView.setError(getString(R.string.fixed_deposit_account_selection_error, selectedAccount.availableBalanceFormated))
            }
        }
    }

    private fun hasValidFields(): Boolean {
        when {
            amountInputView.selectedValueUnmasked.toDouble() < 1000 -> amountInputView.setError(getString(R.string.fixed_deposit_investment_amount_minimum_amount))
            additionalFundsConstraintLayout.isVisible -> when {
                fromAccountNormalInputView.hasError() -> AnimationHelper.shakeShakeAnimate(fromAccountNormalInputView)
                fromAccountNormalInputView.selectedValue.isEmpty() -> fromAccountNormalInputView.setError(R.string.please_select_account_error)
                fromAccountReferenceNormalInputView.selectedValue.isEmpty() -> fromAccountReferenceNormalInputView.setError(R.string.fixed_deposit_from_account_reference_error_message)
                toAccountReferenceNormalInputView.selectedValue.isEmpty() -> toAccountReferenceNormalInputView.setError(R.string.fixed_deposit_to_account_reference_error_message)
                else -> return true
            }
            else -> return true
        }
        return false
    }
}