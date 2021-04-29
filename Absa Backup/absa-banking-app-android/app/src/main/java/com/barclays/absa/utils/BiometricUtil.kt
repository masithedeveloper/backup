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

@file:Suppress("DEPRECATION")

package com.barclays.absa.utils

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.barclays.absa.banking.framework.app.BMBApplication

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
object BiometricUtil {

    fun noFingerPrintHardwareDetected(): Boolean {
        val fingerprintManager: FingerprintManager? = BMBApplication.getInstance().applicationContext.getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager
        val hardwareDetected = fingerprintManager?.isHardwareDetected ?: false
        return !hardwareDetected
    }

    fun noEnrolledFingerPrints(): Boolean {
        val fingerprintManager: FingerprintManager? = BMBApplication.getInstance().applicationContext.getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager
        val hasEnrolledFingerPrints = fingerprintManager?.hasEnrolledFingerprints() ?: false
        return !hasEnrolledFingerPrints
    }
}