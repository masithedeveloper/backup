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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthFromDays
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthToDays
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.fixedDeposit.services.dto.AccountTypes
import com.barclays.absa.banking.fixedDeposit.services.dto.InterestRateTable
import com.barclays.absa.banking.fixedDeposit.services.dto.TermDepositInterestRateDayTable
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ApplicationFlowType
import com.barclays.absa.banking.payments.ChooseBankListActivity
import com.barclays.absa.banking.payments.ChooseBranchListActivity
import com.barclays.absa.banking.payments.PaySomeoneFragment.REQUEST_CODE_FOR_BANK_NAME
import com.barclays.absa.banking.payments.PaySomeoneFragment.REQUEST_CODE_FOR_BRANCH_NAME
import com.barclays.absa.banking.payments.PaymentsConstants
import com.barclays.absa.banking.payments.PaymentsConstants.BANK_NAME
import com.barclays.absa.banking.payments.PaymentsConstants.BRANCH_CODE
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.utils.*
import com.barclays.absa.utils.DateUtils.DASHED_DATE_PATTERN
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import kotlinx.android.synthetic.main.fixed_deposit_new_fixed_deposit_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toTitleCaseRemovingCommas
import java.util.*
import java.util.concurrent.TimeUnit

class FixedDepositNewFixedDepositDetailsFragment : BaseFragment(R.layout.fixed_deposit_new_fixed_deposit_details_fragment) {

    private lateinit var paymentFrequencyList: SelectorList<StringItem>
    private lateinit var listOfAccountToPayInTo: SelectorList<AccountObjectWrapper>
    private lateinit var viewModel: FixedDepositViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var dialog: AlertDialog

    private lateinit var calendarMin: GregorianCalendar
    private lateinit var calendarMax: GregorianCalendar

    private var accountTypes: List<AccountTypes>? = null
    private var capFrequencyList: ArrayList<String> = arrayListOf()
    private var accountToPayInto = AccountObject()
    private var clientAgreement: Int = 0
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit New Fixed Deposit Details Screen")

        (activity as FixedDepositActivity).setToolbarTitle(getString(R.string.fixed_deposit_new_fixed_deposit))
        (activity as FixedDepositActivity).showToolbar()

        if (!::viewModel.isInitialized) {
            initialiseViewModel()
        }
        initialiseMinMaxDate()
        setUpPaymentFrequencyList()
        setUpInterestSelectAccountList()
        setUpAccountSelectionLogic()

        interestPaymentDayNormalInputView.editText?.isEnabled = false
        interestPaymentDayNormalInputView.selectedValue = "1"

