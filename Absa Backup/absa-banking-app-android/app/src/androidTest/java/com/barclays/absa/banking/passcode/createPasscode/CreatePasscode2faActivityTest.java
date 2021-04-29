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

package com.barclays.absa.banking.passcode.createPasscode;

import androidx.test.rule.ActivityTestRule;

import com.barclays.absa.banking.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

public class CreatePasscode2faActivityTest {
    @Rule
    public ActivityTestRule<CreatePasscodeActivity> activityTestRule = new ActivityTestRule<>(CreatePasscodeActivity.class);

    @Test
    public void newPasscodeCanBeEntered() {
        onView(withId(R.id.btn_number2)).perform(click());
        onView(withId(R.id.btn_number7)).perform(click());
        onView(withId(R.id.btn_number3)).perform(click());
        onView(withId(R.id.btn_number5)).perform(click());
        onView(withId(R.id.btn_number0)).perform(click());
    }
}