/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.futurePlan

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.invest.getProductInterestRate.ProductProfileInterestRateViewModel
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.ProductInterestRateRequest
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN
import com.barclays.absa.banking.futurePlan.FuturePlanInvestmentTermFragment.Companion.MAX_MONTHS
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestFundYourAccountFragment
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.toLowerCase
import com.barclays.absa.utils.extensions.toRandAmount
import com.barclays.absa.utils.viewModel
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.MaximumAmountValidationRule
import styleguide.forms.validation.addValidationRule
import java.util.*
import java.util.concurrent.TimeUnit

class FuturePlanFundYourAccountFragment : SaveAndInvestFundYourAccountFragment() {

    companion object {
        private const val MINIMUM_RECURRING_AMOUNT = 100.00
        private const val MAXIMUM_NUMBER_OF_DEPOSITS = 61
    }

    private var selectedPeriod = TermType.DAYS
    private var maturityDateChanged = false
    private val todaysDate = GregorianCalendar()

    private lateinit var productProfileInterestRateViewModel: ProductProfileInterestRateViewModel
    private lateinit var maturityDatePickerDialog: DatePickerDialog

    private val minimumInvestmentTermInDays
        get() = if (saveAndInvestViewModel.minimumInvestmentTermInDays > 0) saveAndInvestViewModel.minimumInvestmentTermInDays else calculateDaysDifference(GregorianCalendar().apply { add(Calendar.MONTH, minimumMonthlyDeposits) })

    override var minimumRecurringAmount = MINIMUM_RECURRING_AMOUNT
    override var defaultAccountName = FUTURE_PLAN

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productProfileInterestRateViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAnalyticsAction("FundYourAccountScreen_ScreenDisplayed")
        setToolBar(R.string.depositor_plus_setup_your_account)

        saveAndInvestActivity.showProgressIndicatorAndToolbar()
        saveAndInvestActivity.setProgressStep(2)

