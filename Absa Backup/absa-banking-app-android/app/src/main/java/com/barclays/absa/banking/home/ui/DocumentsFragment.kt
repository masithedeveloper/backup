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

package com.barclays.absa.banking.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.bankConfirmationLetter.ui.BankConfirmationLetterActivity
import com.barclays.absa.banking.bankConfirmationLetter.ui.BankConfirmationLetterActivity.Companion.BANK_CONFIRMATION_LETTER_ANALYTIC_TAG
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.taxCertificates.ui.TaxCertificatesActivity
import com.barclays.absa.banking.taxCertificates.ui.TaxCertificatesActivity.Companion.TAX_CERTIFICATE_ANALYTIC_TAG
import com.barclays.absa.banking.will.ui.WillActivity
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.documents_fragment.*

class DocumentsFragment : BaseFragment(R.layout.documents_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.documents_menu_item)

        val isOperator = AccessPrivileges.instance.isOperator

        if (!BuildConfig.TOGGLE_DEF_TAX_CERTIFICATE_ENABLED || featureSwitchingToggles.viewTaxCertificate == FeatureSwitchingStates.GONE.key || isOperator) {
            taxCertificatesActionButton.visibility = View.GONE
        }

        if (!BuildConfig.TOGGLE_DEF_BANK_CONFIRMATION_LETTER_ENABLED || featureSwitchingToggles.bankConfirmationLetter == FeatureSwitchingStates.GONE.key || isOperator) {
            bankConfirmationLetterActionButtonView.visibility = View.GONE
        }

        if (isBusinessAccount || isOperator || !BuildConfig.TOGGLE_DEF_WIMI_WILL_ENABLED || featureSwitchingToggles.wimiViewWills == FeatureSwitchingStates.GONE.key) {
            yourWillActionButtonView.visibility = View.GONE
        }

        yourWillActionButtonView.setOnClickListener {
            if (featureSwitchingToggles.wimiViewWills == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_view_your_will))))
            } else {
                startActivity(Intent(baseActivity, WillActivity::class.java))
            }
        }

        taxCertificatesActionButton.setOnClickListener {
            if (isBusinessAccount) {
                AnalyticsUtil.trackAction(TAX_CERTIFICATE_ANALYTIC_TAG, "BBTaxCertificate_MenuScreen_TaxCertificateOptionSelected")
            } else {
                AnalyticsUtil.trackAction(TAX_CERTIFICATE_ANALYTIC_TAG, "TaxCertificate_MenuScreen_TaxCertificateOptionSelected")
            }

            if (featureSwitchingToggles.viewTaxCertificate == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_tax_certificates))))
            } else {
                startActivity(Intent(baseActivity, TaxCertificatesActivity::class.java))
            }
        }

        bankConfirmationLetterActionButtonView.setOnClickListener {
            if (isBusinessAccount) {
                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BBBankConfirmationLetter_Menu_BankConfirmationLetterTapped")
            } else {
                AnalyticsUtil.trackAction(BANK_CONFIRMATION_LETTER_ANALYTIC_TAG, "BankConfirmationLetter_Menu_BankConfirmationLetterTapped")
            }

            if (featureSwitchingToggles.bankConfirmationLetter == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_tax_certificates))))
            } else {
                startActivity(Intent(baseActivity, BankConfirmationLetterActivity::class.java))
            }
        }
    }
}
