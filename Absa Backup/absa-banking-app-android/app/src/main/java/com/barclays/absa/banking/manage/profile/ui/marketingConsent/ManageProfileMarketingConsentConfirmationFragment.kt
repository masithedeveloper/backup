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

package com.barclays.absa.banking.manage.profile.ui.marketingConsent

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_marketing_consent_overview_fragment.*
import kotlinx.android.synthetic.main.manage_profile_other_financial_details_confirmation_fragment.*

class ManageProfileMarketingConsentConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_marketing_consent_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateMarketingConsent() }, 250)
            }

            override fun onSureCheckCancelled() {
                dismissProgressDialog()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_confirm_detail_changes_toolbar_title)
        initData()
        initObservers()
        setUpOnClickListener()
    }

    private fun initData() {
        val marketingConsentDetailsToUpdate = manageProfileViewModel.marketingConsentDetailsToUpdate
        if (YES.equals(marketingConsentDetailsToUpdate.nonCreditIndicator, true)) {
            electronicPreferredCommunicationMethodContentAndLabelView.setContentText(preferredCommunicationMethod(marketingConsentDetailsToUpdate.nonCreditEmailIndicator, marketingConsentDetailsToUpdate.nonCreditSmsIndicator, marketingConsentDetailsToUpdate.nonCreditAutoVoiceIndicator, ""))
        } else {
            electronicPreferredCommunicationMethodContentAndLabelView.setContentText(getString(R.string.manage_profile_marketing_consent_preferred_communication_method_none))
        }

        if (YES.equals(marketingConsentDetailsToUpdate.creditIndicator, true)) {
            creditPreferredCommunicationMethodContentAndLabelView.setContentText(preferredCommunicationMethod(marketingConsentDetailsToUpdate.creditEmailIndicator, marketingConsentDetailsToUpdate.creditSmsIndicator, "", marketingConsentDetailsToUpdate.creditTeleIndicator))
        } else {
            creditPreferredCommunicationMethodContentAndLabelView.setContentText(getString(R.string.manage_profile_marketing_consent_preferred_communication_method_none))
        }
    }

    private fun preferredCommunicationMethod(email: String, sms: String, voiceRecording: String, telephone: String): String {
        val stringBuilder = StringBuilder()
        if (YES.equals(email, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_email)).appendLine()
        }
        if (YES.equals(sms, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_sms)).appendLine()
        }
        if (YES.equals(voiceRecording, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_voice_recording)).appendLine()
        }
        if (YES.equals(telephone, true)) {
            stringBuilder.append(getString(R.string.manage_profile_marketing_consent_telephone)).appendLine()
        }
        return stringBuilder.toString().substringBeforeLast('\n')
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && SUCCESS.equals(customerInformation.transactionStatus, ignoreCase = true) -> navigate(ManageProfileMarketingConsentConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_MarketingConsentDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, ignoreCase = true) -> navigate(ManageProfileMarketingConsentConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_MarketingConsentDetailsUpdateFailureScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateMarketingConsent() }
            }
        })
    }

    private fun setUpOnClickListener() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ConfirmMarketingConsentDetailsChangesScreen_SaveButtonClicked")
            manageProfileViewModel.updateMarketingConsent()
        }
    }
}