/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.transactions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.utils.DateUtils;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {

    private static final String DATE_TYPE = "dateType";
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String fromDate;
    private String toDate;

    private Calendar minCalendar;
    private Calendar maxCalendar;

    public enum DateType {
        FROM_DATE,
        TO_DATE
    }

    public static DatePickerDialogFragment newInstance(DateType dateType) {
        Bundle args = new Bundle();
        args.putSerializable(DATE_TYPE, dateType);
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DateType datePickerDateType = (DateType) getArguments().get("dateType");
        Calendar calendar;
        if (datePickerDateType == DateType.FROM_DATE) {
            calendar = DateUtils.getCalendarObj(fromDate != null ? fromDate : DateUtils.getTodaysDate());
        } else {
            calendar = DateUtils.getCalendarObj(toDate != null ? toDate : DateUtils.getTodaysDate());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePickerDialogTheme, onDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (maxCalendar != null) {
            datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTime().getTime());
        } else {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        if (minCalendar != null) {
            datePickerDialog.getDatePicker().setMinDate(minCalendar.getTime().getTime());
        }

        return datePickerDialog;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setMinimumDate(Calendar calendar) {
        minCalendar = calendar;
    }

    public void setMaximumDate(Calendar calendar) {
        maxCalendar = calendar;
    }
}