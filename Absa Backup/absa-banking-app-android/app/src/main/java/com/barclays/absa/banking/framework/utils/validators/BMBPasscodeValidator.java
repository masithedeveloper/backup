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
package com.barclays.absa.banking.framework.utils.validators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BMBPasscodeValidator {
    private static final Integer PASSCODE_RULE_REQUIRED_LENGTH = 5;
    private static final Integer PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_RECURRING_DIGITS = 3;
    private static final Integer PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_SEQUENTIAL_DIGITS = 3;

    public static String validPasscodeWithMessageIgnoreLengthOfPin(String passcode) {
        return validatePasscodeWithMessage(passcode, true);
    }

    public static String validatePasscodeWithMessage(String passcode) {
        return validatePasscodeWithMessage(passcode, false);
    }

    private static String validatePasscodeWithMessage(String passcode, boolean isIgnoreLength) {
        Pattern pattern = Pattern.compile("[0-9]{5,5}");
        Matcher matcher = pattern.matcher(passcode);

        if (!isIgnoreLength && !matcher.find()) {
            return "Passcode must be numeric and exactly " + PASSCODE_RULE_REQUIRED_LENGTH + " numbers.";
        }

        List<Integer> passcodeDigits = new ArrayList<>();
        for (int i = 0; i < passcode.length(); ++i) {
            Integer digit = Integer.parseInt(passcode.substring(i, i + 1));
            passcodeDigits.add(digit);
        }

        if (containsRepeatedDigits(passcodeDigits, PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_RECURRING_DIGITS)) {
            return PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_RECURRING_DIGITS + " or more numbers may not be repeated consecutively (eg. 555).";
        }

        if (containsSequentialDigits(passcodeDigits, PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_SEQUENTIAL_DIGITS)) {
            return PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_SEQUENTIAL_DIGITS + " or more numbers may not be sequential (eg. 123).";
        }

        Collections.reverse(passcodeDigits);
        if (containsSequentialDigits(passcodeDigits, PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_SEQUENTIAL_DIGITS)) {
            return PASSCODE_RULE_MAXIMUM_SUCCESSIVELY_SEQUENTIAL_DIGITS + " or more numbers may not be sequential (eg. 321).";
        }

        if (passcodeDigitsContainRepeatedDuplicateDigitPairs(passcodeDigits)) {
            return "Number pairs cannot be repeated. (eg. 5757).";
        }

        return "";
    }

    private static boolean containsRepeatedDigits(List<Integer> passcodeDigits, Integer maximumOccurances) {
        int occurancesForCurrentDigit = 1;
        int index = 0;
        while (occurancesForCurrentDigit < maximumOccurances && index < (passcodeDigits.size() - 1)) {
            if (passcodeDigits.get(index).intValue() == passcodeDigits.get(index + 1).intValue()) {
                occurancesForCurrentDigit++;
                if (occurancesForCurrentDigit >= maximumOccurances) {
                    return true;
                }
            } else {
                occurancesForCurrentDigit = 1;
            }
            index++;
        }
        return false;
    }

    private static boolean containsSequentialDigits(List<Integer> passcodeDigits, Integer maximumOccurances) {
        int occurancesForCurrentDigit = 1;
        int index = 0;
        while (occurancesForCurrentDigit < maximumOccurances && index < (passcodeDigits.size() - 1)) {
            if (passcodeDigits.get(index) == passcodeDigits.get(index + 1) - 1) {
                occurancesForCurrentDigit++;
                if (occurancesForCurrentDigit >= maximumOccurances) {
                    return true;
                }
            } else {
                occurancesForCurrentDigit = 1;
            }
            index++;
        }
        return false;
    }

    private static boolean passcodeDigitsContainRepeatedDuplicateDigitPairs(List<Integer> passcodeDigits) {
        if (passcodeDigits.size() < 5) {
            return false;
        }

        if (passcodeDigits.get(0).intValue() == passcodeDigits.get(2).intValue() && passcodeDigits.get(1).intValue() == passcodeDigits.get(3).intValue()) {
            return true;
        }

        return passcodeDigits.get(1).intValue() == passcodeDigits.get(3).intValue() && passcodeDigits.get(2).intValue() == passcodeDigits.get(4).intValue();
    }
}
