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

package com.barclays.absa.banking.linking.ui

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.oneidentity.sdk.android.OneIdentity
import com.oneidentity.sdk.android.customization.ConfirmationScreenConfig
import com.oneidentity.sdk.android.customization.DocumentCaptureConfig
import com.oneidentity.sdk.android.customization.LivenessConfig
import com.oneidentity.sdk.android.exceptions.ExceptionUnsupportedSDK

object FacialBiometricUtil {

    fun startSDK(context: Context, reference: String) {
        try {
            val logLevel = if (!BuildConfig.PRD) Log.VERBOSE else Log.ERROR
            OneIdentity.setLogLevel(logLevel)
            val environment = if (BuildConfig.PRD || BuildConfig.PRD_BETA) OneIdentity.EnvConfig.prod else OneIdentity.EnvConfig.sta
            OneIdentity.initialize(BMBApplication.getInstance(), reference, environment, false, BuildConfig.appIdentifierKey)
            configureSDK(context)
        } catch (exceptionUnsupportedSDK: ExceptionUnsupportedSDK) {
            exceptionUnsupportedSDK.printStackTrace()
        }
    }

    private fun configureSDK(context: Context) {
        context.let {
            val iidentifiiBrown = ContextCompat.getColor(it, R.color.iidentifii_brown)
            val white = ContextCompat.getColor(it, R.color.white)
            val absaPink = ContextCompat.getColor(it, R.color.pink)

            val confirmationScreenConfig = ConfirmationScreenConfig()
                    .screenBackgroundColor(white)
                    .bannerBackgroundColor(white)
                    .confirmationTextColor(iidentifiiBrown)
                    .bannerTextColor(iidentifiiBrown)
                    .confirmButtonBackgroundColor(absaPink)
                    .scanAgainButtonBackgroundColor(absaPink)
                    .scanAgainButtonTextColor(absaPink)

            val livelinessConfig = LivenessConfig()
                    .prepareScreenBackgroundColor(white)
                    .prepareScreenSpinnerColor(iidentifiiBrown)
                    .prepareScreenTextColor(iidentifiiBrown)
                    .splashScreenBackgroundColor(white)
                    .splashScreenProgressbarColor(iidentifiiBrown)
                    .splashScreenProcessTextColor(iidentifiiBrown)

            val documentCaptureConfig = DocumentCaptureConfig().confirmationScreenConfig(confirmationScreenConfig)
            OneIdentity.getInstance().setConfig(livelinessConfig, documentCaptureConfig)
        }
    }
}