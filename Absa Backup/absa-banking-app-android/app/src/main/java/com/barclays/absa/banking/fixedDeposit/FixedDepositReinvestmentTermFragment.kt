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

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthFromDays
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthToDays
import com.barclays.absa.banking.fixedDeposit.services.dto.TermDepositInterestRateDayTable
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.fixed_deposit_reinvestment_term_fragment.*
import styleguide.utils.AnimationHelper
import java.util.*
import java.util.concurrent.TimeUnit

class FixedDepositReinvestmentTermFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_reinvestment_term_fragment) {

    private var selectedPeriod = TermType.DAYS
    private var shouldUpdateMaturityDate = false
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_custom_instruction)
        fixedDepositMaintenanceActivity.setProgressStep(2)
        fixedDepositViewModel.fetchInterestRateInfo()
        setupObservers()
        setupMaturityDatePicker()
        investmentStartDateLineItemView.setLineItemViewContent(DateUtils.getTodaysDate(DateUtils.DATE_DISPLAY_PATTERN))

        investmentTermNormalInputView.setOnClickListener {
            shouldUpdateMaturityDate = true
            navigate(FixedDepositReinvestmentTermFragmentDirections.actionFixedDepositReinvestmentTermFragmentToFixedDepositInvestmentTermFragment2())
        }

        nextButton.setOnClickListener {
            if (hasValidFields()) {
                fixedDepositViewModel.renewalInstructionData.endDate = DateUtils.formatDate(maturityDateNormalInputView.selectedValue, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.DASHED_DATE_PATTERN)
                navigate(FixedDepositReinvestmentTermFragmentDirections.actionFixedDepositReinvestmentTermFragmentToFixedDepositReinvestmentPaymentFrequencyFragment())
            }
        }
    }

    private fun setupObservers() {
        fixedDepositViewModel.interestRateInfo.observe(viewLifecycleOwner, {
            it?.interestRateTable?.first()?.termDepositAmountRangeMinInterestTable?.let { termDepositAmountRangeMinInterestTable ->
                setupDateTable(termDepositAmountRangeMinInterestTable)
            }
            dismissProgressDialog()
        })

        fixedDepositViewModel.investmentTerm.observe(viewLifecycleOwner, {
            investmentTermNormalInputView.selectedValue = it
            investmentTermNormalInputView.clearError()
            var days = it.substringBefore(' ').toInt()
            selectedPeriod = if (it.contains(getString(R.string.fixed_deposit_month), true)) {
                days = getMonthToDays(fixedDepositViewModel.dayTableList, days)
                TermType.MONTHS
            } else {
                TermType.DAYS
            }
            if (shouldUpdateMaturityDate) {
                val calendar = DateUtils.getCalendarObj(DateUtils.getTodaysDate()).apply { add(Calendar.DAY_OF_YEAR, days) }
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                maturityDateNormalInputView.selectedValue = DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN)
                fixedDepositViewModel.renewalInstructionData.term = days.toString()

                if (calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                    maturityDateNormalInputView.setError(R.string.fixed_deposit_date_weekend_error)
                }
            }
        })
    }

    private fun setupMaturityDatePicker() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(fixedDepositMaintenanceActivity, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            maturityDateNormalInputView.selectedValue = DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN)

            if (calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                maturityDateNormalInputView.setError(R.string.fixed_deposit_date_weekend_error)
            } else {
                updateInvestmentTerm(calendar)
                maturityDateNormalInputView.clearError()
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.apply {
            datePicker.minDate = GregorianCalendar().apply { add(Calendar.DAY_OF_YEAR, 8) }.timeInMillis
            datePicker.maxDate = GregorianCalendar().apply { add(Calendar.DAY_OF_YEAR, 1826) }.timeInMillis
        }
        maturityDateNormalInputView.setOnClickListener {
            maturityDateNormalInputView.showDescription(true)
            datePickerDialog.show()
        }
    }

    private fun setupDateTable(interestRateTable: List<TermDepositInterestRateDayTable>) {
        fixedDepositViewModel.dayTableList = ArrayList()
        interestRateTable.forEach {
            val daysFrom = it.daysFrom?.toInt() ?: 0
            val daysTo = it.daysTo?.toInt() ?: 0

            if (daysTo - daysFrom > 35) {
                for (day in daysFrom..daysTo - 28 step 30) {
                    fixedDepositViewModel.dayTableList.add(day)
                }
            } else {
                fixedDepositViewModel.dayTableList.add(daysFrom)
            }
        }
    }

    private fun updateInvestmentTerm(calendar: Calendar) {
        shouldUpdateMaturityDate = false
        val daysBetween = TimeUnit.MILLISECONDS.toDays(calendar.timeInMillis - GregorianCalendar().timeInMillis).toInt() + 1
        val months = getMonthFromDays(fixedDepositViewModel.interestRateInfo.value?.interestRateTable, daysBetween)
        fixedDepositViewModel.renewalInstructionData.term = daysBetween.toString()
        fixedDepositViewModel.investmentTerm.value = if (selectedPeriod == TermType.MONTHS && months != 0) {
            val suffix = if (months > 1) getString(R.string.fixed_deposit_months).toLowerCase(BMBApplication.getApplicationLocale()) else getString(R.string.fixed_deposit_month)
            "$months $suffix"
        } else {
            "$daysBetween ${getString(R.string.fixed_deposit_days)}"
        }
    }

    private fun hasValidFields(): Boolean {
        when {
            maturityDateNormalInputView.hasError() -> AnimationHelper.shakeShakeAnimate(maturityDateNormalInputView)
            investmentTermNormalInputView.selectedValue.isEmpty() -> investmentTermNormalInputView.setError(R.string.fixed_deposit_select_investment_term)
            maturityDateNormalInputView.selectedValue.isEmpty() -> maturityDateNormalInputView.setError(R.string.fixed_deposit_please_select_a_maturity_date)
            else -> return true
        }
        return false
    }

    override fun onDestroyView() {
        fixedDepositViewModel.interestRateInfo.removeObservers(viewLifecycleOwner)
        fixedDepositViewModel.investmentTerm.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private enum class TermType {
        MONTHS,
        DAYS
    }
}