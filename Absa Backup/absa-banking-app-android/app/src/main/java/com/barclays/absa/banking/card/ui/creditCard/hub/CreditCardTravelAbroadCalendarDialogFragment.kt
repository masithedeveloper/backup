/*
 *
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.card.ui.creditCard.hub

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel
import com.barclays.absa.utils.DateUtils
import java.util.*

class CreditCardTravelAbroadCalendarDialogFragment : DialogFragment() {
    private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var travelUpdateModel: TravelUpdateModel? = null
    private var fromDate: String? = null
    private var toDate: String? = null

    enum class DateType {
        FROM_DATE,
        TO_DATE
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val datePickerDateType = arguments?.get(DATE_TYPE) as DateType
        val calendar: Calendar?
        calendar = if (datePickerDateType == DateType.FROM_DATE) {
            DateUtils.getCalendarObj(if (travelUpdateModel?.referralStartDate.isNullOrEmpty() || "0".equals(travelUpdateModel?.referralStartDate, true)) fromDate else travelUpdateModel?.referralStartDate)
        } else {
            DateUtils.getCalendarObj(if (travelUpdateModel?.referralEndDate.isNullOrEmpty() || "0".equals(travelUpdateModel?.referralEndDate, true)) toDate else travelUpdateModel?.referralEndDate)
        }

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme, onDateSetListener, calendar!!.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = DateUtils.getCalendarMinimumDate()

        if (datePickerDateType == DateType.TO_DATE) {
            val cal = DateUtils.getCalendarObj(fromDate)
            datePickerDialog.datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            cal.add(Calendar.DATE, 60)
            datePickerDialog.datePicker.maxDate = cal.timeInMillis
            datePickerDialog.datePicker.minDate = DateUtils.getCalendarObj(fromDate).timeInMillis
        }
        return datePickerDialog
    }

    fun setTravelUpdateModel(travelUpdateModel: TravelUpdateModel?) {
        this.travelUpdateModel = travelUpdateModel
    }

    fun setOnDateSetListener(onDateSetListener: DatePickerDialog.OnDateSetListener) {
        this.onDateSetListener = onDateSetListener
    }

    fun setFromDate(fromDate: String?) {
        this.fromDate = fromDate
    }

    fun getFromDate(): String? {
        return fromDate
    }

    fun setToDate(toDate: String?) {
        this.toDate = toDate
    }

    companion object {

        val DATE_TYPE = "dateType"

        fun newInstance(dateType: DateType): CreditCardTravelAbroadCalendarDialogFragment {
            val args = Bundle()
            args.putSerializable(DATE_TYPE, dateType)
            val fragment = CreditCardTravelAbroadCalendarDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}