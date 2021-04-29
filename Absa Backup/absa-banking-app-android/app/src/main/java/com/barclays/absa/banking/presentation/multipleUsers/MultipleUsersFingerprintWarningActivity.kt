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
package com.barclays.absa.banking.presentation.multipleUsers

import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.biometricAuthentication.BiometricAuthenticationToggler.disableBiometrics
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.utils.UserSettingsManager
import kotlinx.android.synthetic.main.activity_add_users.*

class MultipleUsersFingerprintWarningActivity : BaseActivity(R.layout.activity_add_users) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        continueButton.setOnClickListener {
            disableBiometrics(this) {
                dismissProgressDialog()
                proceedToWelcomeScreen()
            }
        }
        cancelButton.setOnClickListener { finish() }
    }

    private fun proceedToWelcomeScreen() {
        getDeviceProfilingInteractor().notifyLogin()
        finish()
        UserSettingsManager.setFingerprintActive(false)
        startActivity(Intent(this, WelcomeActivity::class.java))
    }
}