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

package com.barclays.absa.banking.deviceLinking;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity;
import com.barclays.absa.banking.deviceLinking.ui.TermsAndConditionsSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.intent.matcher.UriMatchers.hasHost;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

public class AccountLoginActivityTest {

    @Rule
    public ActivityTestRule<AccountLoginActivity> accountLoginActivityActivityTestRule = new ActivityTestRule<>(AccountLoginActivity.class, true, false);

    @Before
    public void setup(){
        Intents.init();
        accountLoginActivityActivityTestRule.launchActivity(new Intent());
    }

    @Test
    public void shouldMakeSureLoginScreenIsLaunched() {
        onView(withId(R.id.accountNumberInputView)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldEnterLoginCredentialsThenProceedWithoutError() {
        onView(withId(R.id.accountNumberInputView)).check(matches(isDisplayed()));
        onView(withId(R.id.pinCodeInputView)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.value_edit_text), isDescendantOfA(withId(R.id.accountNumberInputView)))).perform(typeText("1111111111111111"));
        onView(allOf(withId(R.id.value_edit_text), isDescendantOfA(withId(R.id.pinCodeInputView)))).perform(typeText("1234"));
        onView(allOf(withId(R.id.value_edit_text), isDescendantOfA(withId(R.id.userNumberInputView)))).check(matches(withText("1")));

        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void shouldFailLoginIfAccountNumberIsEmpty() {
        onView(withId(R.id.accountNumberInputView)).check(matches(isDisplayed()));
        onView(withId(R.id.pinCodeInputView)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.value_edit_text), isDescendantOfA(withId(R.id.pinCodeInputView))))
                .perform(typeText("1234"));

        onView(withId(R.id.loginButton)).perform(click());

        onView(allOf(withId(R.id.error_text_view), isDescendantOfA(withId(R.id.accountNumberInputView))))
                .check(matches(withText(R.string.access_account_number_2fa_errormessage)))
                .check(matches(hasTextColor(R.color.primary_red)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldFailLoginIfPinNumberFieldIsEmpty() {
        onView(withId(R.id.accountNumberInputView)).check(matches(isDisplayed()));
        onView(withId(R.id.pinCodeInputView)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.value_edit_text), isDescendantOfA(withId(R.id.accountNumberInputView)))).perform(typeText("1111111111111111"));
        onView(withId(R.id.loginButton)).perform(click());

        onView(allOf(withId(R.id.error_text_view), isDescendantOfA(withId(R.id.pinCodeInputView))))
                .check(matches(withText(R.string.access_pin_errormessage)))
                .check(matches(hasTextColor(R.color.primary_red)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldMakeSureAbsaWebsiteCanBeOpenedByClickingLink() {
        onView(withId(R.id.visitWebsiteTextView)).check(matches(isDisplayed()));

        Intent intent = new Intent();
        String url = "http://www.absa.co.za";
        intent.setData(Uri.parse(url));

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

        onView(withId(R.id.visitWebsiteTextView)).perform(click());

        intending(allOf(hasAction(equalTo(Intent.ACTION_VIEW)),
                hasCategories(hasItem(equalTo(Intent.CATEGORY_BROWSABLE))),
                hasData(hasHost(equalTo(url))),
                toPackage("com.android.browser"))).respondWith(result);
    }

    @Test
    public void shouldMakeSureTermsAndConditionsActivityIsDisplayedWhenButtonClicked() {
        onView(withId(R.id.termsConditionsTextView)).check(matches(isDisplayed()));

        onView(withId(R.id.termsConditionsTextView)).perform(click());

        intended(hasComponent(hasClassName(TermsAndConditionsSelectorActivity.class.getName())));
    }

    @After
    public void tearDown(){
        Intents.release();
    }
}