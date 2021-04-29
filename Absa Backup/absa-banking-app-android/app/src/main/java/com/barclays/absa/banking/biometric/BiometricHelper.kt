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
package com.barclays.absa.banking.biometric

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import javax.crypto.Cipher

@TargetApi(Build.VERSION_CODES.M)
class BiometricHelper private constructor(val callback: Callback) {

    constructor(activity: AppCompatActivity, callback: Callback) : this(callback) {
        context = activity
        biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(context), onAuthenticationCallback())
    }

    constructor(fragment: Fragment, callback: Callback) : this(callback) {
        context = fragment.requireContext()
        biometricPrompt = BiometricPrompt(fragment, ContextCompat.getMainExecutor(context), onAuthenticationCallback())
    }

    private lateinit var context: Context
    private lateinit var biometricPrompt: BiometricPrompt

    private fun onAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            callback.onError(errorCode)
        }
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            callback.onAuthenticated(result.cryptoObject)
        }
        override fun onAuthenticationFailed() {
            callback.onAuthenticationFailed()
        }
    }

    fun authenticate(cipher: Cipher) {
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        biometricPrompt.authenticate(getBiometricPromptInfo(), cryptoObject)
    }

    fun authenticate(cipher: Cipher, biometricPromptInfo: PromptInfo) {
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        biometricPrompt.authenticate(biometricPromptInfo, cryptoObject)
    }

    fun cancelPrompt() {
        biometricPrompt.cancelAuthentication()
    }

    private fun getBiometricPromptInfo(): PromptInfo {
        return PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_login))
                .setSubtitle(context.getString(R.string.fingerprint_login))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build()
    }

    interface Callback {
        fun onAuthenticated(cryptoObject: BiometricPrompt.CryptoObject?)
        fun onError(errorCode: Int)
        fun onAuthenticationFailed()
    }
}