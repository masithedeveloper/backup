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
package com.barclays.absa.banking.presentation.welcome;
/*
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.v4.presentation.whats_new.WhatsNewForcedUpdate2faActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class WelcomeScreen2faActivityTest {
    @Rule
    public IntentsTestRule<WelcomeScreen2faActivity> activityTestRule = new IntentsTestRule<>(WelcomeScreen2faActivity.class);

    @Test
    public void swipingOnViewPagerShowsTheCorrectPage() {

        onView(withId(R.id.vp_welcome)).perform(swipeLeft());
        onView(withText("Payments")).check(matches(isDisplayed()));

        onView(withId(R.id.vp_welcome)).perform(swipeLeft());
        onView(withText("Transfer")).check(matches(isDisplayed()));

        onView(withId(R.id.vp_welcome)).perform(swipeLeft());
        onView(withText("Buy")).check(matches(isDisplayed()));

        onView(withId(R.id.vp_welcome)).perform(swipeRight());
        onView(withText("Transfer")).check(matches(isDisplayed()));
    }

    @Test
    public void btnLoginClickShowsLoginScreen() {
        onView(withId(R.id.btn_login)).perform(click());
        intended(hasComponent(WhatsNewForcedUpdate2faActivity.class.getName()));
    }

    @Test
    public void btnRegisterClickShowsRegisterScreen() {
        onView(withId(R.id.btn_register)).perform(click());
    }
}
*/