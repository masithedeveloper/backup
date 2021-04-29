/*
 * Copyright (c) 2018 Barclays Bank Plc, All Rights Reserved.
 *
 * This code is confidential to Barclays Bank Plc and shall not be disclosed
 * outside the Bank without the prior written permission of the Director of
 * CIO
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Barclays Bank Plc.
 */

package com.barclays.absa.banking.home;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity;
import com.barclays.absa.banking.cashSend.ui.CashSendActivity;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.payments.SelectBeneficiaryPaymentActivity;
import com.barclays.absa.banking.presentation.util.ElementMatcher;
import com.barclays.absa.banking.transfer.TransferFundsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.repeatedlyUntil;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

public class HomeContainerActivityTest {

    @Rule
    public ActivityTestRule<HomeContainerActivity> homeContainerActivityActivityTestRule = new ActivityTestRule<>(HomeContainerActivity.class, false, false);

    @Before
    public void setup() {
        Intents.init();
        homeContainerActivityActivityTestRule.launchActivity(new Intent());
    }

    //we need to make sure we're logged in first before we do anything else:
    public void performLogin() {
        onView(allOf(withId(R.id.button1_text_view), isDescendantOfA(withId(R.id.numericKeypad)))).perform(click());
        onView(allOf(withId(R.id.button2_text_view), isDescendantOfA(withId(R.id.numericKeypad)))).perform(click());
        onView(allOf(withId(R.id.button5_text_view), isDescendantOfA(withId(R.id.numericKeypad)))).perform(click());
        onView(allOf(withId(R.id.button6_text_view), isDescendantOfA(withId(R.id.numericKeypad)))).perform(click());
        onView(allOf(withId(R.id.button0_text_view), isDescendantOfA(withId(R.id.numericKeypad)))).perform(click());
    }

    public void performLogout() {
        onView(withId(R.id.logout_menu_item)).perform(click());
        onView(allOf(ElementMatcher.getElementFromMatchAtPosition((withText(R.string.home_page_logout_option)), 1), isDisplayed())).perform(click());
    }

    @Test
    public void shouldMakeSureCardScrollingWorksVertically() {
        performLogin();
        onView(withId(R.id.vertical_radio_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.vertical_recycler_view)).check(matches(isDisplayed())).perform(RecyclerViewActions.scrollToPosition(60));
        performLogout();
    }

    @Test
    public void shouldMakeSureCardScrollingWorksHorizontally() {
        //when
        performLogin();
        //then
        onView(withId(R.id.horizontal_radio_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.horizontal_account_card_view_pager)).check(matches(isDisplayed()));
        //expected
        onView(withId(R.id.horizontal_account_card_view_pager)).perform(repeatedlyUntil(swipeLeft(), hasDescendant(withText("TERM DEPOSIT")), 20));
        //finally
        performLogout();
    }

    @Test
    public void shouldMakeSurePaymentHubLaunchesSuccessfully() {
        //when:
        performLogin();
        //then
        onView(allOf(withId(R.id.card_view), isDescendantOfA(withId(R.id.pay_floating_action_button_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        intended(hasComponent(hasClassName(SelectBeneficiaryPaymentActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureTransferFundsActivityLaunchesSuccessfully() {
        //when
        performLogin();
        //then
        onView(allOf(withId(R.id.card_view), isDescendantOfA(withId(R.id.transfer_floating_action_button_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        intended(hasComponent(hasClassName(TransferFundsActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureCashSendLaunchesSuccessfully() {
        //when
        performLogin();
        //then
        onView(allOf(withId(R.id.card_view), isDescendantOfA(withId(R.id.cash_send_floating_action_button_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        intended(hasComponent(hasClassName(CashSendActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureBuyAirtimeLaunchesSuccessfully() {
        //when
        performLogin();
        //then
        onView(allOf(withId(R.id.card_view), isDescendantOfA(withId(R.id.buy_airtime_floating_action_button_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        intended(hasComponent(hasClassName(BuyPrepaidActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureBuyElectricityLaunchesSuccessfully() {
        //when
        performLogin();
        //then
        onView(withId(R.id.floatingActionButtonHorizontalScrollView)).check(matches(isDisplayed())).perform(swipeLeft());
        onView(allOf(withId(R.id.card_view), isDescendantOfA(withId(R.id.buy_electricity_floating_action_button_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        intended(hasComponent(hasClassName(PrepaidElectricityActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureCreditCardHubLaunchesSuccessfully() {
        //when
        performLogin();
        //then
        onView(withId(R.id.vertical_radio_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.vertical_recycler_view)).check(matches(isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        //expected
        //intended(hasComponent(hasClassName(CreditCardHubActivity.class.getName())));
        //finally
        pressBack();
        performLogout();
    }

    @Test
    public void shouldMakeSureBottomNavigationNavigatesCorrectly() {
        //when
        performLogin();
        //then
        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.messages_menu_item), isDescendantOfA(withId(R.id.bottom_navigation_view)))).check(matches(isDisplayed())).perform(click());
        //expected
        onView(withId(R.id.notificationRecyclerView)).check(matches(isDisplayed()));

        //when
        onView(allOf(withId(R.id.more_menu_item), isDescendantOfA(withId(R.id.bottom_navigation_view)))).check(matches(isDisplayed())).perform(click());
        //expected:
        //onView(withId(R.id.explore_action_button_view)).check(matches(isDisplayed()));

        //when
        onView(allOf(withId(R.id.contact_us_menu_item), isDescendantOfA(withId(R.id.bottom_navigation_view)))).check(matches(isDisplayed())).perform(click());

        //expected
        onView(withId(R.id.tabBarView)).check(matches(isDisplayed()));

        //finally
        onView(allOf(withId(R.id.home_menu_item), isDescendantOfA(withId(R.id.bottom_navigation_view)))).check(matches(isDisplayed())).perform(click());
        performLogout();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}
