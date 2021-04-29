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

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants

object NetworkUtils {

    fun isNetworkConnected(): Boolean = (BMBApplication.getInstance().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected ?: false

    @JvmStatic
    fun hasServerMaintenanceError(message: String?): Boolean {
        return message != null && (message.contains(BMBConstants.INVALID_RESPONSE_DATA_FORMAT)
                || message.contains(BMBConstants.BMB_FRAMEWORK_ERROR)
                || message.contains(BMBConstants.FUNCTION_NOT_DEFINED)
                || message.contains(BMBConstants.REQUEST_TIMEOUT)
                || message.contains(BMBConstants.SYSTEM_OFFLINE)
                || message.contains(BMBConstants.TECHNICAL_DIFFICULTIES))
    }

    @JvmStatic
    fun urlEncode(params: Map<String, String>): String? = with(Uri.Builder()) {
        for ((key, value) in params.entries) {
            appendQueryParameter(key, value)
        }
        build().encodedQuery
    }
}