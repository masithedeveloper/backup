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

public interface AnalyticsView {
    void trackScreenView(String channel, String screenName);

    void trackButtonClick(String buttonName);

    void trackLogout();

    void trackCustomAction(String actionName);

    void trackUserLogin();

    void trackCustomScreenView(String screenName, String siteSection, String customScreen);

    void trackLogoutPopUpYes(String screenName);

    void trackLogoutPopUpNo(String screenName);

}
