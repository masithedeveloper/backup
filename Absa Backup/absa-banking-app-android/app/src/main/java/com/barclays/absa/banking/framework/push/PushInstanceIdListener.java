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
package com.barclays.absa.banking.framework.push;

import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.UserSettingsManager;
import com.entersekt.sdk.TransaktSDK;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class PushInstanceIdListener extends FirebaseMessagingService {

    private static final String TAG = PushInstanceIdListener.class.getSimpleName();

    @Override
    public void onNewToken(@NotNull String refreshedToken) {
        super.onNewToken(refreshedToken);
        if (FirebaseInstanceId.getInstance() != null) {
            if (refreshedToken != null) {
                UserSettingsManager.INSTANCE.setShouldRegisterPushNotificationIdWithBackend(true);
                BMBLogger.d(TAG, "Refreshed token: " + refreshedToken);
                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                firebaseMessaging.subscribeToTopic("all-devices");
                firebaseMessaging.subscribeToTopic("android-devices");
                try {
                    TransaktSDK.getConfig().setGoogleCloudMessagingId(refreshedToken);
                } catch (NullPointerException e) {
                    new MonitoringInteractor().logTechnicalEvent(TAG, "", e.getMessage());
                }
            }
        }
    }
}