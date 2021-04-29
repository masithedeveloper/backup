/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.framework.app

import android.media.MediaPlayer
import android.media.RingtoneManager
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.UserSettingsManager

object MediaPlayerHelper {
    private var mediaPlayer: MediaPlayer? = null

    fun playNotificationSound() {
        if (UserSettingsManager.isSoundEnabled()) {
            try {
                val notificationAlert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(BMBApplication.getInstance(), notificationAlert)
                val audioAttributes = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                mediaPlayer?.setAudioAttributes(audioAttributes)
                mediaPlayer?.setOnCompletionListener { _ ->
                    stopNotificationSound()
                }

                mediaPlayer?.setOnErrorListener { _, _, _ ->
                    stopNotificationSound()
                    true
                }

                mediaPlayer?.setOnPreparedListener {
                    try {
                        mediaPlayer?.start()
                    } catch (e: IllegalStateException) {
                        BMBLogger.e(BMBConstants.LOGTAG, e.message)
                    }
                }
                mediaPlayer?.prepareAsync()
            } catch (e: Exception) {
                BMBLogger.e(BMBConstants.LOGTAG, "Could not play system notification sound: " + e.message)
            }

        }
    }

    fun stopNotificationSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            BMBLogger.e(BMBConstants.LOGTAG, "Error stopping notification sound: " + e.message)
        }
    }
}