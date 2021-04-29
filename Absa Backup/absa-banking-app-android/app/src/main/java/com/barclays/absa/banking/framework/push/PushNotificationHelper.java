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

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.NonNull;

import com.barclays.absa.banking.BuildConfig;

import java.util.ArrayList;

public class PushNotificationHelper extends ContextWrapper {

    public static final String NOTIFICATION_CHANNELID_VERIFICATION = BuildConfig.APPLICATION_ID + ".Verification";
    public static final String NOTIFICATION_CHANNELID_IMPORTANT = BuildConfig.APPLICATION_ID + ".Important";
    public static final String NOTIFICATION_CHANNELID_OFFERS = BuildConfig.APPLICATION_ID + ".Offers";
    public static final String NOTIFICATION_CHANNELID_MARKETING = BuildConfig.APPLICATION_ID + ".Marketing";
    public static final String NOTIFICATION_CHANNELID_INBOX = BuildConfig.APPLICATION_ID + ".Inbox";

    public static final String NOTIFICATION_CHANNEL_VERIFICATION = "Verification requests";
    public static final String NOTIFICATION_CHANNEL_IMPORTANT = "Important information";
    public static final String NOTIFICATION_CHANNEL_OFFERS = "Offers tailored for you";
    public static final String NOTIFICATION_CHANNEL_MARKETING = "Marketing";
    public static final String NOTIFICATION_CHANNEL_INBOX = "Inbox";

    private NotificationManager notificationManager;

    public PushNotificationHelper(Context base) {
        super(base);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void createNotificationChannels() {
        ArrayList<NotificationChannel> notificationChannels = new ArrayList<>();
        notificationChannels.add(createNotificationChannel(NOTIFICATION_CHANNELID_VERIFICATION, NOTIFICATION_CHANNEL_VERIFICATION, NotificationManager.IMPORTANCE_HIGH));
        notificationChannels.add(createNotificationChannel(NOTIFICATION_CHANNELID_IMPORTANT, NOTIFICATION_CHANNEL_IMPORTANT, NotificationManager.IMPORTANCE_HIGH));
        notificationChannels.add(createNotificationChannel(NOTIFICATION_CHANNELID_OFFERS, NOTIFICATION_CHANNEL_OFFERS, NotificationManager.IMPORTANCE_DEFAULT));
        notificationChannels.add(createNotificationChannel(NOTIFICATION_CHANNELID_MARKETING, NOTIFICATION_CHANNEL_MARKETING, NotificationManager.IMPORTANCE_LOW));
        notificationChannels.add(createNotificationChannel(NOTIFICATION_CHANNELID_INBOX, NOTIFICATION_CHANNEL_INBOX, NotificationManager.IMPORTANCE_LOW));

        if (notificationManager != null) {
            notificationManager.createNotificationChannels(notificationChannels);
        }
    }

    @NonNull
    private NotificationChannel createNotificationChannel(String channelId, String channelName, int importance) {
        return new NotificationChannel(channelId, channelName, importance);
    }

}