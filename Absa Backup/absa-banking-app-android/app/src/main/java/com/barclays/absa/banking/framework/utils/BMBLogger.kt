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
package com.barclays.absa.banking.framework.utils

import android.util.Log
import com.barclays.absa.banking.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

object BMBLogger {

    private const val MINUSONE = -1

    @JvmStatic
    fun i(msg: String?): Int = i(AppConstants.LOG_TAG, msg)
    @JvmStatic
    fun e(msg: String?): Int = e(AppConstants.LOG_TAG, msg)
    @JvmStatic
    fun d(msg: String?): Int = d(AppConstants.LOG_TAG, msg)

    @JvmStatic
    fun d(o: Any?): Int = if (o == null) 0 else d(o.toString())

    @JvmStatic
    fun i(tag: String?, msg: String?): Int = if (BuildConfig.DEBUG && msg != null) Log.i(tag, msg) else MINUSONE
    @JvmStatic
    fun e(tag: String?, msg: String?): Int = if (BuildConfig.DEBUG && msg != null) Log.e(tag, msg) else MINUSONE
    @JvmStatic
    fun d(tag: String?, msg: String?): Int = if (BuildConfig.DEBUG && msg != null) Log.d(tag, msg) else MINUSONE

    @JvmStatic
    fun logPrettyJsonResponse(tag: String, jsonString: String?) {
        if (BuildConfig.DEBUG) {
            try {
                val json = JsonParser().parse(jsonString).asJsonObject
                val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
                val prettyJson = gson.toJson(json)
                log(tag, prettyJson)
                Log.d("-", " ")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    private fun log(tag: String, data: String) {
        val maxLogSize = 4076
        for (i in 0..data.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > data.length) data.length else end
            Log.d(tag, data.substring(start, end))
        }
    }
}