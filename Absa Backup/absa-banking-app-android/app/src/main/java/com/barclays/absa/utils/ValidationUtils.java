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

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.app.BMBApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import styleguide.forms.BaseInputView;
import styleguide.forms.NormalInputView;

public class ValidationUtils {

    public static boolean validateInput(@NonNull BaseInputView field, @NonNull String fieldName) {
        String textValue = field.getSelectedValueUnmasked().trim();
        // Add more validations here.
        if (TextUtils.isEmpty(textValue)) {
            final String errorMessage = String.format(field.getContext().getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", "").toLowerCase());
            field.setError(errorMessage);
            announceFieldErrorFromTextWidget(field.getErrorTextView());
            return false;
        }
        field.hideError();
        return true;
    }

    /**
     * Validate amount input.
     *
     * @param field     the field
     * @param fieldName the field name
     * @param ownAmt    the own amt
     * @param minOwnAmt the min own amt
     * @param maxOwnAmt the max own amt
     * @return true, if successful
     */
    public static String validateAmountInput(EditText field, String fieldName, double ownAmt, double minOwnAmt, double maxOwnAmt) {
        if (field == null) return "";
        // Add more validations here.
        if (TextUtils.isEmpty(field.getText().toString().trim())) {
            return String.format(field.getContext()
                    .getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", "")
                    .toLowerCase());
        } else if ((ownAmt < minOwnAmt) || (ownAmt > maxOwnAmt)) {
            return String.format(field.getContext().getString(R.string.own_amount_error_message), (int) minOwnAmt, (int) maxOwnAmt);
        } else {
            return "";
        }
    }

    public static boolean validateAmountInput(@NonNull BaseInputView baseInputView, String fieldName, double ownAmt, double minOwnAmt, double maxOwnAmt) {
        String error = validateAmountInput(baseInputView.getEditText(), fieldName, ownAmt, minOwnAmt, maxOwnAmt);
        if (TextUtils.isEmpty(error)) {
            return true;
        } else {
            baseInputView.setError(error);
            announceFieldErrorFromTextWidget(baseInputView.getErrorTextView());
            return false;
        }
    }

    private static boolean updateField(EditText field, String errorMessage) {
        field.setVisibility(View.VISIBLE);
        field.setError(errorMessage);
        field.requestFocus();
        announceFieldErrorFromTextWidget(field);
        return false;
    }

    private static boolean updateField(@NonNull NormalInputView field, String errorMessage) {
        field.setVisibility(View.VISIBLE);
        field.setError(errorMessage);
        field.showError();
        announceFieldErrorFromNormalInputView(field);
        return false;
    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        if (TextUtils.isEmpty(mobileNumber)) {
            return false;
        } else {
            mobileNumber = mobileNumber.trim();
            final int MOBILE_NUMBER_LENGTH = 10;
            return (mobileNumber.length() == MOBILE_NUMBER_LENGTH && mobileNumber.startsWith("0"));
        }
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        return styleguide.utils.ValidationUtils.isValidEmailAddress(emailAddress);
    }

    public static void announceFieldErrorFromNormalInputView(@NonNull NormalInputView normalInputView) {

        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            AccessibilityUtils.announceErrorFromTextWidget(normalInputView.getErrorTextView());
        }
    }

