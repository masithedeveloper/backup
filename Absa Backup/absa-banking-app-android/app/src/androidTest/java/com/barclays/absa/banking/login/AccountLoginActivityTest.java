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
package com.barclays.absa.banking.login;
/*
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.presentationlinking.AccountLoginActivity;
import com.barclays.absa.banking.deviceLinking.ui.LinkingPasswordValidationActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class AccountLoginActivityTest {
    private static final String TEST_ACCOUNT_NUMBER = "49191564878";
    @Rule
    public IntentsTestRule<AccountLoginActivity> testRule = new IntentsTestRule<>(AccountLoginActivity.class);

    @Test
    public void btnLoginOpensNextScreen() {
        onView(withId(R.id.et_accountNumber)).perform(typeText(TEST_ACCOUNT_NUMBER), closeSoftKeyboard());
        onView(withId(R.id.et_accessPin)).perform(typeText("02589"), closeSoftKeyboard());
        onView(withId(R.id.et_userNumber)).perform(typeText("312"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        intended(hasComponent(LinkingPasswordValidationActivity.class.getName()));
    }

    @Test
    public void btnHelpShowsAccountInfoDialog() {
        //onView(withId(R.id.iv_helpIcon)).perform(click());
        onView(withId(R.id.iv_closeDialogButton)).check(matches(isDisplayed()));
    }
}
*/