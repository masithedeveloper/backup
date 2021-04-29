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

package com.barclays.absa.banking.presentation;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.registration.RegisterAtmValidationActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


public class WelcomeActivityTest {

    @Rule
    public IntentsTestRule<WelcomeActivity> welcomeActivityIntentsTestRule = new IntentsTestRule<>(WelcomeActivity.class);

    @Test
    public void viewPagerShouldDisplayCorrectTextOnSwipe(){
        onView(withId(R.id.welcomeViewPager));
        onView(withText(R.string.linking_welcome_screen_caption_1)).check(matches(isDisplayed()));

        onView(withId(R.id.welcomeViewPager)).perform(swipeLeft());
        onView(withText(R.string.linking_welcome_screen_caption_2)).check(matches(isDisplayed()));

        onView(withId(R.id.welcomeViewPager)).perform(swipeLeft());
        onView(withText(R.string.linking_welcome_screen_caption_3)).check(matches(isDisplayed()));

        onView(withId(R.id.welcomeViewPager)).perform(swipeLeft());
        onView(withText(R.string.linking_welcome_screen_caption_4)).check(matches(isDisplayed()));

        onView(withId(R.id.welcomeViewPager)).perform(swipeLeft());
        onView(withText(R.string.linking_welcome_screen_caption_5)).check(matches(isDisplayed()));

        onView(withId(R.id.welcomeViewPager)).perform(swipeRight());
        onView(withText(R.string.linking_welcome_screen_caption_4)).check(matches(isDisplayed()));
    }

    @Test
    public void registerButtonShouldStartRegisterScreen(){
       onView(withId(R.id.registerButton)).perform(click());
       intended(hasComponent(RegisterAtmValidationActivity.class.getName()));
    }

    @Test
    public void loginButtonShouldStartLoginScreen() {
        onView(withId(R.id.loginButton)).perform(click());
        intended(hasComponent(AccountLoginActivity.class.getName()));
    }
}
