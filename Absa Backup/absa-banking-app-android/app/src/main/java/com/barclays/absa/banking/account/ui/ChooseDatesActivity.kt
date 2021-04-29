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
package com.barclays.absa.banking.account.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import kotlinx.android.synthetic.main.choose_dates_activity.*
import styleguide.content.TertiaryContentAndLabelView
import java.util.*

open class ChooseDatesActivity : BaseActivity(R.layout.choose_dates_activity) {
    protected lateinit var statementDialogUtils: StatementDialogUtils
    protected lateinit var accountNumber: String
    protected lateinit var pdf: ByteArray

    private lateinit var adapter: DateSelectorAdapter
    protected var selected = 1
    private var featureTag = if (isBusinessAccount) "BB" else ""

    private enum class DateRange {
        MONTH_TO_DATE, LAST_MONTH, LAST_THREE_MONTHS, CUSTOM_DATE
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolBarBack(R.string.choose_dates)
        chooseDateRecyclerView.adapter = DateSelectorAdapter().also { adapter = it }

        intent.extras?.apply {
            accountNumber = getString(IntentFactory.ACCOUNT_NUMBER, "")
            featureTag = getString(IntentFactory.FEATURE_NAME, "")
        }

        statementDialogUtils = StatementDialogUtils.newInstance(this)
        setDefaultFromDate()

        fromDateNormalInputView.setCustomOnClickListener(View.OnClickListener {
            statementDialogUtils.setChoosingFromDate(true)
            showCustomDateSelector()
        })

        toDateNormalInputView.setCustomOnClickListener(View.OnClickListener {
            statementDialogUtils.setChoosingFromDate(false)
            showCustomDateSelector()
        })
    }

    fun tagStatementsEvent(actionTag: String) {
        trackAction("Statements", featureTag + actionTag)
    }

    protected fun updateSelectedRange() {
        val fromDate = statementDialogUtils.fromDate
        val toDate = statementDialogUtils.toDate
        selected = when {
            isMonthToDate(fromDate, toDate) -> 0
            isLastMonth(fromDate, toDate) -> 1
            isLastThreeMonth(fromDate, toDate) -> 2
            else -> 3
        }

        adapter.notifyDataSetChanged()
    }

    private fun setDefaultFromDate() {
        val calendar = GregorianCalendar()
        calendar.add(Calendar.MONTH, -1)
        val fromDate = calendar.time
        statementDialogUtils.fromDate = fromDate
        updateFromAndToDate()
    }

    protected fun updateFromAndToDate() {
        val fromDateString = statementDialogUtils.fromDateStringWithMonthInWord
        val toDateString = statementDialogUtils.toDateStringWithMonthInWord

        if (fromDateString != fromDateNormalInputView.text) {
            fromDateNormalInputView.text = statementDialogUtils.fromDateStringWithMonthInWord
        }

        if (toDateString != toDateNormalInputView.text) {
            toDateNormalInputView.text = statementDialogUtils.toDateStringWithMonthInWord
        }

        downloadButton.isEnabled = true
    }

    private fun isLastMonth(selectedFromDate: Date, selectedToDate: Date): Boolean {
        val fromCalendar = GregorianCalendar()
        fromCalendar.add(Calendar.MONTH, -1)
        val toCalendar = GregorianCalendar()
        return isSameDate(selectedFromDate, selectedToDate, fromCalendar, toCalendar)
    }

    private fun isLastThreeMonth(selectedFromDate: Date, selectedToDate: Date): Boolean {
        val fromCalendar = GregorianCalendar()
        fromCalendar.add(Calendar.MONTH, -3)
        val toCalendar = GregorianCalendar()
        return isSameDate(selectedFromDate, selectedToDate, fromCalendar, toCalendar)
    }

    private fun isMonthToDate(selectedFromDate: Date, selectedToDate: Date): Boolean {
        val fromCalendar = GregorianCalendar()
        fromCalendar[Calendar.DAY_OF_MONTH] = 1
        val toCalendar = GregorianCalendar()
        return isSameDate(selectedFromDate, selectedToDate, fromCalendar, toCalendar)
    }

