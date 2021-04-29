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
package com.barclays.absa.banking.card.ui.creditCard.hub

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.transactions.DatePickerDialogFragment
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DASHED_DATE_PATTERN
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.transaction_history_filter_fragment.*
import java.util.*

class TransactionHistoryFilterFragment : BottomSheetDialogFragment() {
    private lateinit var filteringOptions: FilteringOptions
    private lateinit var hubName: String
    private lateinit var transactionHistoryFilterFragmentView: UpdateFilteringOptions
    private lateinit var baseActivity: BaseActivity
    private lateinit var fromDatePickerDialogFragment: DatePickerDialogFragment
    private lateinit var toDatePickerDialogFragment: DatePickerDialogFragment
    private lateinit var toDate: String
    private lateinit var fromDate: String

    companion object {
        private const val FILTER_OPTIONS = "filterOptions"
        private const val HUB_NAME = "hubName"

        @JvmStatic
        fun newInstance(transactionHistoryFilterFragmentView: UpdateFilteringOptions, filteringOptions: FilteringOptions?, hubName: String?): TransactionHistoryFilterFragment {
            return TransactionHistoryFilterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(FILTER_OPTIONS, filteringOptions)
                    putString(HUB_NAME, hubName)
                }
                this.transactionHistoryFilterFragmentView = transactionHistoryFilterFragmentView
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.transaction_history_filter_fragment, container).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            filteringOptions = it.getParcelable(FILTER_OPTIONS) ?: FilteringOptions()
            hubName = it.getString(HUB_NAME) ?: ""
            initViews()
            setUpComponentListeners()
        }
    }

    private fun setUpComponentListeners() {
        doneButton.setOnClickListener { applyFilter() }
        closeDialogImageView.setOnClickListener { dismiss() }
        fromInputView.setOnClickListener { selectFromDate() }
        toInputView.setOnClickListener { selectToDate() }
        customDateRadioButton.setOnCheckedChangeListener { _, isChecked -> customDateLinearLayout.visibility = if (isChecked) View.VISIBLE else View.GONE }
        if (!customDateRadioButton.isChecked) {
            customDateLinearLayout.visibility = View.GONE
        }

        fromDatePickerDialogFragment = DatePickerDialogFragment.newInstance(DatePickerDialogFragment.DateType.FROM_DATE)
        fromDatePickerDialogFragment.setOnDateSetListener(fromDatePickerListener)
        val minCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -179) }
        fromDatePickerDialogFragment.setMinimumDate(minCalendar)

        toDatePickerDialogFragment = DatePickerDialogFragment.newInstance(DatePickerDialogFragment.DateType.TO_DATE)
        toDatePickerDialogFragment.setOnDateSetListener(toDatePickerListener)
    }

    private fun updateSelectedRange() {
        customDateRadioButton.isChecked = true
        if (toInputView.selectedValue.isNotEmpty() && fromInputView.selectedValue.isNotEmpty()) {
            val selectedFromDate = DateUtils.getDate(fromInputView.selectedValue, DATE_DISPLAY_PATTERN)
            val selectedToDate = DateUtils.getDate(toInputView.selectedValue, DATE_DISPLAY_PATTERN)

            when {
                validateSelectedDates(selectedFromDate, selectedToDate, Calendar.DAY_OF_MONTH, -7) -> lastSevenDaysRadioButton.isChecked = true
                isMonthToDate(selectedFromDate, selectedToDate) -> monthToDateRadioButton.isChecked = true
                validateSelectedDates(selectedFromDate, selectedToDate, Calendar.DAY_OF_MONTH, -30) -> lastMonthRadioButton.isChecked = true
                validateSelectedDates(selectedFromDate, selectedToDate, Calendar.MONTH, -3) -> lastThreeMonthsRadioButton.isChecked = true
                else -> customDateRadioButton.isChecked = true
            }
        }
    }

    private fun initViews() {
        fromInputView.setImageViewVisibility(View.VISIBLE)
        toInputView.setImageViewVisibility(View.VISIBLE)
        when (filteringOptions.filterType) {
            CreditCardHubActivity.ALL_TRANSACTIONS -> allTransactionsRadioButton.isChecked = true
            CreditCardHubActivity.MONEY_IN -> moneyInRadioButton.isChecked = true
            CreditCardHubActivity.MONEY_OUT -> moneyOutRadioButton.isChecked = true
            CreditCardHubActivity.UNCLEARED -> unclearedRadioButton.isChecked = true
            else -> allTransactionsRadioButton.isChecked = true
        }
        setFromDate(filteringOptions.fromDate)
        setToDate(filteringOptions.toDate)
        fromInputView.setIconViewImage(R.drawable.ic_calendar_dark_new)
        toInputView.setIconViewImage(R.drawable.ic_calendar_dark_new)

        if (!filteringOptions.shouldShowTransactionFilter) {
            filterTypeRadioGroup.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        view?.post {
            val parent = view?.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as? BottomSheetBehavior<*>
            view?.let { bottomSheetBehavior?.peekHeight = it.measuredHeight }
        }
    }

    private fun applyFilter() {
        val filteringOptions = FilteringOptions()
        val calendar = GregorianCalendar()
        val toDate = calendar.time
        val fromDate: Date
        val fromDateFormatted: String?
        val toDateFormatted: String?
        when {
            lastSevenDaysRadioButton.isChecked -> calendar.add(Calendar.DAY_OF_MONTH, -7)
            monthToDateRadioButton.isChecked -> calendar[Calendar.DAY_OF_MONTH] = 1
            lastMonthRadioButton.isChecked -> calendar.add(Calendar.DAY_OF_MONTH, -30)
            lastThreeMonthsRadioButton.isChecked -> calendar.add(Calendar.MONTH, -3)
        }
        fromDate = calendar.time
        if (customDateRadioButton.isChecked) {
            toDateFormatted = DateUtils.formatDate(toInputView.selectedValue, DATE_DISPLAY_PATTERN, DASHED_DATE_PATTERN)
            fromDateFormatted = DateUtils.formatDate(fromInputView.selectedValue, DATE_DISPLAY_PATTERN, DASHED_DATE_PATTERN)
        } else {
            toDateFormatted = DateUtils.format(toDate, DASHED_DATE_PATTERN)
            fromDateFormatted = DateUtils.format(fromDate, DASHED_DATE_PATTERN)
        }
        filteringOptions.fromDate = fromDateFormatted.toString()
        filteringOptions.toDate = toDateFormatted.toString()
        when (filterTypeRadioGroup.checkedRadioButtonId) {
            R.id.allTransactionsRadioButton -> {
                AnalyticsUtil.trackAction(hubName, "$hubName - Filter - All transactions")
                filteringOptions.filterType = CreditCardHubActivity.ALL_TRANSACTIONS
            }
            R.id.moneyInRadioButton -> {
                AnalyticsUtil.trackAction(hubName, "$hubName - Filter - In Transactions")
                filteringOptions.filterType = CreditCardHubActivity.MONEY_IN
            }
            R.id.moneyOutRadioButton -> {
                AnalyticsUtil.trackAction(hubName, "$hubName - Filter - Out Transactions")
                filteringOptions.filterType = CreditCardHubActivity.MONEY_OUT
            }
            R.id.unclearedRadioButton -> {
                AnalyticsUtil.trackAction(hubName, "$hubName - Filter - Uncleared Transactions")
                filteringOptions.filterType = CreditCardHubActivity.UNCLEARED
            }
            else -> {
                AnalyticsUtil.trackAction(hubName, "$hubName -Filter - All transactions")
                filteringOptions.filterType = CreditCardHubActivity.ALL_TRANSACTIONS
            }
        }
        if (DateUtils.isFromDateMore(fromDateFormatted, toDateFormatted)) {
            fromInputView.setError(getString(R.string.fromDate_LesserThan_ToDate_error))
        } else {
            transactionHistoryFilterFragmentView.updateFilteringOptions(filteringOptions)
            dismiss()
        }
    }

    fun setFromDate(tempFromDateSelected: String?) {
        val fromDateFormatted = DateUtils.formatDateMonth(tempFromDateSelected)
        fromInputView.clearError()
        fromInputView.selectedValue = fromDateFormatted
        updateSelectedRange()
    }

    fun setToDate(tempToDateSelected: String?) {
        toInputView.selectedValue = DateUtils.formatDateMonth(tempToDateSelected)
        updateSelectedRange()
    }

    private fun equals(fromDate: GregorianCalendar, toDate: GregorianCalendar): Boolean = fromDate[Calendar.YEAR] == toDate[Calendar.YEAR] && fromDate[Calendar.MONTH] == toDate[Calendar.MONTH] && fromDate[Calendar.DAY_OF_MONTH] == toDate[Calendar.DAY_OF_MONTH]

    private fun equals(selectedFromDate: Date, selectedToDate: Date, fromCalendar: GregorianCalendar, toCalendar: GregorianCalendar): Boolean {
        val selectedFromCalendar = GregorianCalendar()
        selectedFromCalendar.time = selectedFromDate
        val selectedToCalendar = GregorianCalendar()
        selectedToCalendar.time = selectedToDate
        return equals(fromCalendar, selectedFromCalendar) && equals(toCalendar, selectedToCalendar)
    }

    private fun validateSelectedDates(selectedFromDate: Date, selectedToDate: Date, durationField: Int, durationValue: Int): Boolean {
        val fromDate = GregorianCalendar()
        fromDate.add(durationField, durationValue)
        val toDate = GregorianCalendar()
        return equals(selectedFromDate, selectedToDate, fromDate, toDate)
    }

    private fun isMonthToDate(selectedFromDate: Date, selectedToDate: Date): Boolean {
        val fromDate = GregorianCalendar()
        fromDate[Calendar.DAY_OF_MONTH] = 1
        val toDate = GregorianCalendar()
        return equals(selectedFromDate, selectedToDate, fromDate, toDate)
    }

    private fun setUpSelectedDates(selectedMonth: Int, selectedDay: Int, selectedYear: Int): String {
        val month = selectedMonth + 1
        val monthSelected = month.convertToFormattedString()
        val daySelected = selectedDay.convertToFormattedString()

        return "${selectedYear}-${monthSelected}-${daySelected}"
    }

    private fun Int.convertToFormattedString(): String {
        return if (this > 9) this.toString() else "0$this"
    }

    private var fromDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
        val tempSelectedDate = setUpSelectedDates(selectedMonth, selectedDay, selectedYear)
        if (::toDate.isInitialized && DateUtils.isFromDateMore(tempSelectedDate, toDate)) {
            baseActivity.toastLong(R.string.personal_loan_hub_fromDate_LesserThan_ToDate_error)
        } else {
            fromDate = tempSelectedDate
        }
        setFromDate(tempSelectedDate)
    }

    private var toDatePickerListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
        val tempSelectedDate = setUpSelectedDates(selectedMonth, selectedDay, selectedYear)
        if (::fromDate.isInitialized && DateUtils.isFromDateMore(fromDate, tempSelectedDate)) {
            Toast.makeText(context, getString(R.string.personal_loan_hub_toDate_LesserThan_FromDate_error), Toast.LENGTH_LONG).show()
        } else {
            toDate = tempSelectedDate
        }
        setToDate(tempSelectedDate)

    }

    private fun selectFromDate() {
        if (!fromDatePickerDialogFragment.isAdded) {
            baseActivity.supportFragmentManager.let { fromDatePickerDialogFragment.show(it, "fromDateDialog") }
        }
    }

    private fun selectToDate() {
        if (!toDatePickerDialogFragment.isAdded) {
            baseActivity.supportFragmentManager.let { toDatePickerDialogFragment.show(it, "toDateDialog") }
        }
    }

    interface UpdateFilteringOptions {
        fun updateFilteringOptions(filteringOptions: FilteringOptions)
    }
}