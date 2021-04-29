/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.utils

import android.app.NotificationManager
import android.content.Context
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger.e
import java.util.*

class CrashHandler : Thread.UncaughtExceptionHandler {

    private val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private const val NOTIFICATION_ID = 12345
    }

    override fun uncaughtException(thread: Thread?, error: Throwable?) {
        val notificationManager = BMBApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (notificationManager != null) {
            try {
                notificationManager.cancel(NOTIFICATION_ID)
            } catch (ex: Throwable) {
                if (BuildConfig.DEBUG) {
                    ex.printStackTrace()
                }
            }
        }

        val map: MutableMap<String, String?> = LinkedHashMap()
        if (thread != null) map["Thread"] = thread.name
        if (error != null) {
            e("x-crash:s", error.toString())
            e("x-crash:m", error.message)
            if (BuildConfig.DEBUG) {
                error.printStackTrace()
            }
            map["e.toString"] = error.toString()
            map["e.getMessage"] = error.message
            map["e.getStackTrace.toString[]"] = Arrays.toString(error.stackTrace)

            error.cause?.let {
                map["e.getCause.toString"] = it.toString()
                map["e.getCause.getMessage"] = it.message
                map["e.getCause.getStackTrace[]"] = Arrays.toString(it.stackTrace)
            }
        }
        e("x-crash", map.toString());
        MonitoringInteractor().logEvent("ABAUncaughtCrashEvent", map);
        MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_ATTRIBUTE_TRANSAKT_SDK, BMBApplication.getInstance().transaktHandler.emCertID);
        // Analytics Call
        AnalyticsUtils.getInstance().trackApplicationCrashEvent(BaseActivity.mScreenName)
        if (thread != null && error != null) {
            defaultUncaughtExceptionHandler?.uncaughtException(thread, error)
        }
    }
}