    public static void announceFieldErrorFromTextWidget(@NonNull TextView textViewWidget) {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            AccessibilityUtils.announceErrorFromTextWidget(textViewWidget);
        }
    }

    public static boolean validatePhoneNumberInput(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll(" ", "");
        final int phoneNumberLimit = BMBApplication.getInstance().getResources().getInteger(R.integer.limit_cell_fax_numbers);
        return !TextUtils.isEmpty(phoneNumber) && (phoneNumber.length() == phoneNumberLimit) && phoneNumber.startsWith("0");
    }

    /**
     * Validate atm pin.
     *
     * @param field      the field
     * @param fieldName  the field name
     * @param pin_length the pin_length
     * @return true, if successful
     */
    public static boolean validateATMPin(EditText field, String fieldName, int pin_length) {

        // Add more validations here.
        if (TextUtils.isEmpty(field.getText().toString().trim())) {
            final String errorMessage = String.format(field.getContext()
                    .getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", ""));
            return updateField(field, errorMessage);
        } else if (field.getText().toString().trim().length() != pin_length) {
            final String errorMessage = String.format(field.getContext()
                    .getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", ""));
            return updateField(field, errorMessage);
        }
        field.setError(null);
        return true;
    }

    public static boolean validateATMPin(@NonNull NormalInputView field, String fieldName, int pin_length) {
        // Add more validations here.
        if (TextUtils.isEmpty(field.getText().trim())) {
            final String errorMessage = String.format(field.getContext()
                    .getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", ""));
            return updateField(field, errorMessage);
        } else if (field.getText().trim().length() != pin_length) {
            final String errorMessage = String.format(field.getContext()
                    .getString(R.string.pleaseEnterValid), fieldName.replaceAll(":", ""));
            return updateField(field, errorMessage);
        }
        field.hideError();
        return true;
    }

    public static boolean validateEmailInputBase(NormalInputView field, String errorMessage) {
        String trimmedInput = field.getText().trim();
        if (TextUtils.isEmpty(trimmedInput)) {
            return updateField(field, errorMessage);
        }

        if (styleguide.utils.ValidationUtils.isValidEmailAddress(trimmedInput)) {
            field.clearError();
            return true;
        }

        return updateField(field, errorMessage);

    }

    public static boolean validatePhoneNumber(NormalInputView field, String fieldName) {
        String phoneNumber = field.getText().replaceAll(" ", "");
        int phoneNumberLimit = BMBApplication.getInstance().getResources().getInteger(R.integer.limit_cell_fax_numbers);

        // Add more validations here.
        if (!TextUtils.isEmpty(phoneNumber) && (phoneNumber.length() == phoneNumberLimit) && phoneNumber.startsWith("0")) {
            return true;
        } else {
            String errorMessage = String.format(field.getContext().getString(R.string.enter_valid_number), fieldName.replaceAll(":", "").toLowerCase());
            return updateField(field, errorMessage);
        }
    }

    public static boolean isValidSouthAfricanIdNumber(String idNumber) {
        if (idNumber == null) {
            return false;
        }

        String regex = "^([0-9]){2}(((0[1-9]|1[0-2])([01][1-9]|10|2[0-8]))|((0[2])(29))|((0[13-9]|1[0-2])(29|30))|((0[13578]|1[02])31))([0-9]){4}([0-1])([0-9]){2}?$";
        return idNumber.matches(regex) && calculateIdNumberControlDigit(idNumber).equals(idNumber.substring(idNumber.length() - 1));
    }

    private static String calculateIdNumberControlDigit(String idNumber) {
        int controlDigit = -1;
        try {
            int dateDigits = 0;
            for (int i = 0; i < 6; i++) {
                dateDigits += Integer.parseInt(String.valueOf(idNumber.charAt(2 * i)));
            }
            int offset = 0;
            for (int i = 0; i < 6; i++) {
                offset = offset * 10 + Integer.parseInt(String.valueOf(idNumber.charAt(2 * i + 1)));
            }
            offset *= 2;
            int modDigit = 0;
            do {
                modDigit += offset % 10;
                offset = offset / 10;
            }
            while (offset > 0);
            modDigit += dateDigits;
            controlDigit = 10 - (modDigit % 10);
            if (controlDigit == 10) controlDigit = 0;
        } catch (Exception ignored) {
        }
        return String.valueOf(controlDigit);
    }

    static boolean isValidSouthAfricanPassportNumber(String passportNumber) {
        String regex = "([0-9]+[a-zA-Z][0-9a-zA-Z]*)|([a-zA-Z]+[0-9][0-9a-zA-Z]*)";
        return passportNumber.matches(regex);
    }

    public static boolean isValidATMCardNumber(String value) {
        String textValue = value == null ? "" : value.trim().replace(" ", "");
        return textValue.length() == 16;
    }

    public static boolean isValidATMPin(String pin) {
        return (pin != null && (pin.length() == 4 || pin.length() == 5));
    }

    public static boolean isValidSurname(String surname) {
        Pattern surnameRegex = Pattern.compile("(^[a-zA-Z ]+)((?:[-'])(?=[a-zA-Z]{2,}))?([a-zA-Z]{2,})?");
        Matcher matcher = surnameRegex.matcher(surname);

        return matcher.matches();
    }

    public static boolean isValidAgeRange(String idNumber, int minAge, int maxAge) {
        Calendar calendar = Calendar.getInstance();
        String currentYearInFull = String.valueOf(calendar.get(Calendar.YEAR));

        int currentCentury = Integer.parseInt(currentYearInFull.substring(0, 2));
        int currentYear = Integer.parseInt(currentYearInFull.substring(2, 4));
        int idYear = Integer.parseInt(idNumber.substring(0, 2));
        int century = currentYear >= idYear ? currentCentury : currentCentury - 1;
        try {
            Date dateOfBirth = DateUtils.getDate(century + idNumber.substring(0, 6), new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
            if (dateOfBirth != null) {
                long diffInMillis = Math.abs(dateOfBirth.getTime() - calendar.getTimeInMillis());
                double diffInYears = diffInMillis / 3.156e+10;
                return diffInYears >= minAge && diffInYears <= maxAge;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    public static boolean isOlderThan18(String idNumber) {
        Calendar calendar = Calendar.getInstance();
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        int currentDecade = Integer.parseInt(currentYear.substring(2, 4));
        int idDecade = Integer.parseInt(idNumber.substring(0, 2));

        int year = Integer.parseInt(currentYear.substring(0, 2));
        int yearStart = currentDecade >= idDecade ? year : year - 1;

        Date dateMyDOB = null;
        try {
            dateMyDOB = DateUtils.getDate(yearStart + idNumber.substring(0, 6), new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateMyDOB != null) {
            long diff = Math.abs(dateMyDOB.getTime() - calendar.getTime().getTime());
            double diffYears = diff / 3.156e+10;

            return diffYears >= 18;
        }
        return false;
    }
}
