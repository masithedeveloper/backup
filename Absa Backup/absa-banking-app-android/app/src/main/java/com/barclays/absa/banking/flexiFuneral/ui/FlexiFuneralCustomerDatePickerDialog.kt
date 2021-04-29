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

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import com.barclays.absa.banking.R
import java.util.*

class FlexiFuneralCustomerDatePickerDialog internal constructor(context: Context, listener: OnDateSetListener?, year: Int, month: Int, dayOfMonth: Int) : DatePickerDialog(context, R.style.DatePickerDialogTheme, listener, year, month, dayOfMonth) {
    private var selectedYear = 0
    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        var mon = month
        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.timeInMillis = view.minDate
        if (selectedYear != year) {
            if (year == calendarMinDate.get(Calendar.YEAR)) {
                mon = calendarMinDate.get(Calendar.MONTH)
            }
            selectedYear = year
            val day = calendarMinDate.get(Calendar.DAY_OF_MONTH)
            super.onDateChanged(view, year, mon, day)
        } else {
            super.onDateChanged(view, year, mon, dayOfMonth)
        }
    }
}