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

package com.barclays.absa.banking.saveAndInvest

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.databinding.SaveAndInvestInterestPaymentDetailsFragmentBinding
import com.barclays.absa.banking.express.invest.getBankNames.BankNamesViewModel
import com.barclays.absa.banking.express.invest.getBranchNames.BranchNamesViewModel
import com.barclays.absa.banking.express.invest.getBranchNames.dto.BranchNameInfo
import com.barclays.absa.banking.express.invest.getBranchNames.dto.BranchNameRequest
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.FilterAccountList
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.viewModel
import styleguide.forms.Form
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toTitleCase

abstract class SaveAndInvestInterestPaymentDetailsFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_interest_payment_details_fragment) {

    protected val binding by viewBinding(SaveAndInvestInterestPaymentDetailsFragmentBinding::bind)
    private lateinit var bankNamesViewModel: BankNamesViewModel
    private lateinit var branchNamesViewModel: BranchNamesViewModel
    protected var investmentAccountName = ""

    abstract fun navigateOnValidFields()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bankNamesViewModel = viewModel()
        branchNamesViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchBanks()
        setupSelectors()
        setupListeners()
    }

    private fun setupSelectors() {
        val accountTypeList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.depositor_plus_cheque_account)))
            add(StringItem(getString(R.string.depositor_plus_savings_account)))
        }

        with(binding.accountTypeNormalInputView) {
            addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_choose_account_type))
            setList(accountTypeList, getString(R.string.depositor_plus_select_account_type))
            setItemSelectionInterface {
                selectedValue = accountTypeList[it].displayValue.toString()
                saveAndInvestViewModel.interestAccountType = selectedValue
                saveAndInvestViewModel.interestAccountTypeCode = if (selectedValue == getString(R.string.depositor_plus_savings_account)) AccountType.SAVINGS.code else AccountType.CHEQUE.code
            }
        }

        val accounts: MutableList<AccountObject> = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT).apply {
            add(0, AccountObject().apply { description = investmentAccountName })
            add(AccountObject().apply { description = getString(R.string.depositor_plus_another_bank_account) })
        }
        val accountList = accounts.toSelectorList { accountObject -> StringItem(accountObject.description, accountObject.accountBalanceAndNumberOrEmpty()) }

        with(binding.interestPaymentAccountNormalInputView) {
            addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_choose_interest_payment_account))
            setList(accountList, getString(R.string.depositor_plus_select_account))
            selectedIndex = 0
            setItemSelectionInterface {
                val selectedAccount = accounts[it]
                saveAndInvestViewModel.interestPaymentAccount = selectedAccount.description
                when (selectedAccount.description) {
                    investmentAccountName -> onInvestmentAccountSelected()
                    getString(R.string.depositor_plus_another_bank_account) -> onAnotherBankAccountSelected()
                    else -> {
                        selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description.toTitleCase(), selectedAccount.accountNumber)
                        onAbsaAccountSelected(selectedAccount)
                    }
                }
            }
        }

        saveAndInvestViewModel.interestPaymentAccount = saveAndInvestViewModel.interestPaymentAccount.ifBlank { accounts.first().description }

        with(binding) {
            interestPaymentAccountNormalInputView.selectedValue = saveAndInvestViewModel.interestPaymentAccount
            anotherBankAccountConstraintLayout.visibility = if (saveAndInvestViewModel.interestPaymentAccount == getString(R.string.depositor_plus_another_bank_account)) View.VISIBLE else View.GONE

            branchNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_choose_branch_code))
            referenceNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_enter_reference))
            accountNumberNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_enter_account_number))
        }
    }

    open fun onInvestmentAccountSelected() {
        binding.anotherBankAccountConstraintLayout.visibility = View.GONE
    }

    open fun onAnotherBankAccountSelected() {
        binding.anotherBankAccountConstraintLayout.visibility = View.VISIBLE
    }

    open fun onAbsaAccountSelected(selectedAccount: AccountObject) {
        with(saveAndInvestViewModel) {
            interestAccountNumber = selectedAccount.accountNumber
            interestAccountType = selectedAccount.accountType
            interestAccountTypeCode = if (selectedAccount.isSavingAccount) AccountType.SAVINGS.code else AccountType.CHEQUE.code
            interestBankName = BMBConstants.ABSA
            interestBranchCode = BMBConstants.ABSA_BRANCH_CODE
        }
        binding.anotherBankAccountConstraintLayout.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.branchNormalInputView.setCustomOnClickListener {
            when {
                saveAndInvestViewModel.interestBankName.isBlank() -> binding.bankNormalInputView.showError()
                binding.branchNormalInputView.selectedValue.isBlank() -> fetchBranchNames()
                else -> binding.branchNormalInputView.triggerListActivity()
            }
        }

        binding.nextButton.setOnClickListener {
            if (hasValidFields()) {
                when (binding.interestPaymentAccountNormalInputView.selectedValue) {
                    investmentAccountName -> clearAnotherBankDetails()
                    getString(R.string.depositor_plus_another_bank_account) -> saveAndInvestViewModel.interestAccountNumber = binding.accountNumberNormalInputView.selectedValueUnmasked
                }
                saveAndInvestViewModel.interestReference = binding.referenceNormalInputView.selectedValue
                navigateOnValidFields()
            }
        }
    }

    private fun fetchBanks() {
        if (!saveAndInvestViewModel.bankList.isNullOrEmpty()) {
            setupBankSelector()
            return
        }
        bankNamesViewModel.fetchBankNamesWithBranchCount()
        bankNamesViewModel.bankNamesLiveData.observe(viewLifecycleOwner, { bankList ->
            saveAndInvestViewModel.bankList = bankList.sortedBy { it.bankName }
            setupBankSelector()
            dismissProgressDialog()
            bankNamesViewModel.bankNamesLiveData.removeObservers(viewLifecycleOwner)
        })
    }

    private fun setupBankSelector() {
        with(binding.bankNormalInputView) {
            addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_choose_bank))
            setList(saveAndInvestViewModel.bankList.toSelectorList { bankNameInfo -> StringItem(bankNameInfo.bankName) }, getString(R.string.depositor_plus_select_bank))
            setItemSelectionInterface { bank ->
                val selectedBank = saveAndInvestViewModel.bankList[bank]
                val branchCode = if (selectedBank.bankName.contains(BMBConstants.ABSA, ignoreCase = true)) BMBConstants.ABSA_BRANCH_CODE else ""
                selectedValue = selectedBank.bankName.toTitleCase()
                saveAndInvestViewModel.interestBankName = selectedBank.bankName
                binding.branchNormalInputView.selectedValue = branchCode
                saveAndInvestViewModel.interestBranchCode = branchCode
            }
        }
    }

    private fun fetchBranchNames() {
        branchNamesViewModel.fetchBranchNames(BranchNameRequest("", saveAndInvestViewModel.interestBankName))
        branchNamesViewModel.branchNamesLiveData.observe(viewLifecycleOwner, { branchList ->
            dismissProgressDialog()
            with(binding.branchNormalInputView) {
                setList(branchList.toSelectorList { branch -> StringItem(branch.branchName, branch.branchCode) }, getString(R.string.depositor_plus_select_branch_code))
                setItemSelectionInterface { populateSelectedBranch(branchList[it]) }

                when {
                    branchList.size == 1 -> populateSelectedBranch(branchList.first())
                    saveAndInvestViewModel.interestBankName.contains(BMBConstants.ABSA, ignoreCase = true) -> {
                        populateSelectedBranch(branchList.find { branch -> branch.branchCode == BMBConstants.ABSA_BRANCH_CODE } ?: BranchNameInfo())
                    }
                    else -> triggerListActivity()
                }
            }
            branchNamesViewModel.branchNamesLiveData.removeObservers(viewLifecycleOwner)
        })
    }

    private fun populateSelectedBranch(selectedBranch: BranchNameInfo) {
        binding.branchNormalInputView.selectedValue = "${selectedBranch.branchName.toTitleCase()} (${selectedBranch.branchCode})"
        saveAndInvestViewModel.interestBranchCode = selectedBranch.branchCode
    }

    private fun AccountObject?.accountBalanceAndNumberOrEmpty(): String = when {
        !this?.accountNumber.isNullOrBlank() -> "${this?.accountNumberFormatted} (${this?.availableBalanceFormated})"
        else -> ""
    }

    private fun clearAnotherBankDetails() {
        with(saveAndInvestViewModel) {
            interestBankName = ""
            interestBranchCode = ""
            interestAccountType = ""
            interestAccountTypeCode = ""
            interestAccountNumber = ""
        }
    }

    private fun hasValidFields() = binding.interestPaymentAccountNormalInputView.validate()
            && Form(binding.anotherBankAccountConstraintLayout).isValid()
            && binding.referenceNormalInputView.validate()

    private enum class AccountType(val code: String) {
        CHEQUE("01"),
        SAVINGS("02")
    }
}