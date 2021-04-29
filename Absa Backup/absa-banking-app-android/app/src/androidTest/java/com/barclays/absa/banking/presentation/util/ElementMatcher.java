/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *   outside the Bank without the prior written permission of the Absa Legal
 *
 *   In the event that such disclosure is permitted the code shall not be copied
 *   or distributed other than on a need-to-know basis and any recipients may be
 *   required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.presentation.util;

import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ElementMatcher {

    public static Matcher<View> getElementFromMatchAtPosition(final Matcher<View> matcher, final int position) {
        return new BaseMatcher<View>() {
            int counter = 0;
            @Override
            public boolean matches(Object item) {
                if (matcher.matches(item)) {
                    if (counter == position) {
                        counter++;
                        return true;
                    }
                    counter++;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Element at hierarchy position: " + position);
            }
        };
    }
}
