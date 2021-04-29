/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.boundary.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsInteractor implements AnalyticsService {

    private final FirebaseAnalytics firebaseAnalytics;
    private String channel;
    private String screenName;

    public FirebaseAnalyticsInteractor(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void trackScreenView(String channel, String screenName) {
        initialise(channel, screenName);
        firebaseAnalytics.logEvent("screen_view", getBundle());
    }

    @Override
    public void trackButtonClick(String buttonName) {
        Bundle bundle = getBundle();
        bundle.putString("buttonName", buttonName);
        firebaseAnalytics.logEvent("buttonClick", bundle);
    }

    @Override
    public void trackCustomAction(String actionName) {
        Bundle bundle = getBundle();
        bundle.putString("action", actionName);
        firebaseAnalytics.logEvent("customAction", bundle);
    }

    @Override
    public void trackCustomScreenView(String screenName, String siteSection, String customScreen) {
        Bundle bundle = getBundle();
        bundle.putString("screenName", screenName);
        bundle.putString("siteSection", siteSection);
        bundle.putString("customScreens", customScreen);
        firebaseAnalytics.logEvent("customScreenView", bundle);
    }

    @Override
    public void trackLogoutPopUpYes(String screenName) {
        Bundle bundle = getBundle();
        bundle.putString("screenName", screenName);
        bundle.putString("logoutPopupYes", "1");
        firebaseAnalytics.logEvent("logoutPopUpYes", bundle);
    }

    @Override
    public void trackLogoutPopUpNo(String screenName) {
        Bundle bundle = getBundle();
        bundle.putString("screenName", screenName);
        bundle.putString("screenName", "1");
        firebaseAnalytics.logEvent("logoutPopUpNo", bundle);
    }

    private void initialise(String channel, String screenName) {
        this.channel = channel;
        this.screenName = screenName;
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", this.channel);
        bundle.putString("screenName", this.screenName);
        return bundle;
    }
}