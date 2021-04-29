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

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.databinding.SaveAndInvestFundYourAccountFragmentBinding
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.InterestRateDetails
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.utils.*
import com.barclays.absa.utils.extensions.toRandAmount
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.Form
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import styleguide.forms.validation.*
import styleguide.utils.extensions.removeCurrency
import styleguide.utils.extensions.takeNumbersOrEmpty
import styleguide.utils.extensions.toTitleCase
import java.util.*

abstract class SaveAndInvestFundYourAccountFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_fund_your_account_fragment) {

    open lateinit var startDatePickerDialog: DatePickerDialog
    open lateinit var endDatePickerDialog: DatePickerDialog
    private var recurringPaymentAccount = AccountObject()

    protected var interestRates = listOf<InterestRateDetails>()
    protected val binding by viewBinding(SaveAndInvestFundYourAccountFragmentBinding::bind)

    open var minimumRecurringAmount: Double = 0.00
    open var minimumRecurringPayments: Int = 1
    open var defaultAccountName: String = ""

    open fun calculateInterestRate(initialDeposit: String): String = ""

    open fun setupRecurringDatePickers() {
        startDatePickerDialog = binding.recurringStartDateNormalInputView.setupDatePickerDialog().apply {
            with(GregorianCalendar()) {
                datePicker.minDate = apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                datePicker.maxDate = apply { add(Calendar.YEAR, 1) }.timeInMillis
            }
        }
        endDatePickerDialog = binding.recurringEndDateNormalInputView.setupDatePickerDialog()
    }

    abstract fun navigateOnTransferLimitSuccess()
    abstract fun fetchInterestRates()

    protected val minimumMonthlyDeposits: Int
        get() = saveAndInvestViewModel.saveAndInvestProductInfo.minimumInvestmentPeriod.takeNumbersOrEmpty().ifEmpty { minimumRecurringPayments.toString() }.toInt()

    protected val minimumInvestmentAmount: Double
        get() = saveAndInvestViewModel.saveAndInvestProductInfo.minimumInvestmentAmount

    private val maximumAmount: Double
        get() = interestRates.lastOrNull()?.toBalance ?: 0.00

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.initialDepositNormalInputView.descriptionTextView?.text = getString(R.string.future_plan_minimum_initial_deposit, minimumInvestmentAmount.toRandAmount())

