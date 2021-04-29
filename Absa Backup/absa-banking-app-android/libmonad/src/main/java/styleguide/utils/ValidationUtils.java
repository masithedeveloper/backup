/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package styleguide.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);

    public static boolean isValidMobileNumber(String mobileNumber) {
        mobileNumber = mobileNumber.trim();
        final int MOBILE_NUMBER_LENGTH = 10;
        return (mobileNumber.length() == MOBILE_NUMBER_LENGTH && mobileNumber.startsWith("0"));
    }

    public static boolean isValidFaxNumber(String faxNumber) {
        faxNumber = faxNumber.trim();
        final int FAX_NUMBER_LENGTH = 10;
        return (faxNumber.length() == FAX_NUMBER_LENGTH);
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        emailAddress = emailAddress.trim();
        if (!TextUtils.isEmpty(emailAddress)) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
            return matcher.matches();
        }
        return false;
    }
}
