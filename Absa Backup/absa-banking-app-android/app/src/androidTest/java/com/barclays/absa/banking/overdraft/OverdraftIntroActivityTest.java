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
package com.barclays.absa.banking.overdraft;

import android.content.Context;
import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.presentation.BaseTest;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.TextFormatUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OverdraftIntroActivityTest extends BaseTest {
    public static final double amount = 7000;
    @Rule
    public ActivityTestRule<OverdraftIntroActivity> mActivityTestRule = new ActivityTestRule<OverdraftIntroActivity>(OverdraftIntroActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, OverdraftIntroActivity.class);
            result.putExtra(IntentFactory.NEW_OVERDRAFT_LIMIT, amount);
            return result;
        }
    };

    @Test
    public void overdraftIntroActivityTest() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_qualify_title)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_overdraft_amount)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_overdraft_amount)).check(matches(withText("R " + TextFormatUtils.formatBasicAmount(amount))));

        onView(withId(R.id.tv_overdraft_info)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_lets_go)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_overdraft_disclaimer)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_lets_go)).perform(click());
        sleep(1000);
        // Step 1: Tell us about yourself
        onView(withId(R.id.tv_tell_us_title)).check(matches(isDisplayed()));
        sleep(1000);
    }
}