        fetchInterestRates()
        setupSelectors()
        setupRecurringDatePickers()
        setupTextWatchers()
        setupListeners()
    }

    protected fun setAmountInputViewsMaximumValidation() {
        binding.amountNormalInputView.addValidationRule(MaximumAmountValidationRule(maximumAmount, false, errorMessage = getString(R.string.depositor_plus_maximum_amount_error, maximumAmount.toRandAmount())))
        binding.initialDepositNormalInputView.addValidationRule(MaximumAmountValidationRule(maximumAmount, true, errorMessage = getString(R.string.depositor_plus_maximum_amount_error, maximumAmount.toRandAmount())))
    }

    private fun setupSelectors() {
        val accounts: AccountList = AbsaCacheManager.getInstance().cachedAccountListObject
        val transactionalAccounts = FilterAccountList.getTransactionalAccounts(accounts.accountsList)
        val transactionalAccountList = transactionalAccounts.toSelectorList { accountObject -> AccountObjectWrapper(accountObject) }

        binding.accountToDebitNormalInputView.setupAccountSelector(transactionalAccountList) { account ->
            when {
                binding.initialDepositNormalInputView.selectedValueUnmasked.isEmpty() -> {
                    binding.accountToDebitNormalInputView.clear()
                    binding.initialDepositNormalInputView.validate()
                }
                account.availableBalance.amountDouble < binding.initialDepositNormalInputView.selectedValueUnmasked.toDouble() -> {
                    binding.accountToDebitNormalInputView.setError(getString(R.string.depositor_plus_account_to_debit_error, account.availableBalanceFormated))
                    saveAndInvestViewModel.initialDepositAccount = account
                }
                else -> {
                    saveAndInvestViewModel.initialDepositAccount = account

                    if (binding.accountNormalInputView.selectedValue.isBlank()) {
                        binding.accountNormalInputView.selectedIndex = transactionalAccountList.indexOfFirst { it == account }
                        binding.accountNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(account.description.toTitleCase(), account.accountNumber)
                        recurringPaymentAccount = account
                    }
                }
            }
        }

        binding.accountNormalInputView.setupAccountSelector(transactionalAccountList) { account ->
            recurringPaymentAccount = account
        }
    }

    private fun <T : SelectorInterface> NormalInputView<T>.setupAccountSelector(transactionalAccountList: SelectorList<AccountObjectWrapper>, selection: (AccountObject) -> Unit) {
        with(this) {
            addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_select_an_account))
            setList(transactionalAccountList, getString(R.string.depositor_plus_select_account))
            setItemSelectionInterface {
                val selectedAccount: AccountObject = transactionalAccountList[it].accountObject
                selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description.toTitleCase(), selectedAccount.accountNumber)
                selection(selectedAccount)
            }
        }
    }

    protected fun <T : SelectorInterface> NormalInputView<T>.setupDatePickerDialog(): DatePickerDialog {
        with(this) {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(baseActivity, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                selectedValue = DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN)
                updateNumberOfPayments()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            setOnClickListener {
                datePickerDialog.show()
            }
            return datePickerDialog
        }
    }

    private fun <T : SelectorInterface> NormalInputView<T>.onAmountChanged() {
        descriptionTextView?.let {
            if (it.text.isNotBlank() && !it.isVisible) {
                showDescription(true)
            }
        }
        if (selectedValueUnmasked.toDouble() > maximumAmount) {
            validate()
        }
    }

    private fun setupTextWatchers() {
        with(binding) {
            accountNameNormalInputView.selectedValue = defaultAccountName
            accountNameNormalInputView.addRequiredValidationHidingTextWatcher(R.string.depositor_plus_please_enter_account_name)
            referenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.depositor_plus_please_enter_reference)
            recurringReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.depositor_plus_please_enter_reference)
        }

        with(binding.amountNormalInputView) {
            addValidationRules(FieldRequiredValidationRule(R.string.depositor_plus_please_enter_amount), MinimumAmountValidationRule(minimumRecurringAmount, false, errorMessage = getString(R.string.depositor_plus_initial_deposit_error, minimumRecurringAmount.toRandAmount())))
            addRequiredValidationHidingTextWatcher { onAmountChanged() }
        }

        with(binding.initialDepositNormalInputView) {
            addValidationRules(FieldRequiredValidationRule(R.string.depositor_plus_please_enter_amount), MinimumAmountValidationRule(minimumInvestmentAmount, true, errorMessage = getString(R.string.depositor_plus_initial_deposit_error, minimumInvestmentAmount.toRandAmount())))
            addRequiredValidationHidingTextWatcher {
                val initialDeposit = binding.initialDepositNormalInputView.selectedValue.removeCurrency().trim()
                if (initialDeposit.isNotEmpty() && initialDeposit.toDouble() >= minimumInvestmentAmount) {
                    binding.interestRateLineItemView.setLineItemViewContent(calculateInterestRate(selectedValueUnmasked))
                } else {
                    binding.interestRateLineItemView.setLineItemViewContent("")
                }
                onAmountChanged()
            }
        }

        with(binding.numberOfPaymentsNormalInputView) {
            addValidationRules(FieldRequiredValidationRule(errorMessage = getString(R.string.depositor_plus_number_of_payments_minimum, minimumMonthlyDeposits)),
                    MinimumAmountValidationRule(minimumMonthlyDeposits.toDouble(), false, errorMessage = getString(R.string.depositor_plus_number_of_payments_minimum, minimumMonthlyDeposits)))
            addRequiredValidationHidingTextWatcher {
                if (binding.recurringStartDateNormalInputView.selectedValue.isBlank()) {
                    with(GregorianCalendar()) {
                        add(Calendar.DAY_OF_MONTH, 1)
                        startDatePickerDialog.updateDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
                        binding.recurringStartDateNormalInputView.selectedValue = DateUtils.format(time, DateUtils.DATE_DISPLAY_PATTERN)
                    }
                }
                updateRecurringEndDate()
            }
            selectedValue = if (saveAndInvestViewModel.numberOfPayments.toInt() > minimumMonthlyDeposits) saveAndInvestViewModel.numberOfPayments else minimumMonthlyDeposits.toString()
        }
    }

    private fun updateRecurringEndDate() {
        if (binding.numberOfPaymentsNormalInputView.selectedValue.isNotBlank()) {
            val numberOfPayments = binding.numberOfPaymentsNormalInputView.selectedValue.toInt()
            with(startDatePickerDialog.datePicker) {
                val calendar = GregorianCalendar(year, month, dayOfMonth).apply { add(Calendar.MONTH, numberOfPayments - 1) }
                endDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), dayOfMonth)
                endDatePickerDialog.datePicker.minDate = GregorianCalendar(year, month, dayOfMonth).timeInMillis
                binding.recurringEndDateNormalInputView.selectedValue = DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN)
            }
        }
    }

    private fun updateNumberOfPayments() {
        if (binding.recurringEndDateNormalInputView.selectedValue.isBlank()) {
            updateRecurringEndDate()
            return
        }
        val yearDifference = endDatePickerDialog.datePicker.year - startDatePickerDialog.datePicker.year
        val monthDifference = yearDifference * 12 + endDatePickerDialog.datePicker.month - startDatePickerDialog.datePicker.month + 1
        binding.numberOfPaymentsNormalInputView.selectedValue = if (monthDifference >= 1) monthDifference.toString() else "1"
    }

    private fun setupListeners() {
        binding.recurringDepositCheckBoxView.setOnCheckedListener { isChecked ->
            binding.recurringPaymentConstraintGroup.visibility = if (isChecked) {
                View.VISIBLE
            } else {
                clearRecurringData()
                View.GONE
            }
        }

        binding.nextButton.setOnClickListener {
            if (binding.maturityDateNormalInputView.isVisible && binding.maturityDateNormalInputView.hasError()) {
                binding.maturityDateNormalInputView.focusAndShakeError()
                return@setOnClickListener
            }

            if (binding.accountToDebitNormalInputView.selectedValueUnmasked.isNotBlank() && binding.initialDepositNormalInputView.selectedValueUnmasked.isNotBlank() && saveAndInvestViewModel.initialDepositAccount.availableBalance.amountDouble < binding.initialDepositNormalInputView.selectedValueUnmasked.toDouble()) {
                binding.accountToDebitNormalInputView.setError(getString(R.string.depositor_plus_account_to_debit_error, saveAndInvestViewModel.initialDepositAccount.availableBalanceFormated))
                return@setOnClickListener
            }
            if (allFieldsValid()) {
                with(saveAndInvestViewModel) {
                    accountName = binding.accountNameNormalInputView.selectedValue
                    initialDepositAmount = binding.initialDepositNormalInputView.selectedValueUnmasked
                    initialDepositReference = binding.referenceNormalInputView.selectedValue

                    if (binding.recurringPaymentConstraintGroup.isVisible) {
                        recurringPaymentAccount = this@SaveAndInvestFundYourAccountFragment.recurringPaymentAccount
                        recurringPaymentAmount = binding.amountNormalInputView.selectedValueUnmasked
                        recurringPaymentStartDate = binding.recurringStartDateNormalInputView.selectedValue
                        recurringPaymentEndDate = binding.recurringEndDateNormalInputView.selectedValue
                        recurringPaymentReference = binding.recurringReferenceNormalInputView.selectedValue
                        numberOfPayments = binding.numberOfPaymentsNormalInputView.selectedValue
                    }
                }
                checkTransferLimit()
            }
        }
    }

    private fun clearRecurringData() {
        with(saveAndInvestViewModel) {
            recurringPaymentAmount = ""
            recurringPaymentAccount = AccountObject()
            recurringPaymentStartDate = ""
            recurringPaymentEndDate = ""
            recurringPaymentReference = ""
            numberOfPayments = ""
        }
    }

    private fun checkTransferLimit() {
        DigitalLimitsHelper.checkTransferAmount(baseActivity, Amount(saveAndInvestViewModel.initialDepositAmount), getString(R.string.depositor_plus_transfer_limit_increase_message))
        DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, {
            if (it == DigitalLimitState.CHANGED || it == DigitalLimitState.UNCHANGED) {
                navigateOnTransferLimitSuccess()
            }
            dismissProgressDialog()
            DigitalLimitsHelper.digitalLimitState.removeObservers(viewLifecycleOwner)
        })
    }

    private fun allFieldsValid() = Form(binding.containerConstraintLayout).isValid()
}