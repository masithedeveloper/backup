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
package com.barclays.absa.banking.presentation.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.AppVersion
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.AppUpgradeUtils
import com.barclays.absa.utils.SharedPreferenceService
import java.util.*

object AppUpdateUtil {

    const val NAG_SCREEN_PROMPT_IN_DAYS = 5

    fun showForceUpdateDialog(activity: Activity, message: String) {
        AppUpgradeUtils.showMajorOptionsDialog(activity, message) { dialog, _ ->
            launchPlayStore(activity)
            dialog?.dismiss()
        }
    }

    fun launchPlayStore(activity: Activity) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=${activity.getString(R.string.app_package_name)}")
            if (isAvailable(activity, this)) {
                activity.startActivity(this)
                activity.finish()
            }
        }
    }

    fun launchOptionalUpdateScreen(activity: Activity, message: String, dismissCallback: DismissCallback) {
        AppUpgradeUtils.showMinorOptionsDialog(activity, message, { dialog, _ ->
            dialog?.dismiss()
            dismissCallback.onDismiss()
        }, { dialog, _ ->
            launchPlayStore(activity)
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_PleaseUpdateYourAppScreen_UpdateMyAppButtonClicked")
            dialog?.dismiss()
        })
    }

    private fun isAppVersionOld(): Boolean {
        val latestAppVersion = AppVersion(BMBApplication.getInstance().latestAppVersion)
        val buildAppVersion = AppVersion(BuildConfig.VERSION_NAME)

        return when {
            latestAppVersion.major > buildAppVersion.major -> true
            latestAppVersion.minor - buildAppVersion.minor >= 2 -> true
            else -> false
        }
    }

    private fun haveNotShownNagScreenRecently(): Boolean {
        val lastNagDate = SharedPreferenceService.getLastNagDate()

        return if (lastNagDate == -1L) {
            true
        } else {
            val lastDateShownPlusNagPromptDays = Calendar.getInstance().apply {
                timeInMillis = lastNagDate
                add(Calendar.DATE, NAG_SCREEN_PROMPT_IN_DAYS)
            }

            lastDateShownPlusNagPromptDays.time < Calendar.getInstance().time
        }
    }

    fun isUsingSupportedSDKVersion() = Build.VERSION.SDK_INT >= BMBApplication.getInstance().minimumSupportedSDK

    fun shouldShowNagScreenUpdate() = isAppVersionOld() && haveNotShownNagScreenRecently()

    private fun isAvailable(context: Context, intent: Intent): Boolean {
        val packageManager = context.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.isNotEmpty()
    }

    interface DismissCallback {
        fun onDismiss()
    }
}