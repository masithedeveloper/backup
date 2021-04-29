/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.framework.app

import android.content.Context
import android.os.Handler
import com.barclays.absa.banking.BuildConfig
import com.imimobile.connect.core.ICLogger
import com.imimobile.connect.core.IMIconnect
import com.imimobile.connect.core.enums.ICLogTarget
import com.imimobile.connect.core.enums.ICLogType

object IMIHelper {

    fun startIMI(context: Context) {
        val handler = Handler(BMBApplication.getInstance().backgroundHandlerThread.looper)
        handler.post {
            try {
                if (BuildConfig.DEBUG) {
                    ICLogger.startup(context)
                    ICLogger.setLogOptions(ICLogType.Debug, ICLogTarget.Console)
                }
                IMIconnect.startup(context)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }
}