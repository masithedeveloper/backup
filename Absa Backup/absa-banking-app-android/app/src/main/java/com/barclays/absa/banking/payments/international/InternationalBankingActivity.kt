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

package com.barclays.absa.banking.payments.international

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.MenuFragment.Companion.SHOW_SWIFT_INTENT_KEY
import com.barclays.absa.banking.home.ui.MenuFragment.Companion.SHOW_WESTERN_UNION_INTENT_KEY
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_banking_activity.*

class InternationalBankingActivity : BaseActivity(R.layout.international_banking_activity) {

    companion object {
        val SWIFT_ALLOWED_CUSTOMER_CODES = arrayOf("00101", "00102", "00401", "00402")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.international_banking_activity_title))
        setupListeners()

        intent.extras?.apply {
            if (!getBoolean(SHOW_WESTERN_UNION_INTENT_KEY)) {
                westernUnionOptionActionButtonView.visibility = View.GONE
            }
            if (!getBoolean(SHOW_SWIFT_INTENT_KEY)) {
                swiftOptionActionButtonView.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        westernUnionOptionActionButtonView.setOnClickListener {
            if (featureSwitchingToggles.internationalPayments == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_international_payments))))
            } else {
                startActivity(Intent(this, InternationalPaymentsPaymentsActivity::class.java))
            }
        }

        swiftOptionActionButtonView.setOnClickListener {
            if (featureSwitchingToggles.swiftInternationalPayments == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_swift))))
            } else {
                if (BMBApplication.getApplicationLocale().language.equals(BMBApplication.AFRIKAANS_CODE, ignoreCase = true)) {
                    startActivity(buildLanguageInfoIntent())
                } else {
                    AnalyticsUtil.trackAction(SWIFT, "Swift_InternationalBankingScreen_SwiftTransactionsClicked")
                    startActivity(Intent(this, SwiftTransactionsActivity::class.java))
                }
            }
        }
    }

    private fun buildLanguageInfoIntent(): Intent? {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(SWIFT, "Swift_LanguageLimitationScreen_SluitButtonClicked")
            BMBApplication.getInstance().topMostActivity.finish()
            startActivity(Intent(this, SwiftTransactionsActivity::class.java))
        }
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(SWIFT, "Swift_LanguageLimitationScreen_GaanVoortButtonClicked")
            BMBApplication.getInstance().topMostActivity.finish()
        }
        return Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.continue_button)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.close)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.international_payment_result_please_note)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.international_payments_swift_english_only_notice)
            putExtra(GenericResultActivity.IS_GENERAL_ALERT, true)
        }
    }
}