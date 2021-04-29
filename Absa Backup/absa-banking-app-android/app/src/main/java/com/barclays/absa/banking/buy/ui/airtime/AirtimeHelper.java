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
package com.barclays.absa.banking.buy.ui.airtime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.ValidationUtils;

import java.util.List;

import styleguide.forms.NormalInputView;
import styleguide.forms.SelectorList;

public class AirtimeHelper {
    public static SelectorList<PrepaidAmountWrapper> setupPrepaidAmountView(BaseActivity activity, NormalInputView<PrepaidAmountWrapper> amountInputView, List<String> prepaidServerAmounts) {
        SelectorList<PrepaidAmountWrapper> prepaidAmounts = new SelectorList<>();
        if (prepaidServerAmounts != null) {
            final String forString = activity.getString(R.string.forStr);
            for (int i = 0; i < prepaidServerAmounts.size(); i++) {
                prepaidAmounts.add(new PrepaidAmountWrapper(prepaidServerAmounts.get(i), forString));
            }
            amountInputView.setList(prepaidAmounts, activity.getString(R.string.sel_airtime_amount));
        }
        return prepaidAmounts;
    }

    public static Pair<String, String> getNameAndPhone(Activity activity, Intent data) {
        Uri contactUri = data.getData();
        if (contactUri != null) {
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = activity.getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.getCount() > 0)
                try {
                    cursor.moveToNext();
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replace("+27", "0").replace(" ", "");
                    return new Pair<>(name, number);
                } finally {
                    cursor.close();
                }
        }
        return null;
    }

    public static String getPhone(Activity activity, Intent data) {
        Pair<String, String> nameAndNumber = getNameAndPhone(activity, data);
        return nameAndNumber == null ? null : nameAndNumber.second;
    }

    public static void setMobileNumberInputView(Activity activity, NormalInputView mobileNumberInputView, String phone) {
        final String INVALID_MOBILE_NUMBER = activity.getString(R.string.invalid_mobile_number);
        CommonUtils.updateMobileNumberOnValidation(mobileNumberInputView, phone, INVALID_MOBILE_NUMBER);
    }

    public static String getErrorPleaseEnterValid(Activity activity, @StringRes int fieldName) {
        return activity.getString(R.string.pleaseEnterValid, activity.getString(fieldName));
    }

    public static String getErrorPleaseEnterValidPrepaidType(Activity activity) {
        return getErrorPleaseEnterValid(activity, R.string.prepaid_type_overview);
    }

    public static String getErrorPleaseEnterValidAmount(Activity activity) {
        return getErrorPleaseEnterValid(activity, R.string.amount);
    }

    public static void setMobileNumberValidation(final Activity activity, final NormalInputView mobileNumberInputView){
        final View.OnFocusChangeListener onFocusChangeListener = mobileNumberInputView.getEditText().getOnFocusChangeListener();
        mobileNumberInputView.setOnFocusChangeListener((view, hasFocus) -> {
            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusChange(view, hasFocus);
            }
            if (!hasFocus) {
                validateMobileNumber(activity, mobileNumberInputView);
            }
        });
    }

    public static boolean validateMobileNumber(Activity activity, NormalInputView mobileNumberInputView) {
        if (ValidationUtils.validatePhoneNumberInput(getMobileNumber(mobileNumberInputView.getText()))) {
            mobileNumberInputView.hideError();
            return true;
        } else {
            String errorMessage = String.format(activity.getString(R.string.invalid_mobile_number), activity.getString(R.string.ben_mobile_tv));
            mobileNumberInputView.setError(errorMessage);
            return false;
        }
    }

    public static String getMobileNumber(@NonNull String mobileNumber) {
        return mobileNumber.replace(" ", "");
    }
}