        setUpComponentListeners()
        setUpAccountTypeList()
        setUpObservers()
    }

    private fun initialiseMinMaxDate() {
        calendarMin = GregorianCalendar()
        calendarMin.add(Calendar.DAY_OF_YEAR, 8)

        calendarMax = GregorianCalendar()
        calendarMax.add(Calendar.DAY_OF_YEAR, 1826)
    }

    private fun setUpAccountSelectionLogic() {
        absaAccountToPayFromRadioButtonView.setItemCheckedInterface {
            viewModel.fixedDepositData.selectedAbsaAccountType = it
            if (it == 0) {
                toggleVisibilityOfBankInputFields(View.GONE)
            } else {
                toggleVisibilityOfBankInputFields(View.VISIBLE)

                bankNormalInputView.selectedValue = "Absa"
                branchNormalInputView.selectedValue = "632 005"
                bankNormalInputView.isEnabled = false
                branchNormalInputView.isEnabled = false
            }

            viewModel.fixedDepositData.bankName = "Absa"
            viewModel.fixedDepositData.branchCode = "632005"
        }

        val responseObject = PayBeneficiaryPaymentObject()
        AbsaCacheManager.getInstance().getModelForAccounts(responseObject, ApplicationFlowType.ACCT_SUMMARY)
        listOfAccountToPayInTo = SelectorList()
        if (responseObject.fromAccounts != null) {
            for (accountObject in responseObject.fromAccounts!!) {
                listOfAccountToPayInTo.add(AccountObjectWrapper(accountObject))
            }
            selectAccountSelectorNormalInputView.setList(listOfAccountToPayInTo, getString(R.string.account_to_pay_from))
        }

        selectAccountSelectorNormalInputView.setItemSelectionInterface {
            accountToPayInto = listOfAccountToPayInTo[it].accountObject
            selectAccountSelectorNormalInputView.selectedValue = accountToPayInto.accountInformation

            val accountCode = accountTypes?.find { types -> types.accountType == accountToPayInto.accountType }?.code
            if (accountCode != null) {
                viewModel.fixedDepositData.interestToAccountType = accountCode
            }

            when {
                accountToPayInto.accountType == "currentAccount" -> {
                    viewModel.fixedDepositData.accountType = getString(R.string.current_account)
                }
                accountToPayInto.accountType == "savingsAccount" -> {
                    viewModel.fixedDepositData.accountType = getString(R.string.savings_account)
                }
                accountToPayInto.accountType == "creditCard" -> {
                    viewModel.fixedDepositData.accountType = getString(R.string.credit_card)
                }
            }
        }
    }

    private fun setUpComponentListeners() {
        sourceOfFundsNormalInputView.setOnClickListener {
            navigate(FixedDepositNewFixedDepositDetailsFragmentDirections.actionFixedDepositNewFixedDepositDetailsFragmentToFixedDepositSourceOfFundsFragment())
        }

        clientAgreement = if (CustomerProfileObject.instance.clientTypeGroup.isBusiness()) R.string.business_client_agreement else R.string.personal_client_agreement

        if (absaCacheService.isPersonalClientAgreementAccepted()) {
            ClientAgreementHelper.updateClientAgreementContainer(personalClientAgreementCheckBoxView, true, R.string.client_agreement_have_accepted, clientAgreement, performCAClickOnClientAgreement)
        } else {
            ClientAgreementHelper.updateClientAgreementContainer(personalClientAgreementCheckBoxView, false, R.string.accept_personal_client_agreement, clientAgreement, performCAClickOnClientAgreement)

        }

        interestPaymentFrequencyNormalInputView.setItemSelectionInterface {
            viewModel.fixedDepositData.capFrequencyCode = capFrequencyList[it]
            viewModel.fixedDepositData.interestRate = paymentFrequencyList[it].item2!!
            interestPaymentFrequencyNormalInputView.setDescription(String.format("%s: %s", getString(R.string.fixed_deposit_interest_rate_per_annum), paymentFrequencyList[it].item2))

            if (it == 4) {
                maturitySelected()
            } else {
                interestPaymentDayNormalInputView.visibility = View.VISIBLE
                interestPaymentDayNormalInputView.editText?.isEnabled = true

                maturityNotSelected()

                if (interestPaymentFrequencyNormalInputView.selectedIndex != -1 && viewModel.fixedDepositData.maturityDate.isNotEmpty()) {
                    checkMaturityDate(DateUtils.getCalendar(viewModel.fixedDepositData.maturityDate, DASHED_DATE_PATTERN))
                }
            }

            viewModel.fixedDepositData.interestFrequency = interestPaymentFrequencyNormalInputView.selectedValue
            setNextPaymentDate()
            setCalendarMinDate()
        }

        investmentTermNormalInputView.setOnClickListener {
            var minimumMonth = 0
            if (interestPaymentFrequencyNormalInputView.selectedValueUnmasked.isNotEmpty()) {
                minimumMonth = viewModel.fixedDepositData.capFrequencyCode.toInt()
                if (minimumMonth == 1) {
                    minimumMonth = 0
                }
            }
            navigate(FixedDepositNewFixedDepositDetailsFragmentDirections.actionFixedDepositNewFixedDepositDetailsFragmentToFixedDepositInvestmentTermFragment(minimumMonth))
        }

        interestPaymentDayNormalInputView.setOnClickListener {
            selectDebitDay()
        }

        maturityDateNormalInputView.setOnClickListener {
            if (maturityDateNormalInputView.isEnabled) {

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.time = DateUtils.getDate(maturityDateNormalInputView.selectedValue, DATE_DISPLAY_PATTERN)

                val datePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme, { _, year, month, day ->
                    selectedCalendar.set(year, month, day)

                    if (selectedCalendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || selectedCalendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                        showMessage(getString(R.string.fixed_deposit_invalid_date), getString(R.string.fixed_deposit_date_weekend_error)) { _, _ -> }
                    } else {
                        maturityDateNormalInputView.clearError()
                        maturityDateNormalInputView.selectedValue = DateUtils.format(selectedCalendar.time, DATE_DISPLAY_PATTERN)

                        viewModel.fixedDepositData.displayMaturityDate = DateUtils.format(selectedCalendar.time, DATE_DISPLAY_PATTERN)
                        viewModel.fixedDepositData.maturityDate = DateUtils.format(selectedCalendar.time, DASHED_DATE_PATTERN)

                        val calendar = GregorianCalendar()
                        var daysBetween = selectedCalendar.timeInMillis - calendar.timeInMillis
                        daysBetween = TimeUnit.DAYS.convert(daysBetween, TimeUnit.MILLISECONDS) + 1

                        val months = getMonthFromDays(viewModel.interestRateInfo.value?.interestRateTable, daysBetween.toInt())

                        if (months != 0 && investmentTermNormalInputView.selectedValue.contains(getString(R.string.fixed_deposit_month).toLowerCase(BMBApplication.getApplicationLocale()))) {
                            if (months > 1) {
                                investmentTermNormalInputView.selectedValue = months.toString() + " " + getString(R.string.fixed_deposit_months).toLowerCase(BMBApplication.getApplicationLocale())
                            } else {
                                investmentTermNormalInputView.selectedValue = months.toString() + " " + getString(R.string.fixed_deposit_month).toLowerCase(BMBApplication.getApplicationLocale())
                            }
                        } else {
                            investmentTermNormalInputView.selectedValue = daysBetween.toString() + " " + getString(R.string.fixed_deposit_days).toLowerCase(BMBApplication.getApplicationLocale())
                        }

                        viewModel.fixedDepositData.investmentTerm = investmentTermNormalInputView.selectedValue
                        viewModel.investmentTerm.value = investmentTermNormalInputView.selectedValue

                        if (interestPaymentFrequencyNormalInputView.selectedIndex != -1) {
                            checkMaturityDate(selectedCalendar)
                        }
                    }
                }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH))

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context?.getString(R.string.cancel), datePickerDialog)
                val datePicker = datePickerDialog.datePicker
                datePicker.minDate = calendarMin.timeInMillis
                datePicker.maxDate = calendarMax.timeInMillis
                datePickerDialog.show()
            }
        }

        nextButton.setOnClickListener {
            viewModel.fixedDepositData.interestToAccountNumber = if (accountNumberNormalInputView.visibility == View.VISIBLE) accountNumberNormalInputView.selectedValueUnmasked else accountToPayInto.accountNumber.removeSpaces()
            viewModel.fixedDepositData.paymentReference = paymentReferenceNormalInputView.selectedValue
            viewModel.fixedDepositData.bankName = bankNormalInputView.selectedValue
            viewModel.fixedDepositData.branchCode = branchNormalInputView.selectedValue

            if (isValidInput()) {
                if (!absaCacheService.isPersonalClientAgreementAccepted()) {
                    viewModel.updatePersonalClientAgreement()
                } else {
                    navigate(FixedDepositNewFixedDepositDetailsFragmentDirections.actionFixedDepositNewFixedDepositDetailsFragmentToFixedDepositTermsAndConditionsFragment(true))
                }
            }
        }

        payInterestIntoNormalInputView.setItemSelectionInterface {
            interestIntoItemSelected()
        }

        accountTypeNormalInputView.setItemSelectionInterface {
            viewModel.fixedDepositData.interestToAccountType = accountTypes?.get(it)?.code.toString()
            viewModel.fixedDepositData.accountType = accountTypes?.get(it)?.description.toString()
        }

        accountNumberNormalInputView.addRequiredValidationHidingTextWatcher()
        paymentReferenceNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun setUpObservers() {
        viewModel.investmentTerm.removeObservers(this)
        viewModel.investmentTerm.observe(viewLifecycleOwner, { investmentTerm ->
            if (investmentTermNormalInputView.selectedValue != investmentTerm) {
                investmentTermNormalInputView.selectedValue = investmentTerm
                viewModel.fixedDepositData.investmentTerm = investmentTerm!!

                val term = investmentTerm.split(" ")[0].toInt()
                val calendar = GregorianCalendar()

                if (investmentTerm.contains(getString(R.string.fixed_deposit_month), true)) {
                    calendar.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, term))
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, term)
                }

                if (calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) {
                    calendar.add(Calendar.DAY_OF_YEAR, 2)
                } else if (calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                setNextPaymentDate()
                setCalendarMinDate()

                viewModel.fixedDepositData.displayMaturityDate = DateUtils.format(calendar.time, DATE_DISPLAY_PATTERN)
                viewModel.fixedDepositData.maturityDate = DateUtils.format(calendar.time, DASHED_DATE_PATTERN)

                maturityDateNormalInputView.selectedValue = viewModel.fixedDepositData.displayMaturityDate

                calculateRateTable()

                if (viewModel.fixedDepositData.interestFrequency.isNotEmpty() && viewModel.fixedDepositData.maturityDate.isNotEmpty()) {
                    checkMaturityDate(DateUtils.getCalendar(viewModel.fixedDepositData.maturityDate, DASHED_DATE_PATTERN))
                } else {
                    investmentTermNormalInputView.clearError()
                    maturityDateNormalInputView.clearError()
                }
            }
        })

        viewModel.successResponse.removeObservers(this)
        viewModel.successResponse = MutableLiveData()
        viewModel.successResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigate(FixedDepositNewFixedDepositDetailsFragmentDirections.actionFixedDepositNewFixedDepositDetailsFragmentToFixedDepositTermsAndConditionsFragment(true))
        })

        viewModel.bankBranches.removeObservers(this)
        viewModel.bankBranches.observe(viewLifecycleOwner, {
            if (it?.branchList != null && it.branchList?.size == 1) {
                branchNormalInputView.selectedValue = it.branchList!![0].branchCode.toString()
                branchNormalInputView.isEnabled = false
            } else {
                branchNormalInputView.isEnabled = true
            }
            dismissProgressDialog()
        })

        sharedViewModel.selectedSourceOfFunds.removeObservers(this)
        sharedViewModel.selectedSourceOfFunds.observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                val sourceOfFunds = StringBuilder()
                val sourceOfFundsCode = StringBuilder()
                for (item in it) {
                    sourceOfFunds.append(item.defaultLabel.toTitleCaseRemovingCommas()).append(", ")
                    sourceOfFundsCode.append(item.itemCode).append("|")
                }
                sourceOfFunds.delete(sourceOfFunds.length - 2, sourceOfFunds.length)
                sourceOfFundsCode.deleteCharAt(sourceOfFundsCode.length - 1)

                sourceOfFundsNormalInputView.selectedValue = sourceOfFunds.toString()
                viewModel.fixedDepositData.sourceOfFunds = sourceOfFundsCode.toString()
                interestIntoItemSelected()
            } else {
                sourceOfFundsNormalInputView.selectedValue = ""
            }

            sharedViewModel.selectedSourceOfFunds.removeObservers(this)
        })
    }

    private fun interestIntoItemSelected() {
        viewModel.fixedDepositData.payInterestInto = payInterestIntoNormalInputView.selectedValue

        if (payInterestIntoNormalInputView.selectedIndex == 0) {
            if (viewModel.fixedDepositData.selectedAbsaAccountType == 0) {
                toggleVisibilityOfBankInputFields(View.GONE)
            } else {
                toggleVisibilityOfBankInputFields(View.VISIBLE)
            }
            absaAccountToPayFromRadioButtonView.visibility = View.VISIBLE
            paymentReferenceNormalInputView.visibility = View.VISIBLE
            viewModel.fixedDepositData.bankName = "Absa"
            viewModel.fixedDepositData.branchCode = "632005"
        } else if (payInterestIntoNormalInputView.selectedIndex == 1) {
            if (viewModel.fixedDepositData.bankName == "Absa") {
                viewModel.fixedDepositData.bankName = ""
                viewModel.fixedDepositData.branchCode = ""
            }
            bankNormalInputView.selectedValue = ""
            branchNormalInputView.selectedValue = ""
            accountTypeNormalInputView.selectedValue = ""
            accountNumberNormalInputView.selectedValue = ""
            branchNormalInputView.isEnabled = false
            absaAccountToPayFromRadioButtonView.visibility = View.GONE
            paymentReferenceNormalInputView.visibility = View.VISIBLE
            toggleVisibilityOfBankInputFields(View.VISIBLE)
            bankNormalInputView.isEnabled = true

            viewModel.fetchBankList()

            viewModel.bankDetails.removeObservers(this)
            viewModel.bankDetails.observe(viewLifecycleOwner, {
                dismissProgressDialog()
            })

            bankNormalInputView.setOnClickListener {
                val chooseBankIntent = Intent(BMBApplication.getInstance().topMostActivity, ChooseBankListActivity::class.java)
                chooseBankIntent.putExtra(PaymentsConstants.BANK_LIST, viewModel.bankDetails.value)
                startActivityForResult(chooseBankIntent, REQUEST_CODE_FOR_BANK_NAME)
            }
        }

        if (::viewModel.isInitialized) {
            bankNormalInputView.selectedValue = viewModel.fixedDepositData.bankName
            branchNormalInputView.selectedValue = viewModel.fixedDepositData.branchCode
            accountTypeNormalInputView.selectedValue = viewModel.fixedDepositData.accountType
            accountNumberNormalInputView.selectedValue = viewModel.fixedDepositData.interestToAccountNumber
        }
    }

    private fun checkMaturityDate(selectedCalendar: Calendar) {
        val futureDate = Calendar.getInstance()
        if (viewModel.fixedDepositData.capFrequencyCode.toInt() != 1) {
            futureDate.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, viewModel.fixedDepositData.capFrequencyCode.toInt()))
        }

        futureDate.add(Calendar.DAY_OF_YEAR, -1)
        if (futureDate.after(selectedCalendar)) {
            maturityDateNormalInputView.setError(getString(R.string.fixed_deposit_maturity_date_after, DateUtils.format(futureDate.time, DATE_DISPLAY_PATTERN)))
            investmentTermNormalInputView.setError(getString(R.string.fixed_deposit_please_select_a_valid_investment_term))
            interestPaymentFrequencyNormalInputView.setError(getString(R.string.fixed_deposit_please_select_a_valid_frequency))
        } else {
            maturityDateNormalInputView.clearError()
            investmentTermNormalInputView.clearError()
            interestPaymentFrequencyNormalInputView.clearError()
        }
    }

    private fun maturitySelected() {
        interestPaymentDayNormalInputView.visibility = View.GONE
        viewModel.fixedDepositData.interestPaymentDay = "1"
    }

    private fun maturityNotSelected() {
        payInterestIntoNormalInputView.visibility = View.VISIBLE
    }

    private fun isValidInput(): Boolean {
        when {
            interestPaymentFrequencyNormalInputView.selectedValue.isEmpty() -> interestPaymentFrequencyNormalInputView.setError(getString(R.string.fixed_deposit_please_choose_payment_frequency))
            maturityDateNormalInputView.selectedValue.isEmpty() -> maturityDateNormalInputView.setError(getString(R.string.fixed_deposit_please_select_a_maturity_date))
            sourceOfFundsNormalInputView.selectedValue.isEmpty() -> sourceOfFundsNormalInputView.setError(getString(R.string.fixed_deposit_please_select_a_source_of_fund))
            investmentTermNormalInputView.hasError() -> investmentTermNormalInputView.focusAndShakeError()
            maturityDateNormalInputView.hasError() -> maturityDateNormalInputView.focusAndShakeError()
            sourceOfFundsNormalInputView.hasError() -> sourceOfFundsNormalInputView.focusAndShakeError()
            payInterestIntoNormalInputView.selectedValue.isEmpty() -> payInterestIntoNormalInputView.setError(getString(R.string.fixed_deposit_please_choose_an_account_to_be_credited))
            selectAccountSelectorNormalInputView.visibility == View.VISIBLE && selectAccountSelectorNormalInputView.selectedValue.isEmpty() -> selectAccountSelectorNormalInputView.setError(getString(R.string.please_select_account_error))
            bankNormalInputView.visibility == View.VISIBLE && bankNormalInputView.selectedValue.isEmpty() -> bankNormalInputView.setError(getString(R.string.fixed_deposit_please_choose_a_bank))
            branchNormalInputView.visibility == View.VISIBLE && branchNormalInputView.selectedValue.isEmpty() -> branchNormalInputView.setError(getString(R.string.fixed_deposit_please_choose_a_branch))
            accountTypeNormalInputView.visibility == View.VISIBLE && accountTypeNormalInputView.selectedValue.isEmpty() -> accountTypeNormalInputView.setError(getString(R.string.fixed_deposit_please_choose_an_account_type))
            accountNumberNormalInputView.visibility == View.VISIBLE && accountNumberNormalInputView.selectedValue.isEmpty() -> accountNumberNormalInputView.setError(getString(R.string.fixed_deposit_please_enter_an_account_number))
            paymentReferenceNormalInputView.selectedValue.isEmpty() -> paymentReferenceNormalInputView.setError(getString(R.string.fixed_deposit_please_enter_a_payment_reference))
            !declarationCheckBoxView.isChecked -> declarationCheckBoxView.setErrorMessage(getString(R.string.fixed_deposit_please_agree_to_these_terms))
            !personalClientAgreementCheckBoxView.isChecked -> personalClientAgreementCheckBoxView.setErrorMessage(getString(R.string.fixed_deposit_please_agree_to_these_terms))
            else -> {
                return true
            }
        }
        return false
    }

    private fun toggleVisibilityOfBankInputFields(viewState: Int) {
        branchNormalInputView.visibility = viewState
        bankNormalInputView.visibility = viewState
        accountTypeNormalInputView.visibility = viewState
        accountNumberNormalInputView.visibility = viewState
        if (viewState == View.VISIBLE) {
            selectAccountSelectorNormalInputView.visibility = View.GONE
        } else {
            selectAccountSelectorNormalInputView.visibility = View.VISIBLE
        }
    }

    private fun setUpAccountTypeList() {
        val accountList = SelectorList<StringItem>()
        accountTypes = viewModel.createAccountConfirmResponse.value?.accountTypes

        if (accountTypes != null) {
            for (account in accountTypes!!) {
                if (account.description != null) {
                    accountList.add(StringItem(account.description))
                }
            }
        } else {
            accountTypes = listOf()
        }

        accountTypeNormalInputView.setList(accountList, getString(R.string.select_account_type))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_BANK_NAME) {
                if (bankNormalInputView.selectedValue != data.getStringExtra(BANK_NAME)) {
                    branchNormalInputView.selectedValue = ""
                }

                bankNormalInputView.clearError()
                bankNormalInputView.selectedValue = data.getStringExtra(BANK_NAME) ?: ""
                viewModel.fixedDepositData.bankName = bankNormalInputView.selectedValue
                showProgressDialog()
                viewModel.fetchBranchList(viewModel.fixedDepositData.bankName)

                branchNormalInputView.setOnClickListener {
                    val chooseBranchIntent = Intent(BMBApplication.getInstance().topMostActivity, ChooseBranchListActivity::class.java)
                    chooseBranchIntent.putExtra(BMBConstants.RESULT, viewModel.bankBranches.value!!)
                    startActivityForResult(chooseBranchIntent, REQUEST_CODE_FOR_BRANCH_NAME)
                }
            } else if (requestCode == REQUEST_CODE_FOR_BRANCH_NAME) {
                branchNormalInputView.clearError()
                branchNormalInputView.selectedValue = data.getStringExtra(BRANCH_CODE) ?: ""
                viewModel.fixedDepositData.branchCode = branchNormalInputView.selectedValue
            }
        }
    }

    private fun setUpInterestSelectAccountList() {
        val absaAccountType: SelectorList<StringItem> = SelectorList()
        absaAccountType.add(StringItem(getString(R.string.choose_one_of_my_accounts)))
        absaAccountType.add(StringItem(getString(R.string.another_absa_account)))
        absaAccountToPayFromRadioButtonView.setDataSource(absaAccountType)
        absaAccountToPayFromRadioButtonView.selectedIndex = viewModel.fixedDepositData.selectedAbsaAccountType

        val selectAccountList = SelectorList<StringItem>()
        selectAccountList.add(StringItem(getString(R.string.fixed_deposit_absa_account)))
        selectAccountList.add(StringItem(getString(R.string.fixed_deposit_account_another_bank)))
        payInterestIntoNormalInputView.setList(selectAccountList, getString(R.string.select_account_toolbar_title))
    }

    private fun initialiseViewModel() {
        viewModel = baseActivity.viewModel()
        sharedViewModel = baseActivity.viewModel()
    }

    private fun setUpPaymentFrequencyList() {
        paymentFrequencyList = SelectorList()

        capFrequencyList.add("01")
        capFrequencyList.add("03")
        capFrequencyList.add("06")
        capFrequencyList.add("12")
        capFrequencyList.add("00")

        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_monthly), viewModel.currentRateTable.categoryRateMonthly + "%"))
        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_quarterly), viewModel.currentRateTable.categoryRateQuaterly + "%"))
        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_half_yearly), viewModel.currentRateTable.categoryRateHalfYearly + "%"))
        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_yearly), viewModel.currentRateTable.categoryRateAnnually + "%"))
        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_maturity), viewModel.currentRateTable.categoryRateMaturity + "%"))

        val selectedIndex = interestPaymentFrequencyNormalInputView.selectedIndex
        interestPaymentFrequencyNormalInputView.setList(paymentFrequencyList, getString(R.string.fixed_deposit_select_payment_frequency))
        if (selectedIndex > -1) {
            interestPaymentFrequencyNormalInputView.setDescription(String.format("%s: %s", getString(R.string.fixed_deposit_interest_rate_per_annum), paymentFrequencyList[selectedIndex].item2))
            paymentFrequencyList[selectedIndex].item2?.let { viewModel.fixedDepositData.interestRate = it }
        }
    }

    private fun selectDebitDay() {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
        val view = layoutInflater.inflate(R.layout.funeral_cover_debit_day, null)

        val data = arrayOfNulls<String>(31)
        for (i in data.indices) {
            data[i] = "" + (i + 1)
        }
        val adapter = DebitDateAdapter(requireContext(), data)

        val debitDateGridView = view.findViewById<GridView>(R.id.gvDays)
        debitDateGridView.adapter = adapter

        debitDateGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDayOfDebit = (position + 1).toString()
            interestPaymentDayNormalInputView.selectedValue = selectedDayOfDebit
            setNextPaymentDate()
            dialog.dismiss()
        }
        builder.setView(view).setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        dialog = builder.create()
        dialog.show()
    }

    private fun setNextPaymentDate() {
        val paymentFrequencyIndex = interestPaymentFrequencyNormalInputView.selectedIndex
        val calendar = GregorianCalendar()

        val dayOfMonth = interestPaymentDayNormalInputView.selectedValue.toInt()
        if (calendar[Calendar.DAY_OF_MONTH] >= dayOfMonth && paymentFrequencyIndex == 0) {
            calendar.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 1))
        } else {
            when (paymentFrequencyIndex) {
                1 -> calendar.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 3))
                2 -> calendar.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 6))
                3 -> calendar.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 12))
            }
        }

        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        viewModel.fixedDepositData.interestPaymentDay = dayOfMonth.toString()
        viewModel.fixedDepositData.nextCapDate = DateUtils.format(calendar.time, DASHED_DATE_PATTERN)

        interestPaymentDayNormalInputView.setDescription(getString(R.string.fixed_deposit_next_payment_date, DateUtils.format(calendar.time, DATE_DISPLAY_PATTERN)))
    }

    private fun setCalendarMinDate() {
        calendarMin = GregorianCalendar()

        when (interestPaymentFrequencyNormalInputView.selectedIndex) {
            1 -> calendarMin.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 3))
            2 -> calendarMin.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 6))
            3 -> calendarMin.add(Calendar.DAY_OF_YEAR, getMonthToDays(viewModel.dayTableList, 12))
            else -> calendarMin.add(Calendar.DAY_OF_YEAR, 8)
        }
    }

    private fun calculateRateTable() {
        if (investmentTermNormalInputView.selectedValue.isEmpty()) {
            return
        }

        val currentAmount = viewModel.fixedDepositData.amount.amountDouble
        val selectedValue = investmentTermNormalInputView.selectedValue
        val days: Int

        val term = selectedValue.substring(0, selectedValue.indexOf(" ")).trim().toInt()
        days = if (selectedValue.contains(getString(R.string.fixed_deposit_month))) {
            getMonthToDays(viewModel.dayTableList, term)
        } else {
            term
        }

        if (viewModel.interestRateInfo.value!!.interestRateTable != null) {
            for (item: InterestRateTable? in viewModel.interestRateInfo.value!!.interestRateTable!!) {
                val (minAmountLower, maxAmountLower) = getMinMaxAmount(item?.termDepositAmountRangeMin!!)

                if (currentAmount in minAmountLower..maxAmountLower) {
                    if (item.termDepositAmountRangeMinInterestTable != null) {
                        setUpTable(item.termDepositAmountRangeMinInterestTable, days)
                    }
                } else {
                    val (minAmountUpper, maxAmountUpper) = getMinMaxAmount(item.termDepositAmountRangeMax!!)
                    if (currentAmount in minAmountUpper..maxAmountUpper) {
                        if (item.termDepositAmountRangeMaxInterestTable != null) {
                            setUpTable(item.termDepositAmountRangeMaxInterestTable, days)
                        }
                        break
                    }
                }
            }
        }
    }

    private fun setUpTable(dayTables: List<TermDepositInterestRateDayTable>, days: Int) {
        for (dayTable: TermDepositInterestRateDayTable? in dayTables) {
            val daysFrom = dayTable?.daysFrom?.toInt()
            val daysTo = dayTable?.daysTo?.toInt()
            if (days >= daysFrom!! && days <= daysTo!!) {
                viewModel.currentRateTable = dayTable
                setUpPaymentFrequencyList()
                break
            }
        }
    }

    private fun getMinMaxAmount(amountRange: String): Pair<Double, Double> {
        val amounts = amountRange.replace("R", "").split("-")

        return if (amounts.size > 1) {
            val minAmount = if (amounts[0].isNotEmpty()) amounts[0].toDouble() else 0.0
            val maxAmount = if (amounts[1].isNotEmpty()) amounts[1].toDouble() else 0.0
            Pair(minAmount, maxAmount)
        } else {
            Pair(0.0, 0.0)
        }
    }

    internal inner class DebitDateAdapter(context: Context, objects: Array<String?>) : ArrayAdapter<String>(context, 0, objects) {

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val textView = LayoutInflater.from(context).inflate(R.layout.funeral_cover_debit_order_item, parent, false) as TextView
            textView.text = (position + 1).toString()
            textView.setTextColor(Color.BLACK)
            return textView
        }
    }

    private val performCAClickOnClientAgreement = object : ClickableSpan() {
        override fun onClick(view: View) {
            startTermsAndConditionsActivity()
        }
    }

    private fun startTermsAndConditionsActivity() {
        val clientType = CustomerProfileObject.instance.clientTypeGroup

        if (NetworkUtils.isNetworkConnected()) {
            PdfUtil.showTermsAndConditionsClientAgreement(activity as BaseActivity, clientType)
        } else {
            Toast.makeText(context, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show()
        }
    }
}