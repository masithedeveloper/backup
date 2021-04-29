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
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthFromDays
import com.barclays.absa.banking.fixedDeposit.services.dto.InterestRateTable
import com.barclays.absa.banking.fixedDeposit.services.dto.TermDepositInterestRateDayTable
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.fixed_deposit_reinvestment_payment_frequency_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import java.util.*

class FixedDepositReinvestmentPaymentFrequencyFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_reinvestment_payment_frequency_fragment) {

    private var interestRate = ""
    private var capFrequency = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixedDepositMaintenanceActivity.setProgressStep(3)

        interestPaymentDayNormalInputView.setOnClickListener { showDayPicker() }
        nextPaymentDayLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(fixedDepositViewModel.renewalInstructionData.endDate))
        setupFrequencySelector()
        setupObservers()
        nextButton.setOnClickListener {
            if (hasValidFields()) {
                fixedDepositViewModel.renewalInstructionData.capDay = interestPaymentDayNormalInputView.selectedValue
                fixedDepositViewModel.confirmRenewalInstruction()
            }
        }
    }

    private fun setupObservers() {
        fixedDepositViewModel.confirmRenewalInstructionResponse = MutableLiveData()
        fixedDepositViewModel.confirmRenewalInstructionResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            if (it.transactionMessage.equals(BMBConstants.SUCCESS, true)) {
                navigate(FixedDepositReinvestmentPaymentFrequencyFragmentDirections.actionFixedDepositReinvestmentPaymentFrequencyFragmentToFixedDepositReinvestConfirmationFragment(interestRate, capFrequency))
            } else {
                fixedDepositViewModel.notifyFailure(it)
            }
        })

        fixedDepositViewModel.failureResponse = MutableLiveData()
        fixedDepositViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            fixedDepositMaintenanceActivity.hideProgressIndicatorAndToolbar()
            navigate(FixedDepositReinvestmentPaymentFrequencyFragmentDirections.actionFixedDepositReinvestmentPaymentFrequencyFragmentToGenericResultScreenFragment(buildFailureResultScreenProperties()))
        })
    }

    private fun showDayPicker() {
        val dayPickerDialogFragment = DayPickerDialogFragment.newInstance(emptyArray())
        dayPickerDialogFragment.onDateItemSelectionListener = object : DayPickerDialogFragment.OnDateItemSelectionListener {
            override fun onDateItemSelected(day: String) {
                interestPaymentDayNormalInputView.clearError()
                interestPaymentDayNormalInputView.selectedValue = day
                fixedDepositViewModel.renewalInstructionData.capDay = day
                updatePayoutDate()
            }
        }
        dayPickerDialogFragment.show(childFragmentManager, "dialog")
    }

    private fun updatePayoutDate() {
        val calendar = DateUtils.getCalendarObj(DateUtils.getTodaysDate())
        val capitalisationDate: String = when (val capitalisationFrequency = fixedDepositViewModel.renewalInstructionData.interestCapFreq.toInt()) {
            0 -> fixedDepositViewModel.renewalInstructionData.endDate
            1 -> {
                if (interestPaymentDayNormalInputView.selectedValue.toInt() <= DateUtils.getTodaysDate("dd").toInt()) {
                    calendar.add(Calendar.MONTH, capitalisationFrequency)
                }
                calendar.set(Calendar.DAY_OF_MONTH, interestPaymentDayNormalInputView.selectedValue.toInt())
                DateUtils.format(calendar, DateUtils.DASHED_DATE_PATTERN)
            }
            else -> {
                calendar.add(Calendar.MONTH, capitalisationFrequency)
                calendar.set(Calendar.DAY_OF_MONTH, interestPaymentDayNormalInputView.selectedValue.toInt())
                DateUtils.format(calendar, DateUtils.DASHED_DATE_PATTERN)
            }
        }
        nextPaymentDayLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(capitalisationDate))
        fixedDepositViewModel.renewalInstructionData.nextCapDate = capitalisationDate
    }

    private fun setupFrequencySelector() {
        val months = getMonthFromDays(fixedDepositViewModel.interestRateInfo.value?.interestRateTable, fixedDepositViewModel.renewalInstructionData.term.toInt())
        val capFrequencyList = listOf("12", "6", "3", "1")
        val frequencyCodeList = mutableListOf<String>()
        setupTable()

        val paymentFrequencyList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.fixed_deposit_monthly), fixedDepositViewModel.currentRateTable.categoryRateMonthly + "%"))
            add(StringItem(getString(R.string.fixed_deposit_quarterly), fixedDepositViewModel.currentRateTable.categoryRateQuaterly + "%"))
            add(StringItem(getString(R.string.fixed_deposit_half_yearly), fixedDepositViewModel.currentRateTable.categoryRateHalfYearly + "%"))
            add(StringItem(getString(R.string.fixed_deposit_yearly), fixedDepositViewModel.currentRateTable.categoryRateAnnually + "%"))
        }

        capFrequencyList.forEach { frequency ->
            if (months < frequency.toInt()) {
                paymentFrequencyList.removeAt(paymentFrequencyList.lastIndex)
            } else {
                frequencyCodeList.add(0, frequency)
            }
        }
        paymentFrequencyList.add(StringItem(getString(R.string.fixed_deposit_maturity), fixedDepositViewModel.currentRateTable.categoryRateMaturity + "%"))
        frequencyCodeList.add("0")
        interestPaymentFrequencyNormalInputView.setList(paymentFrequencyList, getString(R.string.fixed_deposit_select_payment_frequency))
        interestPaymentFrequencyNormalInputView.setItemSelectionInterface {
            interestPaymentFrequencyNormalInputView.clearError()
            val selectedFrequency = paymentFrequencyList[it]
            capFrequency = selectedFrequency.displayValue.toString()
            fixedDepositViewModel.renewalInstructionData.interestCapFreq = frequencyCodeList[it]
            interestRate = selectedFrequency.displayValueLine2?.removeSuffix("%") ?: ""
            interestPaymentFrequencyNormalInputView.setDescription(getString(R.string.fixed_deposit_interest_earned, selectedFrequency.displayValueLine2.toString(), capFrequency))
            updatePaymentDay()
        }
    }

    private fun updatePaymentDay() {
        val date = fixedDepositViewModel.renewalInstructionData.endDate
        if (fixedDepositViewModel.renewalInstructionData.interestCapFreq == "0") {
            interestPaymentDayNormalInputView.selectedValue = date.takeLast(2)
            interestPaymentDayNormalInputView.visibility = View.GONE
            nextPaymentDayLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(date))
            fixedDepositViewModel.renewalInstructionData.nextCapDate = date
        } else if (interestPaymentDayNormalInputView.selectedValue.isNotEmpty()) {
            interestPaymentDayNormalInputView.visibility = View.VISIBLE
            updatePayoutDate()
        }
    }

    private fun String.isInRange(): Boolean {
        val amounts = this.replace("R", "").split("-")

        return if (amounts.size > 1) {
            val minAmount = if (amounts[0].isNotEmpty()) amounts[0].toDouble() else 0.0
            val maxAmount = if (amounts[1].isNotEmpty()) amounts[1].toDouble() else 0.0

            val currentAmount = fixedDepositViewModel.renewalInstructionData.amount.toDouble()
            currentAmount in minAmount..maxAmount
        } else {
            false
        }
    }

    private fun setupTable() {
        fixedDepositViewModel.interestRateInfo.value?.interestRateTable?.let { table ->
            table.forEach { item: InterestRateTable ->
                if (item.termDepositAmountRangeMin?.isInRange() == true) {
                    item.termDepositAmountRangeMinInterestTable?.let {
                        populateTable(it)
                    }
                } else if (item.termDepositAmountRangeMax?.isInRange() == true) {
                    item.termDepositAmountRangeMaxInterestTable?.let {
                        populateTable(it)
                    }
                    return@forEach
                }
            }
        }
    }

    private fun populateTable(dayTables: List<TermDepositInterestRateDayTable>) {
        dayTables.forEach { dayTable: TermDepositInterestRateDayTable ->
            val daysFrom = dayTable.daysFrom?.toInt() ?: 0
            val daysTo = dayTable.daysTo?.toInt() ?: 0
            val days = fixedDepositViewModel.renewalInstructionData.term.toInt()
            if (days in daysFrom..daysTo) {
                fixedDepositViewModel.currentRateTable = dayTable
                return
            }
        }
    }

    private fun hasValidFields(): Boolean {
        when {
            interestPaymentFrequencyNormalInputView.selectedValue.isEmpty() -> interestPaymentFrequencyNormalInputView.setError(R.string.fixed_deposit_please_choose_payment_frequency)
            interestPaymentDayNormalInputView.selectedValue.isEmpty() -> interestPaymentDayNormalInputView.setError(R.string.fixed_deposit_payment_day_error)
            else -> return true
        }
        return false
    }

    private fun buildFailureResultScreenProperties(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            fixedDepositMaintenanceActivity.onBackPressed()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.error))
                .setDescription(getString(R.string.fixed_deposit_reinvest_error_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    override fun onDestroyView() {
        fixedDepositViewModel.failureResponse.removeObservers(viewLifecycleOwner)
        fixedDepositViewModel.confirmRenewalInstructionResponse.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}