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
package com.barclays.absa.banking.passcode

import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.framework.BaseActivity
import kotlinx.android.synthetic.main.passcode_locked_activity.*

class PasscodeLockedActivity : BaseActivity(R.layout.passcode_locked_activity) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetPasscodeButton.setOnClickListener {
            with(appCacheService) {
                clear()
                setShouldRevertToOldLinkingFlow(true)
                setPasscodeResetFlow(true)
            }

            navigateAccountLoginActivity()
        }
    }

    private fun navigateAccountLoginActivity() = startActivity(Intent(this, AccountLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
}