    private fun isSameDate(selectedFromDate: Date, selectedToDate: Date, fromCalendar: GregorianCalendar, toCalendar: GregorianCalendar): Boolean {
        val selectedFromCalendar = GregorianCalendar()
        selectedFromCalendar.time = selectedFromDate
        val selectedToCalendar = GregorianCalendar()
        selectedToCalendar.time = selectedToDate
        return isSameDate(fromCalendar, selectedFromCalendar) && isSameDate(toCalendar, selectedToCalendar)
    }

    private fun isSameDate(fromDate: GregorianCalendar, toDate: GregorianCalendar): Boolean {
        return fromDate[Calendar.YEAR] == toDate[Calendar.YEAR] && fromDate[Calendar.MONTH] == toDate[Calendar.MONTH] && fromDate[Calendar.DAY_OF_MONTH] == toDate[Calendar.DAY_OF_MONTH]
    }

    private inner class DateSelectorAdapter : RecyclerView.Adapter<DateSelectorAdapter.ViewHolder>() {
        private val data: Array<String>?
        private var selectedItemPosition: Int = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tertiaryContentAndLabelView = TertiaryContentAndLabelView((parent.context))
            tertiaryContentAndLabelView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tertiaryContentAndLabelView.showCheckBox(true)
            return ViewHolder(tertiaryContentAndLabelView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (data != null) {
                with(holder) {
                    tertiaryContentAndLabelView.tertiaryCheckBox.isChecked = position == selectedItemPosition
                    tertiaryContentAndLabelView.setContentText(data[position])
                    tertiaryContentAndLabelView.setOnClickListener(this)
                    tertiaryContentAndLabelView.tertiaryCheckBox.setOnClickListener(holder)
                }
            }
        }

        override fun getItemCount(): Int = data?.size ?: 0

        inner class ViewHolder(view: TertiaryContentAndLabelView) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val tertiaryContentAndLabelView = view

            override fun onClick(view: View) {
                if (0 <= adapterPosition && adapterPosition < DateRange.values().size) {
                    animate(tertiaryContentAndLabelView, R.anim.contract_horizontal)
                    val calendar = GregorianCalendar()
                    val toDate = calendar.time
                    statementDialogUtils.toDate = toDate
                    var fromDate = Date()

                    when (DateRange.values()[adapterPosition]) {
                        DateRange.MONTH_TO_DATE -> {
                            calendar[Calendar.DAY_OF_MONTH] = 1
                            fromDate = calendar.time
                        }
                        DateRange.LAST_MONTH -> {
                            calendar.add(Calendar.MONTH, -1)
                            fromDate = calendar.time
                        }
                        DateRange.LAST_THREE_MONTHS -> {
                            calendar.add(Calendar.MONTH, -3)
                            fromDate = calendar.time
                        }
                        else -> {
                        }
                    }
                    statementDialogUtils.fromDate = fromDate
                    updateFromAndToDate()
                    selectedItemPosition = adapterPosition

                    if (adapterPosition != selectedItemPosition) {
                        tertiaryContentAndLabelView.tertiaryCheckBox.isChecked = false
                        selectedItemPosition = 1
                    } else {
                        tertiaryContentAndLabelView.tertiaryCheckBox.isChecked = true
                        selectedItemPosition = adapterPosition
                        notifyDataSetChanged()
                    }
                }
            }
        }

        init {
            data = resources.getStringArray(R.array.stamped_choose_date)
        }
    }

    private fun showCustomDateSelector() {
        statementDialogUtils.showDatePicker(
                object : StatementDialogUtils.CallBack {
                    override fun proceed() {
                        updateFromAndToDate()
                        updateSelectedRange()
                    }

                    override fun cancel() {
                    }
                })
    }

    override fun onDestroy() {
        statementDialogUtils.destroy()
        super.onDestroy()
    }
}