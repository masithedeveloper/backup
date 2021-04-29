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
 */
package com.barclays.absa.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity

object DontKeepActivitiesHelper {

    private var hasShownThisSession: Boolean = false

    // SharedPreferenceService will get injected into this class in the future
    private val sharedPreferencesService: ISharedPreferenceService = SharedPreferenceService

    fun checkForDontKeepActivities(context: Context) {
        if (!hasShownThisSession) {
            hasShownThisSession = true
            if (isDontKeepActivitiesOn(context) && sharedPreferencesService.getDontKeepActivitiesCount() < 3) {
                incrementDontKeepActivitiesCount()
                navigateToDontKeepActivitiesScreen(context)
            } else {
                resetDontKeepActivitiesCount()
            }
        }
    }

    private fun isDontKeepActivitiesOn(context: Context): Boolean = Settings.Global.getInt(context.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0) == 1

    private fun navigateToDontKeepActivitiesScreen(context: Context) {
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            if (intent.resolveActivity(BMBApplication.getInstance().packageManager) != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                context.startActivity(intent)
            }
        }

        context.startActivity(Intent(context, GenericResultActivity::class.java)
                .putExtra(GenericResultActivity.IS_FAILURE, true)
                .putExtra(GenericResultActivity.SUB_MESSAGE_STRING, context.getString(R.string.dont_keep_activities_sub_message))
                .putExtra(GenericResultActivity.NOTICE_MESSAGE_STRING, context.getString(R.string.dont_keep_activities_title))
                .putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.dont_care)
                .putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.fix_problem))
    }

    private fun resetDontKeepActivitiesCount() = sharedPreferencesService.setDontKeepActivitiesCount(0)

    private fun incrementDontKeepActivitiesCount() = sharedPreferencesService.setDontKeepActivitiesCount(sharedPreferencesService.getDontKeepActivitiesCount() + 1)
}