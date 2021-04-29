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
package com.barclays.absa.banking.framework.analytics;

import com.adobe.mobile.Analytics;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.util.HashMap;

public final class AnalyticsUtils {

    private static final AnalyticsUtils mAnalyticsUtils;
    private final HashMap<String, Object> contextData = new HashMap<>();

    private AnalyticsUtils() {
    }

    static {
        mAnalyticsUtils = new AnalyticsUtils();
    }

    public static AnalyticsUtils getInstance() {
        return mAnalyticsUtils;
    }

    public void trackCustomScreenView(String screenName, String siteSection, String customScreen) {
        contextData.clear();
        contextData.put("absa.screenName", screenName);
        contextData.put("absa.siteSection", siteSection);
        contextData.put("absa.customScreens", customScreen);
        Analytics.trackState("home", contextData);
    }

    public void trackCustomScreen(String screenName, String siteSection, String customScreen) {
        contextData.clear();
        contextData.put("absa.screenName", screenName);
        contextData.put("absa.siteSection", siteSection);
        contextData.put("absa.customScreens", customScreen);
        Analytics.trackState(screenName, contextData);
    }

    /**
     * Track event on every share
     */
    public void trackAirDropShare() {
        contextData.clear();
        contextData.put("absa.shareType", BMBConstants.SHARE_CONST);
        contextData.put("absa.share", BMBConstants.TRUE_CONST);
        Analytics.trackState(BMBConstants.SHARE_CONST, contextData);
    }

    public void trackAppActionStart(String screenName, String siteSection, String bankingAction) {
        contextData.clear();
        contextData.put("absa.screenName", screenName);
        contextData.put("absa.siteSection", siteSection);
        contextData.put("absa.bankingAction", bankingAction);
        contextData.put("absa.actionStart", "1");
        Analytics.trackState(screenName, contextData);
    }

    //////// Duplicate of previous event //////////

    public void trackAppError(String screenName, String siteSection, String errorMessage) {
        contextData.clear();
        contextData.put("absa.screenName", screenName);
        contextData.put("absa.siteSection", siteSection);
        contextData.put("absa.errorMessage", errorMessage);
        contextData.put("absa.actionSuccess", "1");
        Analytics.trackState(screenName, contextData);
    }

    public void trackCancelButton(String screenName, String siteSection) {
        contextData.clear();
        contextData.put("absa.screenName", screenName);
        contextData.put("absa.siteSection", siteSection);
        contextData.put("absa.bankingAction", screenName);
        contextData.put("absa.cancelbutton", "1");
        Analytics.trackState(screenName.concat(" cancel"), contextData);
    }

    public void trackLogoutPopUpYes(String mScreenName) {
        contextData.clear();
        contextData.put("absa.screenName", mScreenName);
        contextData.put("absa.logoutPopupYes", "1");
        Analytics.trackState("logout popup", contextData);
    }

    public void trackLogoutPopUpNo(String mScreenName) {
        contextData.clear();
        contextData.put("absa.screenName", mScreenName);
        contextData.put("absa.logoutPopupNo", "1");
        Analytics.trackState("logout popup", contextData);
    }

    public void trackActionStart(String mScreen, String mSiteSection, String mBankingAction, String mCustomScreen) {
        contextData.clear();
        contextData.put("absa.screenName", mScreen);
        contextData.put("absa.siteSection", mSiteSection);
        contextData.put("absa.bankingAction", mBankingAction);
        contextData.put("absa.actionStart", mCustomScreen);
        Analytics.trackState(mScreen, contextData);
    }

    public void trackActionSuccess(String mScreen, String mSiteSection, String mBankingAction, String mCustomScreen) {
        contextData.clear();
        contextData.put("absa.screenName", mScreen);
        contextData.put("absa.siteSection", mSiteSection);
        contextData.put("absa.bankingAction", mBankingAction);
        contextData.put("absa.actionSuccess", mCustomScreen);
        Analytics.trackState(mScreen, contextData);
    }


    public void trackSearchEvent(String mScreen) {
        contextData.clear();
        contextData.put("absa.screenName", mScreen);
        contextData.put("absa.searchUsed", "1");
        Analytics.trackState(mScreen, contextData);

    }

    public void trackRecentlyPaidEvent(String mScreen) {
        contextData.clear();
        contextData.put("absa.screenName", mScreen);
        contextData.put("absa.recentlyPaid", "1");
        Analytics.trackState(mScreen, contextData);
    }

    public void trackApplicationCrashEvent(String mScreenName) {
        contextData.clear();
        contextData.put("absa.screenName", mScreenName);
        contextData.put("absa.appForcedClose", "1");
        Analytics.trackState(mScreenName, contextData);
    }
}


