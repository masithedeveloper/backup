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
package com.barclays.absa.banking.manage.devices.linking

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.deviceLinking.ui.LinkingPasswordValidationActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.passcode.passcodeLogin.ForgotPasscodeActivity
import kotlinx.android.synthetic.main.activity_2fa_forgot_password.*

class ForgotPassword2faActivity : BaseActivity(R.layout.activity_2fa_forgot_password) {
    private var passwordLocked = false
    private var accLocked = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScreenName = BMBConstants.PROFILE_BLOCK_CONST
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PROFILE_BLOCK_CONST, BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST)

        intent.extras?.let {
            if (it.containsKey(LinkingPasswordValidationActivity.PASSWORD_LOCKED)) {
                passwordLocked = intent.getBooleanExtra(LinkingPasswordValidationActivity.PASSWORD_LOCKED, false)
            } else if (it.containsKey(AccountLoginActivity.ACCOUNT_LOCKED)) {
                accLocked = intent.getBooleanExtra(AccountLoginActivity.ACCOUNT_LOCKED, false)
            }
        }

        setToolBarBack(R.string.forgot_your_password)
        populateView()
    }

    private fun populateView() {
        if (accLocked) {
            setToolBarBack(R.string.login_forgot_passcode)
            titleCenteredTitleView.setTitle(getString(R.string.profile_block_title))
            contentDescriptionView.setDescription(getString(R.string.profile_block_sub_title_1))
            resetPasscodeButton.visibility = View.VISIBLE

            resetPasscodeButton.setOnClickListener {
                startActivity(Intent(this, ForgotPasscodeActivity::class.java))
            }
        }
        contactView.setContact(TelephoneUtil.getBankingAppSupportContact(this))
        contactView.setOnClickListener { TelephoneUtil.callMainSwitchBoard(this@ForgotPassword2faActivity) }
    }
}