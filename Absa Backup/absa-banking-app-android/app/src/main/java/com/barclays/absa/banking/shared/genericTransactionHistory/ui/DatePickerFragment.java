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

package com.barclays.absa.banking.shared.genericTransactionHistory.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.DatePickerFragmentBinding;
import com.barclays.absa.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN;
import static com.barclays.absa.utils.DateUtils.parseDate;

public class DatePickerFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String START_DATE_KEY = "start_date_key";
    private static final String END_DATE_KEY = "end_date_key";
    private final int MONTH_TO_DATE = 0;
    private final int LAST_MONTH = 1;
    private final int PAST_THREE_MONTH = 2;

    private DatePickerFragmentBinding datePickerFragmentBinding;
    private Date startDate;
    private Date endDate;
    private String startDisplayDate;
    private String endDisplayDate;
    private OnDateRangeSelectionListener onDateRangeSelectionListener;

    public static DatePickerFragment newInstance(String startDate, String endDate) {
        Bundle arguments = new Bundle();
        arguments.putString(START_DATE_KEY, startDate);
        arguments.putString(END_DATE_KEY, endDate);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(arguments);
        return datePickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        datePickerFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.date_picker_fragment, container, false);
        datePickerFragmentBinding.dateRangeRadioButtonView.setDataSource(buildDateRanges());
        datePickerFragmentBinding.dateRangeRadioButtonView.setItemCheckedInterface((index) -> {
            startDate = getStartDate(index);
            endDate = getToday();
            datePickerFragmentBinding.fromDateNormalInputView.setSelectedValue(DateUtils.format(startDate, DATE_DISPLAY_PATTERN));
            datePickerFragmentBinding.toDateNormalInputView.setSelectedValue(DateUtils.format(endDate, DATE_DISPLAY_PATTERN));
        });
        datePickerFragmentBinding.closeButton.setOnClickListener(this);
        datePickerFragmentBinding.fromDateNormalInputView.setOnClickListener(this);
        datePickerFragmentBinding.toDateNormalInputView.setOnClickListener(this);
        datePickerFragmentBinding.doneButton.setOnClickListener(this);
        return datePickerFragmentBinding.getRoot();
    }

    private Date getStartDate(int selectedIndex) {
        GregorianCalendar calendar = new GregorianCalendar();
        switch (selectedIndex) {
            case MONTH_TO_DATE:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                break;
            case PAST_THREE_MONTH:
                calendar.add(Calendar.MONTH, -3);
                break;
        }
        return calendar.getTime();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            startDisplayDate = arguments.getString(START_DATE_KEY);
            startDate = parseDate(startDisplayDate);

            endDisplayDate = arguments.getString(END_DATE_KEY);
            endDate = parseDate(endDisplayDate);
        }
        datePickerFragmentBinding.fromDateNormalInputView.setSelectedValue(DateUtils.formatDateMonth(startDisplayDate) != null ? DateUtils.formatDateMonth(startDisplayDate) : "");
        datePickerFragmentBinding.toDateNormalInputView.setSelectedValue(DateUtils.formatDateMonth(endDisplayDate) != null ? DateUtils.formatDateMonth(endDisplayDate) : "");
    }

    private Date getToday() {
        return new Date();
    }

    protected boolean isMonthToDate(@NonNull Date selectedStartDate, @NonNull Date selectedEndDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return isDateEquals(startCalendar, selectedStartDate, selectedEndDate);
    }

    private boolean isLastMonth(@NonNull Date seletectStartDate, @NonNull Date selectedEndDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        return isDateEquals(startCalendar, seletectStartDate, selectedEndDate);
    }

    protected boolean isLastThreeMonth(@NonNull Date selectedStartDate, @NonNull Date selectedEndDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -3);
        return isDateEquals(startCalendar, selectedStartDate, selectedEndDate);
    }

    private boolean isDateEquals(@NonNull Calendar startCalendar, @NonNull Date selectedStartDate, @NonNull Date selectedEndDate) {
        Calendar endCalendar = Calendar.getInstance();

        Calendar selectedStartCalendar = Calendar.getInstance();
        selectedStartCalendar.setTime(selectedStartDate);
        Calendar selectedEndCalendar = Calendar.getInstance();
        selectedEndCalendar.setTime(selectedEndDate);

        return selectedStartCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR) &&
                selectedStartCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH) &&
                selectedStartCalendar.get(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.DAY_OF_MONTH) &&
                selectedEndCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
                selectedEndCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) &&
                selectedEndCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private SelectorList<StringItem> buildDateRanges() {
        SelectorList<StringItem> dateRanges = new SelectorList<>();
        dateRanges.add(new StringItem(getString(R.string.month_to_date)));
        dateRanges.add(new StringItem(getString(R.string.last_month)));
        dateRanges.add(new StringItem(getString(R.string.past_3_months)));
        return dateRanges;
    }

    public void invalidateDateRangeRadioButtonView() {
        if (startDate != null && endDate != null) {
            if (isMonthToDate(startDate, endDate)) {
                datePickerFragmentBinding.dateRangeRadioButtonView.setSelectedIndex(0);
            } else if (isLastMonth(startDate, endDate)) {
                datePickerFragmentBinding.dateRangeRadioButtonView.setSelectedIndex(1);
            } else if (isLastThreeMonth(startDate, endDate)) {
                datePickerFragmentBinding.dateRangeRadioButtonView.setSelectedIndex(2);
            }
        }
    }

    public void setOnDateRangeSelectionListener(OnDateRangeSelectionListener onDateRangeSelectionListener) {
        this.onDateRangeSelectionListener = onDateRangeSelectionListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeButton:
                dismiss();
                break;
            case R.id.fromDateNormalInputView:
                DateUtils.showDatePicker(getActivity(), date -> {
                    datePickerFragmentBinding.dateRangeRadioButtonView.clearSelection();
                    datePickerFragmentBinding.fromDateNormalInputView.setSelectedValue(DateUtils.format(date, DATE_DISPLAY_PATTERN));
                    startDate = date;
                    invalidateDateRangeRadioButtonView();
                }, startDate);
                break;
            case R.id.toDateNormalInputView:
                DateUtils.showDatePicker(getActivity(), date -> {
                    datePickerFragmentBinding.dateRangeRadioButtonView.clearSelection();
                    datePickerFragmentBinding.toDateNormalInputView.setSelectedValue(DateUtils.format(date, DATE_DISPLAY_PATTERN));
                    endDate = date;
                    invalidateDateRangeRadioButtonView();
                }, startDate != null ? startDate.getTime() : Calendar.getInstance().getTimeInMillis());
                break;
            case R.id.doneButton:
                if (startDate != null && endDate != null) {
                    if (onDateRangeSelectionListener != null) {
                        onDateRangeSelectionListener.onDateRangeSelected(startDate, endDate);
                    }
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public interface OnDateRangeSelectionListener {
        void onDateRangeSelected(Date startDate, Date endDate);
    }
}