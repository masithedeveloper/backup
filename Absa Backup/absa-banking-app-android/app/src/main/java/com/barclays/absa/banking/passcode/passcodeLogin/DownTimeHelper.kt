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

package com.barclays.absa.banking.passcode.passcodeLogin

import android.app.Activity
import android.content.Intent
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.genericResult.MaintenanceResultActivity

object DownTimeHelper {
    var isDownTimeEnabled = false
    var downTimeType: String = ""
    var downTimeMessageMaintenance = ""
    var downTimeMessageUnplanned = ""

    fun showMaintenancePage(activity: Activity) {
        if (downTimeType == DownTimeType.MAINTENANCE.name) {
            showMaintenancePage(activity, downTimeMessageMaintenance)
        } else {
            showMaintenancePage(activity, downTimeMessageUnplanned)
        }
    }

    private fun showMaintenancePage(activity: Activity, message: String) {

        val subMessage = if (downTimeType == DownTimeType.MAINTENANCE.name) "$message\n\n${activity.getString(R.string.maintenance_instruction)}".trimIndent() else message

        Intent(activity, MaintenanceResultActivity::class.java).apply {
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.maintenance_title)
            putExtra(GenericResultActivity.SUB_MESSAGE_STRING, subMessage)
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close)

            if (downTimeType == DownTimeType.MAINTENANCE.name) {
                putExtra(GenericResultActivity.CLICKABLE_TEXT, activity.getString(R.string.maintenance_instruction_url))
                putExtra(GenericResultActivity.CLICKABLE_TYPE, GenericResultActivity.ClickableType.URL)
            }

            activity.startActivity(this)
        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }
    }
}

enum class DownTimeType {
    MAINTENANCE,
    UNPLANNED
}