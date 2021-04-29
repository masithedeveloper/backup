/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.presentation.help

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.recognition.ui.BranchRecognitionActivity
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.feedback_activity.*

class FeedbackActivity : BaseActivity(R.layout.feedback_activity) {

    private lateinit var emailAddress: String
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.title_activity_feedback_rebuild)
        emailAddress = if (CommonUtils.isPrivateBanker()) "privatebanking@absa.co.za" else "bankingapp@absa.co.za"
        message = StringBuilder()
                .append("Hello app team,\n\n")
                .append("<Start typing here...>\n\n\n\n")
                .append("Version info:\n")
                .append("App version: ${BuildConfig.VERSION_NAME}\n")
                .append("Phone model: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                .append("OS Version: ${Build.VERSION.RELEASE}")
                .toString()

        checkQrFeature()
        likeSomethingActionButtonView.setOnClickListener { openMail(getString(R.string.feedback_like_app)) }
        suggestionActionButtonView.setOnClickListener { openMail(getString(R.string.feedback_suggestion)) }
        technicalIssuesActionButtonView.setOnClickListener { openMail(getString(R.string.feedback_technical_issue)) }
        otherActionButtonView.setOnClickListener { openMail(getString(R.string.feedback_other)) }
        someoneHelpedMeActionButtonView.setOnClickListener { launchQrFeedbackFlow() }

        if (!BMBApplication.getInstance().userLoggedInStatus || ProfileManager.getInstance().activeUserProfile?.userId == null) {
            someoneHelpedMeActionButtonView.visibility = View.GONE
        }
    }

    private fun checkQrFeature() {
        if (featureSwitchingToggles.branchQRScanning == FeatureSwitchingStates.GONE.key) {
            someoneHelpedMeActionButtonView.visibility = View.GONE
        }
    }

    private fun openMail(subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val hasEmailClient = startActivityIfAvailable(Intent.createChooser(intent, "Send mail..."))
        if (!hasEmailClient) {
            toastShort("There are no email clients installed.")
        }
    }

    private fun launchQrFeedbackFlow() {
        if (featureSwitchingToggles.branchQRScanning == FeatureSwitchingStates.DISABLED.key) {
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_branch_qr_scan))))
        } else {
            startActivityIfAvailable(Intent(this, BranchRecognitionActivity::class.java))
        }
    }
}