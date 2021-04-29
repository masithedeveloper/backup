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
package com.barclays.absa.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final String CLASS_TAG = "DateUtils: ";
    private static final int MIN_DATE_SELECTION = 1;
    private static final int MAX_DATE_SELECTION = 2;
    private static final String FULL_DATE_TIME = "yyyyMMdd HHmmss";

    public static final String DASHED_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DASHED_DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_DISPLAY_PATTERN = "dd MMM yyyy";
    public static final String DATE_DISPLAY_PATTERN_FULL = "dd MMMM yyyy";
    public static final String DATE_PATTERN_YEAR_MONTH = "yyyyMM";
    public static final String DATE_TIME_PATTERN = "dd MMM yyyy, h:mm aa";
    public static final String TWELVE_HOUR_TIME_PATTERN = "hh:mma";
    public static final String TWENTY_FOUR_HOUR_TIME_PATTERN = "HH:mm";
    public static final String SLASH_DATE_PATTERN = "yyyy/MM/dd";
    public static final String SLASHED_DATE_PATTERN = "dd/MM/yyyy";

    /**
     * Gets the formatted date.
     *
     * @param date       the date
     * @param currFormat the curr format
     * @param reqFormat  the req format
     * @return the formatted date
     * @throws ParseException the parse exception
     */
    public static String getFormattedDate(String date, SimpleDateFormat currFormat, SimpleDateFormat reqFormat) throws ParseException {
        if (date == null) {
            return "";
        }

        currFormat = getSimpleDateFormat(date, currFormat);

        final String formattedString = reqFormat.format(currFormat.parse(date));

        BMBLogger.d(CLASS_TAG, "Locale : " + CommonUtils.getCurrentApplicationLocale() + " ::: Date Str : " + formattedString);
        return formattedString;
    }

    public static String getFormattedDate(String date, String currFormat, String reqFormat) throws ParseException {
        return getFormattedDate(date, new SimpleDateFormat(currFormat, BMBApplication.getApplicationLocale()), new SimpleDateFormat(reqFormat, BMBApplication.getApplicationLocale()));
    }

    public static Date getDate(String date, SimpleDateFormat currFormat) throws ParseException {

        currFormat = getSimpleDateFormat(date, currFormat);

        BMBLogger.d(CLASS_TAG, "Locale : " + CommonUtils.getCurrentApplicationLocale());
        return currFormat.parse(date);
    }

    public static Date getDate(String date, String currFormat) throws ParseException {
        SimpleDateFormat format = getSimpleDateFormat(date, new SimpleDateFormat(currFormat, CommonUtils.getCurrentApplicationLocale()));

        BMBLogger.d(CLASS_TAG, "Locale : " + CommonUtils.getCurrentApplicationLocale());
        return format.parse(date);
    }

    public static Calendar getCalendar(String date, String currFormat) throws ParseException {
        SimpleDateFormat format = getSimpleDateFormat(date, new SimpleDateFormat(currFormat, CommonUtils.getCurrentApplicationLocale()));

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(format.parse(date));

        return calendar;
    }

    private static SimpleDateFormat getSimpleDateFormat(String date, SimpleDateFormat currFormat) {
        if (currFormat == null) {
            if (date.contains("/")) {
                currFormat = new SimpleDateFormat("dd/MM/yyyy", CommonUtils.getCurrentApplicationLocale());
            } else if (date.contains(BMBConstants.HYPHEN)) {
                currFormat = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
            } else {
                currFormat = new SimpleDateFormat("dd/MM/yyyy", CommonUtils.getCurrentApplicationLocale());
            }
        }

        return currFormat;
    }

    public static long getDate(String inceptionDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return df.parse(inceptionDate).getTime();
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.e(CLASS_TAG, "Failed to parse date");
            }
            return 0L;
        }
    }

    public static String formatDate(String date) {
        return formatDate(date, "yyyy-MM-dd", "dd/MM/yyyy");
    }

    public static String hyphenateDate(String date) {
        String formattedDate = null;
        if (date != null) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
                Date sourceDate = df.parse(date);
                formattedDate = df.format(sourceDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return formattedDate;
    }

    public static String hyphenateDate(Date date) {
        String formattedDate = null;
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
            formattedDate = df.format(date);
        }
        return formattedDate;
    }

    public static String formatDateMonth(String date) {
        return formatDate(date, "yyyy-MM-dd", "dd MMM yyyy");
    }

    public static String formatTravelDate(String fromDate, String toDate) {
        String fromMonth = formatDate(fromDate, "yyyyMMdd", "MMM");
        if (fromMonth.equalsIgnoreCase(formatDate(toDate, "yyyyMMdd", "MMM"))) {
            return formatDate(fromDate, "yyyyMMdd", "dd") + " - " + formatDate(toDate, "yyyyMMdd", "dd MMMM yyyy");
        } else {
            return formatDate(fromDate, "yyyyMMdd", "dd MMM yyyy") + " - " + formatDate(toDate, "yyyyMMdd", "dd MMM yyyy");
        }
    }

    public static String formatDate(String date, String srcPattern, String destPattern) {
        return formatDate(date, srcPattern, destPattern, BMBApplication.getApplicationLocale());
    }

    public static String formatDate(String date, String srcPattern, String destPattern, Locale locale) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        SimpleDateFormat srcFormat = new SimpleDateFormat(srcPattern, locale);
        SimpleDateFormat dstFormat = new SimpleDateFormat(destPattern, locale);

        try {
            Date srcDate = srcFormat.parse(date);
            return dstFormat.format(srcDate);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(CLASS_TAG, "Error parsing date", e);
            }
            return date;
        }
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, BMBApplication.getApplicationLocale());
    }

    public static String format(Date date, String pattern, Locale locale) {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(pattern, locale);
        return requiredFormat.format(date);
    }

    public static String format(Calendar calendar, String pattern) {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(pattern, BMBApplication.getApplicationLocale());
        return requiredFormat.format(calendar.getTime());
    }

    /**
     * Gets the calendar obj.
     *
     * @param currentDateStr the current date str
     * @return the calendar obj
     */
    public static Calendar getCalendarObj(String currentDateStr) {
        if (TextUtils.isEmpty(currentDateStr)) {
            return Calendar.getInstance();
        }

        Date myDate;
        final SimpleDateFormat dateFormatWithHyphen = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
        try {
            myDate = dateFormatWithHyphen.parse(currentDateStr);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            return calendar;
        } catch (final ParseException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return null;
        }
    }

    public static String setTimeOfDayGreetingMessage(Context context) {
        String greetingMessage;
        Calendar calendar = Calendar.getInstance();

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = calendar.get(Calendar.MINUTE);
        int AM_PM = calendar.get(Calendar.AM_PM);
        int AFTERNOON_START_HOUR = 12;
        int EVENING_START_TIME = 17;
        int START_MINUTE = 1;

        if (AM_PM == Calendar.PM) {

            if (hourOfDay == AFTERNOON_START_HOUR && minuteOfHour < START_MINUTE) {
                greetingMessage = context.getString(R.string.good_morning_login);
            } else if (hourOfDay == EVENING_START_TIME && minuteOfHour == START_MINUTE || hourOfDay > EVENING_START_TIME) {
                greetingMessage = context.getString(R.string.good_evening_login);
            } else {
                greetingMessage = context.getString(R.string.good_afternoon_login);
            }

        } else {
            greetingMessage = context.getString(R.string.good_morning_login);
        }

        return greetingMessage;
    }

    public static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
        return df.format(calendar.getTime());
    }

    public static String getTodaysDate(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, BMBApplication.getApplicationLocale());
        return df.format(calendar.getTime());
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return df.format(calendar.getTime());
    }

    public static String getDateWithMonthNameFromHyphenatedString(String date) {
        String formattedDate = null;
        if (date != null) {
            try {
                final SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
                Date sourceDate = sourceDateFormat.parse(date);
                formattedDate = displayDateFormat.format(sourceDate);
            } catch (ParseException e) {
                if (BuildConfig.DEBUG) {
                    BMBLogger.d(CLASS_TAG, e.getMessage());
                }
                return null;
            }
        }
        return formattedDate;
    }


    public static String removePeriod(String date) {
        if (date != null) {
            StringBuilder builder = new StringBuilder(date);
            if (date.contains(".")) {
                builder.deleteCharAt(date.indexOf("."));
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    public static String getDateWithMonthNameFromStringWithoutHyphen(String date) {
        String formattedDate = null;
        String dateNotAvailableMessage = "Date not available";
        if (date != null) {
            try {
                final SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyyMMdd", BMBApplication.getApplicationLocale());
                final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
                Date sourceDate = sourceDateFormat.parse(date);
                formattedDate = displayDateFormat.format(sourceDate);
            } catch (StringIndexOutOfBoundsException | ParseException e) {
                if (BuildConfig.DEBUG) {
                    BMBLogger.d(CLASS_TAG, e.getMessage());
                }
                return dateNotAvailableMessage;
            }
        }
        return formattedDate;
    }

    public static long getDateDiff(Date fromDate, Date toDate) {
        return getDateDiff(fromDate, toDate, TimeUnit.DAYS);
    }

    public static long getDateDiff(Date fromDate, Date toDate, TimeUnit timeUnit) {
        long diffInMillies = toDate.getTime() - fromDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Date getLocalTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FULL_DATE_TIME, BMBApplication.getApplicationLocale());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
        try {
            return getDate(simpleDateFormat.format(new Date()), FULL_DATE_TIME);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String formatCollectionDay(int day) {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
        String languageCode = appCacheService.getSecureHomePageObject().getLangCode();
        if ("E".equalsIgnoreCase(languageCode)) {
            return formatCollectionDayEnglish(day);
        } else if ("A".equalsIgnoreCase(languageCode)) {
            return formatCollectionDayAfrikaans(day);
        } else {
            return String.valueOf(day);
        }
    }

    //Do not externialise this strings. This will cause a crash if externalised
    private static String formatCollectionDayEnglish(int day) {
        if (day == 1 || day == 21 || day == 31) {
            return String.valueOf(day).concat("st of every month");
        } else if (day == 2 || day == 22) {
            return String.valueOf(day).concat("nd of every month");
        } else if (day == 3 || day == 23) {
            return String.valueOf(day).concat("rd of every month");
        } else {
            return String.valueOf(day).concat("th of every month");
        }
    }

    //Do not externialise this strings. This will cause a crash if externalised
    private static String formatCollectionDayAfrikaans(int day) {
        if (day == 1 || day == 8 || day > 19) {
            return String.valueOf(day).concat("ste dag van elke maand");
        } else {
            return String.valueOf(day).concat("de dag van elke maand");
        }
    }

    public interface OnDateSelectionCallback {
        void onDateSelected(Date date);
    }

    public static Date parseDate(String date) {
        if (date == null) {
            return null;
        }

        final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(DASHED_DATE_PATTERN, BMBApplication.getApplicationLocale());
        try {
            return simpleDateFormatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static void showDatePicker(Context context, OnDateSelectionCallback callback, long minDate) {
        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();
        final Calendar selectedCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -3);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogTheme, (datePicker, year, month, day) -> {
            selectedCalendar.set(year, month, day);
            callback.onDateSelected(selectedCalendar.getTime());
        }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), datePickerDialog);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        if (minDate == Long.MIN_VALUE) {
            datePicker.setMinDate(startCalendar.getTimeInMillis());
        } else {
            datePicker.setMinDate(minDate);
        }
        datePicker.setMaxDate(endCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public static void showDatePicker(Context context, OnDateSelectionCallback callback, Date selectedDate) {
        Calendar selectedCalenderDate = Calendar.getInstance();
        if (selectedDate != null) {
            selectedCalenderDate.setTime(selectedDate);
        }
        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -3);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogTheme, (datePicker, year, month, day) -> {
            selectedCalenderDate.set(year, month, day);
            callback.onDateSelected(selectedCalenderDate.getTime());
        }, selectedCalenderDate.get(Calendar.YEAR), selectedCalenderDate.get(Calendar.MONTH), selectedCalenderDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), datePickerDialog);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(endCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public static String getTheFirstOfNextMonthDate(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date nextMonthFirstDay = calendar.getTime();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return DateUtils.format(nextMonthFirstDay, dateFormat);
    }

    public static boolean isFromDateMore(String fromDate, String toDate, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            if (toDate != null && fromDate != null) {
                Date fromDateItem = simpleDateFormat.parse(fromDate);
                Date toDateItem = simpleDateFormat.parse(toDate);
                return fromDateItem.after(toDateItem);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isFromDateMore(String fromDate, String toDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (toDate != null && fromDate != null) {
                Date fromDateItem = simpleDateFormat.parse(fromDate);
                Date toDateItem = simpleDateFormat.parse(toDate);
                return fromDateItem.after(toDateItem);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hyphenateDateFormat_YYYY_MM_DD(String date) {
        StringBuilder dateStringBuilder = new StringBuilder(date);
        dateStringBuilder.insert(4, "-");
        dateStringBuilder.insert(7, "-");
        return dateStringBuilder.toString();
    }

    public static long numberOfMillisTillDateTimeSA(String dateTime) {
        TimeZone gmtPlus2 = TimeZone.getTimeZone("Africa/Johannesburg");
        Calendar southAfricanTime = GregorianCalendar.getInstance(gmtPlus2);
        Calendar currentTimeCal = GregorianCalendar.getInstance();
        int offsetTime = southAfricanTime.getTimeZone().getOffset(currentTimeCal.getTime().getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date targetDate = simpleDateFormat.parse(dateTime);
            return targetDate.getTime() - (currentTimeCal.getTimeInMillis() + offsetTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean givenDateMillisIsMoreThanGivenDaysAgo(long dateMillis, int daysAgo) {
        long daysAgoInMillis = daysAgo * 24 * 60 * 60 * 1000L;
        return ((new Date().getTime() - dateMillis) > daysAgoInMillis);
    }

    public static long getCalendarMinimumDate() {
        return System.currentTimeMillis() - 1000;
    }

    private static String getCurrentTimeIn24HourFormat() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

    public static String getCurrentDateAndTime() {
        String dateFormat = "dd MMM yyyy";
        String date = DateUtils.getTodaysDate(dateFormat);
        String time = DateUtils.getCurrentTimeIn24HourFormat();
        return BMBApplication.getInstance().getString(R.string.payment_at_time_custom, date, time);
    }

}