        with(binding) {
            amountNormalInputView.setDescription(getString(R.string.future_plan_minimum_recurring_deposit, minimumRecurringAmount.toRandAmount()))
            investmentTermNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.fixed_deposit_select_investment_term))
            numberOfPaymentsNormalInputView.addValidationRule(MaximumAmountValidationRule(MAXIMUM_NUMBER_OF_DEPOSITS.toDouble(), false, errorMessage = getString(R.string.depositor_plus_number_of_payments_maximum, MAXIMUM_NUMBER_OF_DEPOSITS)))
            recurringDepositCheckBoxView.visibility = View.GONE
            monthlyContributionDetailsTextView.visibility = View.VISIBLE
            recurringPaymentConstraintGroup.visibility = View.VISIBLE
            investmentTermNormalInputView.visibility = View.VISIBLE
            maturityDateNormalInputView.visibility = View.VISIBLE
        }

        setupMaturityDatePicker()
        setupViewListeners()
        setupObservers()

        saveAndInvestViewModel.minimumInvestmentTermInDays = minimumInvestmentTermInDays
    }

    override fun calculateInterestRate(initialDeposit: String): String {
        saveAndInvestViewModel.interestRate = initialDeposit.toDouble().let {
            interestRates.find { interestRateDetails -> it >= interestRateDetails.fromBalance && it <= interestRateDetails.toBalance }?.interestRate ?: 0.00
        }.toString()
        return "${saveAndInvestViewModel.interestRate} %"
    }

    override fun navigateOnTransferLimitSuccess() {
        navigate(FuturePlanFundYourAccountFragmentDirections.actionFuturePlanFundYourAccountFragmentToFuturePlanMarketingConsentFragment())
    }

    override fun fetchInterestRates() {
        with(productProfileInterestRateViewModel) {
            fetchProductProfileInterestRates(ProductInterestRateRequest().apply {
                productCode = saveAndInvestActivity.productType.productCode
                creditRatePlan = saveAndInvestActivity.productType.creditRatePlanCode
            })
            productProfileInterestRatesLiveData.observe(viewLifecycleOwner, {
                interestRates = it
                setAmountInputViewsMaximumValidation()
                dismissProgressDialog()
                productProfileInterestRatesLiveData.removeObservers(viewLifecycleOwner)
            })
        }
    }

    override fun setupRecurringDatePickers() {
        startDatePickerDialog = binding.recurringStartDateNormalInputView.setupDatePickerDialog().apply {
            with(GregorianCalendar()) {
                datePicker.minDate = apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                datePicker.maxDate = apply { add(Calendar.YEAR, 1) }.timeInMillis
            }
        }

        endDatePickerDialog = binding.recurringEndDateNormalInputView.setupDatePickerDialog().apply {
            val minDate = with(startDatePickerDialog.datePicker) {
                GregorianCalendar(year, month, dayOfMonth).apply { add(Calendar.MONTH, minimumMonthlyDeposits) }
            }
            datePicker.minDate = minDate.timeInMillis
            datePicker.maxDate = GregorianCalendar().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                add(Calendar.MONTH, MAX_MONTHS)
            }.timeInMillis
        }
    }

    private fun setupMaturityDatePicker() {
        val selectedCalendar = Calendar.getInstance()
        maturityDatePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            selectedCalendar.set(year, month, day)
            maturityDateChanged = true
            binding.maturityDateNormalInputView.selectedValue = DateUtils.format(selectedCalendar.time, DateUtils.DATE_DISPLAY_PATTERN)

            if (selectedCalendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || selectedCalendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                binding.maturityDateNormalInputView.setError(R.string.fixed_deposit_date_weekend_error)
            } else {
                updateInvestmentTerm(selectedCalendar)
                binding.maturityDateNormalInputView.clearError()
            }
        }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH))

        maturityDatePickerDialog.apply {
            datePicker.minDate = GregorianCalendar().apply { add(Calendar.MONTH, minimumMonthlyDeposits) }.timeInMillis
            datePicker.maxDate = GregorianCalendar().apply { add(Calendar.MONTH, MAX_MONTHS) }.timeInMillis
        }
    }

    private fun updateInvestmentTerm(selectedCalendar: Calendar) {
        val daysDifference = calculateDaysDifference(selectedCalendar)
        val monthsDifference = calculateTermInMonths(selectedCalendar)
        saveAndInvestViewModel.investmentTerm.value = if (selectedPeriod == TermType.MONTHS && monthsDifference != 0) {
            resources.getQuantityString(R.plurals.numberOfMonths, monthsDifference, monthsDifference).toLowerCase()
        } else {
            resources.getQuantityString(R.plurals.numberOfDays, daysDifference, daysDifference).toLowerCase()
        }
    }

    private fun setupViewListeners() {
        binding.investmentTermNormalInputView.setOnClickListener {
            maturityDateChanged = false
            navigate(FuturePlanFundYourAccountFragmentDirections.actionFuturePlanFundYourAccountFragmentToFuturePlanInvestmentTermFragment())
        }

        binding.maturityDateNormalInputView.setOnClickListener {
            binding.maturityDateNormalInputView.showDescription(true)
            maturityDatePickerDialog.show()
        }
    }

    private fun setupObservers() {
        with(saveAndInvestViewModel) {
            investmentTerm.observe(viewLifecycleOwner, {
                binding.investmentTermNormalInputView.selectedValue = it
                binding.investmentTermNormalInputView.clearError()
                val futureDatedCalendar = GregorianCalendar()
                val term = it.substringBefore(' ').toInt()
                selectedPeriod = if (it.contains(getString(R.string.fixed_deposit_month), true)) {
                    futureDatedCalendar.add(Calendar.MONTH, term)
                    TermType.MONTHS
                } else {
                    futureDatedCalendar.add(Calendar.DAY_OF_YEAR, term - 1)
                    TermType.DAYS
                }

                updateMaturityDate(futureDatedCalendar, term)
                investmentTermInDays = calculateDaysDifference(futureDatedCalendar) + 1
                investmentTermInMonths = calculateTermInMonths(futureDatedCalendar)
                maturityDate = DateUtils.format(futureDatedCalendar.time, DateUtils.DATE_DISPLAY_PATTERN)
                startDatePickerDialog.datePicker.maxDate = if (investmentTermInMonths < 12) futureDatedCalendar.timeInMillis else GregorianCalendar().apply { add(Calendar.YEAR, 1) }.timeInMillis
            })
        }
    }

    private fun updateMaturityDate(newTermCalendar: Calendar, term: Int) {
        if (!maturityDateChanged) {
            val termCalendar = if (term == minimumInvestmentTermInDays) GregorianCalendar().apply { add(Calendar.MONTH, minimumMonthlyDeposits) } else newTermCalendar
            binding.maturityDateNormalInputView.selectedValue = DateUtils.format(termCalendar.time, DateUtils.DATE_DISPLAY_PATTERN)
            maturityDatePickerDialog.updateDate(termCalendar.get(Calendar.YEAR), termCalendar.get(Calendar.MONTH), termCalendar.get(Calendar.DAY_OF_MONTH))

            if (newTermCalendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || newTermCalendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                binding.maturityDateNormalInputView.setError(R.string.fixed_deposit_date_weekend_error)
            }
        }
    }

    private fun calculateTermInMonths(calendar: Calendar): Int {
        val yearDifference = calendar.get(Calendar.YEAR) - GregorianCalendar().get(Calendar.YEAR)
        return yearDifference * 12 + (calendar.get(Calendar.MONTH) - GregorianCalendar().get(Calendar.MONTH))
    }

    private fun calculateDaysDifference(calendar: Calendar): Int = TimeUnit.MILLISECONDS.toDays(calendar.timeInMillis - todaysDate.timeInMillis).toInt() + 1

    private enum class TermType {
        MONTHS,
        DAYS
    }
}