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

package com.barclays.absa.banking.presentation.deviceLink;

import androidx.test.rule.ActivityTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CreateNicknameActivityTest {
    @Rule
    public ActivityTestRule<CreateNicknameActivity> activityTestRule = new ActivityTestRule<>(CreateNicknameActivity.class, false, false);

    @Test
    public void deviceNicknameCanBeEntered() {
        activityTestRule.launchActivity(null);
        onView(withId(R.id.et_deviceNickname)).check(matches(isDisplayed()));
        onView(withId(R.id.et_deviceNickname)).perform(typeText("Optimus Prime"));
        //onView(withId(R.id.btn_saveAndContinue)).perform(click());
    }

    @Test
    public void btnSkipOpensNextActivity() {
        activityTestRule.launchActivity(null);
        onView(withId(R.id.saveAndContinueButton)).perform(click());
    }
}