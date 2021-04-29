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
package com.barclays.absa.banking.settings.ui.customisation

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.IAppShortcutsHandler
import com.barclays.absa.utils.AppShortcutsHandler
import kotlinx.android.synthetic.main.activity_customise_login_option.*

class CustomiseLoginOptionActivity : BaseActivity(R.layout.activity_customise_login_option) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (AppShortcutsHandler as IAppShortcutsHandler).setDefaultCachedUserShortcutList(this)

        customiseLoginScreenButton.setOnClickListener { navigateCustomiseLoginActivity() }
        defaultLoginButton.setOnClickListener { navigateDefaultLoginScreen() }
    }

    private fun navigateCustomiseLoginActivity() {
        startActivity(Intent(this, CustomiseLoginActivity::class.java))
        finish()
    }

    private fun navigateDefaultLoginScreen() {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { BMBApplication.getInstance().topMostActivity.finish() }
        startActivity(Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.do_banking_faster_default_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.do_banking_faster_default_message)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
        })
        finish()
    }

    override fun onBackPressed() {}
}