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
package com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments

import android.os.Bundle
import com.barclays.absa.banking.biometric.BiometricHelper
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity
import com.barclays.absa.utils.ProfileManager

abstract class UserFragment : BaseFragment() {
    companion object {
        const val PAGE_NUMBER = "PAGE_NUMBER"
        private const val MAX_FINGERPRINT_RETRIES = 3
    }

    protected var position = 0

    protected var useFingerprint = false
    protected var biometricHelper: BiometricHelper? = null
    protected var fingerPrintRetries = MAX_FINGERPRINT_RETRIES
    protected val profileManager: ProfileManager = ProfileManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt(PAGE_NUMBER) ?: 0
    }

    fun deactivateFingerprintSensor() {
        useFingerprint = false
        biometricHelper?.cancelPrompt()
    }

    abstract fun onPageScrollSettled()
    protected fun getSimplifiedLoginActivity(): SimplifiedLoginActivity = activity as SimplifiedLoginActivity
}