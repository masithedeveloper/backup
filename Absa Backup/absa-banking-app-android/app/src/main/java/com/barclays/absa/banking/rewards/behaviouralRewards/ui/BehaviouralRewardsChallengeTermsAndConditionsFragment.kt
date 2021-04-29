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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.behavioural_rewards_challenge_terms_and_conditions_fragment.*

class BehaviouralRewardsChallengeTermsAndConditionsFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_challenge_terms_and_conditions_fragment) {

    companion object {
        const val BEHAVIOURAL_REWARDS_URL_ENGLISH = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/product-tc/terms-and-conditions-absa-advantage-eng.pdf"
        const val BEHAVIOURAL_REWARDS_URL_AFRIKAANS = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/product-tc/terms-and-conditions-absa-advantag-afr.pdf"
        const val PRIVACY_POLICY_URL = "https://www.absa.co.za/legal-and-compliance/privacy-statement/"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_challenge_terms_and_conditions_terms_and_conditions))
        showToolBar()

        arguments?.let {
            val navArgs = BehaviouralRewardsChallengeTermsAndConditionsFragmentArgs.fromBundle(it)

            if (!navArgs.behaviouralRewardsChallenge.active) {
                acceptTermsAndConditionsCheckBoxView.visibility = View.GONE
                gotItButton.visibility = View.GONE
            } else {
                gotItButton.setOnClickListener {
                    if (acceptTermsAndConditionsCheckBoxView.isChecked) {
                        navigate(BehaviouralRewardsChallengeTermsAndConditionsFragmentDirections.actionBehaviouralRewardsChallengeTermsAndConditionsFragmentToBehaviouralRewardsChallengeDetailsFragment(navArgs.behaviouralRewardsChallenge))
                    } else {
                        acceptTermsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.please_accept_terms_and_conditions))
                    }

                    trackAnalytics("TermsAndConditions_ContinueButtonClicked")
                }
            }
        }

        CommonUtils.makeTextClickable(context, R.string.behavioural_rewards_challenge_terms_and_conditions_full_terms, getString(R.string.behavioural_rewards_challenge_terms_and_conditions_terms_and_conditions),
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val activeUserProfileLanguage = ProfileManager.getInstance().activeUserProfile?.languageCode
                        if (BMBConstants.ENGLISH_LANGUAGE.toString().equals(activeUserProfileLanguage, ignoreCase = true)) {
                            PdfUtil.showPDFInApp(baseActivity, BEHAVIOURAL_REWARDS_URL_ENGLISH)
                        } else {
                            PdfUtil.showPDFInApp(baseActivity, BEHAVIOURAL_REWARDS_URL_AFRIKAANS)
                        }
                    }
                }, fullTermsTextView, R.color.pink)

        CommonUtils.makeTextClickable(context, R.string.behavioural_rewards_challenge_terms_and_conditions_privacy_policy, getString(R.string.behavioural_rewards_privacy_notice),
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val openUrl = Intent(Intent.ACTION_VIEW).setData(Uri.parse(PRIVACY_POLICY_URL))
                        startActivityIfAvailable(openUrl)
                    }
                }, privacyNoticeTextView, R.color.pink)

        acceptTermsAndConditionsCheckBoxView.setOnCheckedListener { acceptTermsAndConditionsCheckBoxView.clearError() }
    }
}