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
package com.barclays.absa.banking.framework.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The Class DecimalDigitsInputFilter.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    /** The pattern. */

    // Changed the DecimalDigitsInputFilter 26 March 2012 by Devdatta

    private final Pattern mPattern;

    /**
     * Instantiates a new decimal digits input filter.
     * 
     * @param digitsBeforeZero the digits before zero
     * @param digitsAfterZero the digits after zero
     */
    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
    	// mPattern = Pattern.compile("([1-9]{1}[0-9]{0,2}([0-9]{3})*(\\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|(\\.[0-9]{1,2})?)");
        this.mPattern = Pattern.compile("[,0-9]{0," + (digitsBeforeZero) + "}((\\.[0-9]{0," + (digitsAfterZero) + "})?)||(\\.)?");
    }

    /* (non-Javadoc)
     * @see android.text.InputFilter#filter(java.lang.CharSequence, int, int, android.text.Spanned, int, int)
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

    		String formatedSource = source.subSequence(start, end).toString();

    	    String destPrefix = dest.subSequence(0, dstart).toString();

    	    String destSuffix = dest.subSequence(dend, dest.length()).toString();

    	    String result = destPrefix + formatedSource + destSuffix;

    	    Matcher matcher = mPattern.matcher(result);

    	    if (matcher.matches()) {
    	        BMBLogger.d("x-x-filter", source + " matches. " + dest);
    	        return null;
    	    } else {
                BMBLogger.d("x-x-filter", source + " No match. "  +dest);
                return "";

            }
    }
}