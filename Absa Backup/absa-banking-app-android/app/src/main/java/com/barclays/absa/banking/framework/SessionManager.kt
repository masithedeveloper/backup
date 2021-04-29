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
package com.barclays.absa.banking.framework

import java.util.*

object SessionManager {

    private const val SECOND = 60 * 1000
    private const val SESSION_INTERVAL_CHECK_SECONDS = 2

    private val timer: Timer = Timer()
    private var timerTask: TimerTask? = null

    @JvmStatic
    var timerListener: TimerListener? = null
    @JvmStatic
    var isSessionStarted: Boolean = false

    interface TimerListener {
        fun timerReached()
    }

    @JvmStatic
    fun startSession() {
        createTimerTask()
        timer.schedule(timerTask, 0, SESSION_INTERVAL_CHECK_SECONDS * SECOND.toLong())
    }

    @JvmStatic
    fun resetSession() {
        isSessionStarted = false
        timerTask?.cancel()
    }

    private fun createTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (isSessionStarted) {
                    timerListener?.timerReached()
                }
                isSessionStarted = true
            }
        }
    }
}