package com.money.randing.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.money.randing.R
import com.money.randing.constant.K
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RandingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createInAppUpdatesNotificationChannel()
    }

    private fun createInAppUpdatesNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.in_app_update_notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel =
                NotificationChannel(K.APP_UPDATE_NOTIFICATION_CHANNEL_ID, name, importance)
            NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
        }
    }
}