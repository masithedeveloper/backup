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
package za.co.absa.networking

import android.util.Log

object Logger {

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            log(tag, message, false)
        }
    }

    fun e(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            log(tag, message, true)
        }
    }

    private fun log(tag: String, data: String, isError: Boolean) {
        val maxLogSize = 4024
        for (i in 0..data.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > data.length) data.length else end
            if (isError) {
                Log.e(tag, data.substring(start, end))
            } else {
                Log.d(tag, data.substring(start, end))
            }
        }
    }
}