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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.annotation.LayoutRes
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthFromDays
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestViewModel
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.extensions.toLowerCase
import kotlinx.android.synthetic.main.investment_term_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import java.util.*
import java.util.concurrent.TimeUnit

abstract class InvestmentTermBaseFragment<T : BaseViewModel>(@LayoutRes layoutId: Int) : BaseFragment(layoutId) {

    abstract var viewModel: T
    abstract var maxDays: Int
    abstract var minDays: Int
    abstract var maxMonths: Int
    abstract var minMonths: Int

    abstract fun setupViews()

    open var seekBarMinimum: Int = 0
    open var seekBarRestriction: Int = 0
    open var suffix: String = ""

    protected var isTrackChange = false
    protected var isSelfChange = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRadioView()
        setupViews()
        restoreInput()

        investmentTermNormalInputView.editText?.setOnFocusChangeListener { _, hasFocus ->
            val text = investmentTermNormalInputView.selectedValue

            if (hasFocus) {
                setInvestmentTermMaxLength(4)
            } else {
                setInvestmentTermMaxLength(10)
            }

            if (hasFocus && text.contains(" ")) {
                isSelfChange = true
                investmentTermNormalInputView.selectedValue = text.substring(0, text.indexOf(" "))
            } else {
                if (!investmentTermNormalInputView.hasError()) {
                    investmentTermNormalInputView.selectedValue += " $suffix"
                }
            }
        }
    }

    open fun setUpRadioView() {
        val monthsDaysSelectorList = SelectorList<StringItem>()
        monthsDaysSelectorList.add(StringItem(getString(R.string.fixed_deposit_months)))
        monthsDaysSelectorList.add(StringItem(getString(R.string.fixed_deposit_days)))
        monthDayRadioButtonView.setDataSource(monthsDaysSelectorList)
    }

    protected fun updateInvestmentView(position: Int) {
        suffix = getSuffix(position)
        var realPosition = position
        if (position == 0) {
            realPosition = position + seekBarRestriction
        }
        investmentTermNormalInputView.selectedValue = "$realPosition $suffix"
    }

    protected fun clearFocusFromInvestmentTermView() {
        KeyboardUtils.hideSoftKeyboard(investmentTermNormalInputView)
        investmentTermNormalInputView.editText?.clearFocus()
        focusHackLinearLayout.requestFocus()
    }

    protected fun convertTermType(termValue: Int) {
        val convertedTerm: Int
        val calendar = GregorianCalendar().apply {
            if (monthDayRadioButtonView.selectedIndex == 0) add(Calendar.DAY_OF_YEAR, termValue) else add(Calendar.MONTH, termValue)
        }

        convertedTerm = if (monthDayRadioButtonView.selectedIndex == 0 && termValue in minDays..maxDays) {
            //Months - Meaning we switched from days
            val yearDifference = calendar.get(Calendar.YEAR) - GregorianCalendar().get(Calendar.YEAR)
            yearDifference * 12 + (calendar.get(Calendar.MONTH) - GregorianCalendar().get(Calendar.MONTH))
        } else if (monthDayRadioButtonView.selectedIndex == 1 && termValue in minMonths..maxMonths) {
            //Days - Meaning we switched from Months
            when (termValue) {
                minMonths -> minDays
                maxMonths -> maxDays
                else -> TimeUnit.MILLISECONDS.toDays(calendar.timeInMillis - GregorianCalendar().timeInMillis).toInt()
            }
        } else {
            investmentTermSliderView.setProgress(0)
            investmentTermNormalInputView.clearError()
            updateInvestmentView(0)
            return
        }
        investmentTermSliderView.setProgress(convertedTerm - seekBarMinimum)
        updateInvestmentView(convertedTerm)
    }

    protected fun getSuffix(progress: Int): String {
        suffix = if (monthDayRadioButtonView.selectedIndex == 0) {
            if (progress == 0 && minMonths == 0) {
                getString(R.string.fixed_deposit_month).toLowerCase()
            } else {
                getString(R.string.fixed_deposit_months).toLowerCase()
            }
        } else {
            getString(R.string.fixed_deposit_days).toLowerCase()
        }
        return suffix
    }

    private fun restoreInput() {
        var investmentTerm = when (viewModel) {
            is FixedDepositViewModel -> (viewModel as FixedDepositViewModel).investmentTerm.value
            is SaveAndInvestViewModel -> (viewModel as SaveAndInvestViewModel).investmentTerm.value
            else -> ""
        }

        if (investmentTerm.isNullOrEmpty() && seekBarRestriction == 1) {
            investmentTermNormalInputView.selectedValue = "1 ${getString(R.string.fixed_deposit_month)}"
            return
        } else if (investmentTerm.isNullOrEmpty()) {
            investmentTermNormalInputView.selectedValue = "$seekBarRestriction ${getString(R.string.fixed_deposit_months).toLowerCase()}"
            return
        }

        var progressValue = investmentTerm.split(" ").first().toInt()
        if (progressValue < seekBarRestriction) {
            progressValue = seekBarRestriction

            investmentTerm = if (investmentTerm.contains(getString(R.string.fixed_deposit_days), true)) {
                "$progressValue ${getString(R.string.fixed_deposit_days).toLowerCase()}"
            } else {
                "$progressValue ${getString(R.string.fixed_deposit_months).toLowerCase()}"
            }
        }

        if (investmentTerm.contains(getString(R.string.fixed_deposit_days), true)) {
            if (viewModel is FixedDepositViewModel) {
                progressValue = getMonthFromDays((viewModel as FixedDepositViewModel).interestRateInfo.value?.interestRateTable, progressValue)
            }
            monthDayRadioButtonView.selectedIndex = 1
        }

        investmentTermNormalInputView.selectedValue = investmentTerm
        investmentTermSliderView.setProgress(progressValue - seekBarMinimum)
    }

    private fun setInvestmentTermMaxLength(length: Int) {
        val newFilters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
        investmentTermNormalInputView.editText?.filters = newFilters
    }
}