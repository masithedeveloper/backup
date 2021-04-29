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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.presentation.multipleUsers.MultipleUsersFingerprintWarningActivity
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.UserSettingsManager

class NewUserFragment : UserFragment() {

    companion object {
        fun newInstance(pageNumber: Int): NewUserFragment = NewUserFragment().apply {
            arguments = Bundle().apply {
                putInt(PAGE_NUMBER, pageNumber)
            }
        }
    }

    override fun onPageScrollSettled() = deactivateFingerprintSensor()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_multiple_user_new_user_view, container, false).apply {
        findViewById<ImageView>(R.id.addNewUserImageView).setOnClickListener { navigateOnNewUserClicked() }
        findViewById<Button>(R.id.addNewUserButton).setOnClickListener { navigateOnNewUserClicked() }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun navigateOnNewUserClicked() {
        if (ProfileManager.getInstance().profileCount == 1 && UserSettingsManager.isFingerprintActive()) {
            navigateMultipleUsersFingerprintWarningActivity()
        } else {
            navigateWelcomeActivity()
        }
    }

    private fun navigateWelcomeActivity() = startActivity(Intent(activity, WelcomeActivity::class.java).apply {
        putExtra(WelcomeActivity.SKIP_HELLO_CONFIG, true)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    })

    private fun navigateMultipleUsersFingerprintWarningActivity() = startActivity(Intent(activity, MultipleUsersFingerprintWarningActivity::class.java))
}