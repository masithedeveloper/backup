/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.utils.key

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi

class UserAuthKeySpecGenerator(private val context: Context, private val blockMode: String, private val padding: String) : KeySpecGenerator {

    companion object {
        private const val VALIDITY_DURATION_SECONDS = -1
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun generate(keyName: String): KeyGenParameterSpec {
        val keyGenParameterSpecBuilder = KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(blockMode)
                .setEncryptionPaddings(padding)
                .setUserAuthenticationValidityDurationSeconds(VALIDITY_DURATION_SECONDS)
                .setUserAuthenticationRequired(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            keyGenParameterSpecBuilder.setInvalidatedByBiometricEnrollment(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            keyGenParameterSpecBuilder.setUnlockedDeviceRequired(true)
            if (hasStrongBox(context)) {
                keyGenParameterSpecBuilder.setIsStrongBoxBacked(true)
            }
        }
        return keyGenParameterSpecBuilder.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun hasStrongBox(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
}