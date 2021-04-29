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
package com.barclays.absa.banking.account.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ChooseDatesPickerBinding;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StatementDialogUtils {

    public static final String DAY_DATE_FORMAT = "EE dd MMM yyyy";
    private boolean isChoosingFromDate = true;
    private Dialog dialogDatePicker, dialogDisclaimer;
    private Date fromDate, toDate;

    private ChooseDatesPickerBinding binding;

    private static final SimpleDateFormat simpletDateCompactFormat = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
    private static final SimpleDateFormat simpletDateCompactFormatMonthInWord = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());

    private boolean isArchivedStatement;
    private Activity activity;

    private StatementDialogUtils(Activity activity, boolean isArchivedStatement) {
        this.activity = activity;
        this.isArchivedStatement = isArchivedStatement;
        Calendar calendar = getCalendar();
        fromDate = calendar.getTime();
        toDate = calendar.getTime();
    }

    public StatementDialogUtils(Activity activity) {
        this(activity, false);
    }

    private Calendar getCalendar() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    static StatementDialogUtils newInstanceArchived(Activity activity) {
        return new StatementDialogUtils(activity, true);
    }

    static StatementDialogUtils newInstance(Activity activity) {
        return new StatementDialogUtils(activity, false);
    }

    private long getMaxDate() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime().getTime();
    }

    private long getMinDateForStamped() {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, -3);
        return calendar.getTime().getTime();
    }

    private long getMinDateForArchived() {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, -18);
        return calendar.getTime().getTime();
    }

    private void setFromDateView() {
        String fromDateText = activity.getString(R.string.from_date) + "\n" + DateUtils.format(fromDate, DAY_DATE_FORMAT);
        binding.btnFrom.setText(fromDateText);
    }

    private void setCvDateView(long date) {
        if (binding != null) {
            try {
                binding.cvDate.setDate(date);
            } catch (java.lang.IllegalArgumentException e) {
                toastInvalidDateRange();
            }
        }
    }

    private void setToDateView() {
        if (toDate != null) {
            String toDateText = activity.getString(R.string.to_date) + "\n" + DateUtils.format(toDate, DAY_DATE_FORMAT);
            binding.btnTo.setText(toDateText);
        }
    }

    private void initCalendarDate() {
        binding.cvDate.setMaxDate(getMaxDate());
        if (isArchivedStatement) {
            binding.cvDate.setMinDate(getMinDateForArchived());
        } else {
            binding.cvDate.setMinDate(getMinDateForStamped());
        }

        adjustDate(fromDate);
        adjustDate(toDate);

        setCvDateView();
    }

    private void setCvDateView() {
        if (isChoosingFromDate) {
            setCvDateView(fromDate.getTime());
        } else {
            setCvDateView(toDate.getTime());
        }
    }

    private void adjustDate(Date date) {
        if (date.getTime() > binding.cvDate.getMaxDate())
            date.setTime(binding.cvDate.getMaxDate());
        else if (date.getTime() < binding.cvDate.getMinDate())
            date.setTime(binding.cvDate.getMinDate());
    }

    synchronized void showDatePicker(final CallBack callBack) {
        binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.choose_dates_picker, null, false);

        View view = binding.getRoot();

        initCalendarDate();

        setFromDateView();
        setToDateView();

        setCvDateView(isChoosingFromDate ? fromDate.getTime() : toDate.getTime());

        updateFromToColors();

        final Animation animationBounce = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        final Animation animationExpand = AnimationUtils.loadAnimation(activity, R.anim.expand_linear);

        binding.cvDate.setOnDateChangeListener((calendarView, year, month, day) -> {
            Date date = new GregorianCalendar(year, month, day, 0, 0, 0).getTime();
            if (isChoosingFromDate)
                binding.btnFrom.startAnimation(animationBounce);
            else
                binding.btnTo.startAnimation(animationBounce);

            if (isChoosingFromDate && date.compareTo(toDate) <= 0 || !isChoosingFromDate && fromDate.compareTo(date) <= 0) {
                if (isChoosingFromDate) {
                    fromDate = date;
                    setFromDateView();
                } else {
                    toDate = date;
                    setToDateView();
                }
                setCvDateView(date.getTime());
            } else {
                toastInvalidDateRange();
            }
        });

        binding.btnFrom.setOnClickListener(view1 -> {
            isChoosingFromDate = true;
            updateFromToColors();
            binding.cvDate.startAnimation(animationExpand);
            setCvDateView(fromDate.getTime());

        });
        binding.btnTo.setOnClickListener(view12 -> {
            isChoosingFromDate = false;
            updateFromToColors();
            binding.cvDate.startAnimation(animationExpand);
            setCvDateView(toDate.getTime());
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogThemeWhite);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    if (callBack != null)
                        callBack.proceed();
                    if (dialog != null)
                        dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    if (dialog != null) {
                        callBack.cancel();
                        dialog.cancel();
                    }
                });
        builder.setCancelable(false);
        dialogDatePicker = builder.create();
        dialogDatePicker.show();
    }

    private void toastInvalidDateRange() {
        Toast.makeText(activity, "invalid date range", Toast.LENGTH_SHORT).show();
    }

    private void updateFromToColors() {
        if (isChoosingFromDate) {
            binding.btnFrom.setTextColor(Color.WHITE);
            binding.btnTo.setTextColor(ContextCompat.getColor(activity, R.color.silver));
        } else {
            binding.btnFrom.setTextColor(ContextCompat.getColor(activity, R.color.silver));
            binding.btnTo.setTextColor(Color.WHITE);
        }
    }

    void setChoosingFromDate(boolean is) {
        this.isChoosingFromDate = is;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate.setTime(fromDate);
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setToDate(long toDate) {
        this.toDate.setTime(toDate);
    }

    public Date getFromDate() {
        return fromDate;
    }

    String getFromDateString() {
        return simpletDateCompactFormat.format(fromDate);
    }

    String getFromDateStringWithMonthInWord() {
        return simpletDateCompactFormatMonthInWord.format(fromDate);
    }


    public Date getToDate() {
        return toDate;
    }

    String getToDateString() {
        return simpletDateCompactFormat.format(toDate);
    }

    String getToDateStringWithMonthInWord() {
        return simpletDateCompactFormatMonthInWord.format(toDate);
    }

    synchronized void destroy() {
        if (dialogDisclaimer != null && dialogDisclaimer.isShowing())
            dialogDisclaimer.dismiss();
        if (dialogDatePicker != null && dialogDatePicker.isShowing())
            dialogDatePicker.dismiss();
    }

    synchronized void showDisclaimerDialog(final CallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogThemeWhite);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        // Inflate and set the layout for the choose_dates_picker
        // Pass null as the parent view because its going in the choose_dates_picker layout
        View view = inflater.inflate(R.layout.stamped_statement_disclaimer, null);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        titleTextView.setText(activity.getString(isArchivedStatement ? R.string.historical_statement : R.string.stamped_statement));
        contentTextView.setText(activity.getString(isArchivedStatement ? R.string.acrhived_statement_disclaimer : R.string.stamped_statement_disclaimer));
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.statements_no_download, (dialog, id) -> {
                    // sign in the user ...
                    callBack.proceed();
                    if (dialog != null)
                        dialog.dismiss();
                })
                .setNegativeButton(R.string.statements_no_thanks, (dialog, id) -> {
                    if (dialog != null) {
                        callBack.cancel();
                        dialog.cancel();
                        if (isArchivedStatement) {
                            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.ANALYTICS_SCREEN_NAME, "No selected on Archived Statements PDF Viewer disclaimer dialog", BMBConstants.TRUE_CONST);
                        } else {
                            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.ANALYTICS_SCREEN_NAME, "No selected on Stamped Statements PDF Viewer disclaimer dialog", BMBConstants.TRUE_CONST);
                        }
                    }
                });
        builder.setCancelable(false);
        dialogDisclaimer = builder.create();
        dialogDisclaimer.show();
    }

    public interface CallBack {
        void proceed();

        void cancel();
    }
}