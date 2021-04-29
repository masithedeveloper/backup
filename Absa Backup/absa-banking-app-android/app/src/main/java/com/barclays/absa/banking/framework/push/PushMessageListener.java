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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.launch.SplashActivity;
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Map;

import static com.barclays.absa.banking.framework.push.PushNotificationHelper.NOTIFICATION_CHANNELID_INBOX;
import static com.barclays.absa.banking.framework.push.PushNotificationHelper.NOTIFICATION_CHANNELID_VERIFICATION;

public class PushMessageListener extends FirebaseMessagingService {

    private static final String TAG = PushMessageListener.class.getSimpleName();
    public static final String IN_APP_TRANSACTION_ID = "tid";
    public static final String IN_APP_NOTIFICATION_IDENTIFIER = "PUSH_IN_APP_NOTIFICATION_IDENTIFIER";
    public static final String IN_APP_NOTIFICATION_EXTRAS = "extras";
    public static final String IN_APP_SHORTCUT = "inAppMessaging";
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "New message received");
        StringBuilder dataBuilder = new StringBuilder();
        final Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            for (String key : data.keySet()) {
                dataBuilder.append("\n").append(key).append(": ").append(data.get(key));
            }
        }
        String dataString = dataBuilder.toString();
        BMBLogger.d(TAG, dataString);
        final RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            showPushNotification(notification.getTitle(), notification.getBody(), dataString, data, this);
        } else if (!BMBApplication.getInstance().isInForeground()) {
            showPushNotification(data, this);
        } else {
            appCacheService.setPrimarySecondFactorDevice(true);
            BMBApplication.getInstance().listenForAuth();
        }
    }

    public void showPushNotification(String title, String body, String data, Map<String, String> dataMap, Context context) {
        Intent intent = new Intent(BMBApplication.getInstance(), SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNELID_INBOX);
        Notification notification = mBuilder.setSmallIcon(R.drawable.logo_white_full).setTicker(title).setWhen(0)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format("%s\n%s", body, data)))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_white_full))
                .setTicker(title)
                .setContentText(body + "\n" + data).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (dataMap.containsKey(IN_APP_TRANSACTION_ID)) {
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            } else {
                notificationManager.notify(0, notification);
            }
        }
    }

    public void showPushNotification(Map<String, String> dataMap, Context context) {
        BMBLogger.d("x-show::", "showPushNotification(Map, Context");
        Activity activity = BMBApplication.getInstance().getTopMostActivity();

        long millis = Calendar.getInstance().getTimeInMillis();

        Intent intent;
        if (dataMap.containsKey(BMBConstants.HAS_AUTH)) {
            intent = new Intent(activity, SureCheckAuth2faActivity.class);
        } else if (!BMBApplication.getInstance().getUserLoggedInStatus() && !dataMap.containsKey(IN_APP_TRANSACTION_ID)) {
            intent = new Intent(BMBApplication.getInstance(), SimplifiedLoginActivity.class);
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_APP_START, false);
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_TIME, millis);
        } else if (activity == null) {
            intent = new Intent(BMBApplication.getInstance(), SplashActivity.class);
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_APP_START, !dataMap.containsKey(IN_APP_TRANSACTION_ID));
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_TIME, millis);
        } else {
            intent = new Intent(BMBApplication.getInstance(), activity.getClass());
        }

        if (dataMap.containsKey(IN_APP_TRANSACTION_ID)) {
            intent.putExtra(IN_APP_NOTIFICATION_IDENTIFIER, getPushNotificationData(dataMap).getCustomTags());
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(BuildConfig.APPLICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNELID_VERIFICATION);
        String tickerText, title;
        if (dataMap.containsKey("tickerText")) {
            tickerText = dataMap.get("tickerText");
            title = tickerText;
        } else {
            tickerText = dataMap.get("alert");
            title = dataMap.get("title");
        }

        notificationBuilder.setSmallIcon(R.drawable.logo_white_full).setTicker(tickerText).setWhen(0)
                .setAutoCancel(true);
        Notification notification = notificationBuilder.setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tickerText))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_white_full))
                .setContentText(tickerText).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (dataMap.containsKey(IN_APP_TRANSACTION_ID)) {
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            } else {
                notificationManager.notify(0, notification);
            }
        }
    }

    private PushNotificationExtras getPushNotificationData(Map<String, String> dataMap) {
        PushNotificationExtras pushExtras = new PushNotificationExtras();
        try {
            String pushNotification = dataMap.get(IN_APP_NOTIFICATION_EXTRAS);
            if (pushNotification != null) {
                pushExtras = new ObjectMapper().readValue(pushNotification, PushNotificationExtras.class);
            }
        } catch (Exception e) {
            BMBLogger.d(e.toString());
        }
        return pushExtras;
    }
}