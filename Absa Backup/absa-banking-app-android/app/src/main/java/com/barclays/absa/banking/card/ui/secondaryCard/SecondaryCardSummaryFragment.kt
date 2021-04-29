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
package com.barclays.absa.banking.card.ui.secondaryCard

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardSummaryFragmentDirections.actionSecondaryCardSummaryFragmentToGenericResultScreenFragment
import com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard.dto.UpdateSecondaryCardMandateRequest
import com.barclays.absa.banking.express.sureCheck.requestSecurityNotification.RequestSecurityNotificationViewModel
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler
import com.barclays.absa.banking.sureCheck.ProcessSureCheck
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.barclays.absa.utils.DateUtils.TWENTY_FOUR_HOUR_TIME_PATTERN
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.secondary_card_summary_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber

class SecondaryCardSummaryFragment : SecondaryCardBaseFragment(R.layout.secondary_card_summary_fragment) {

    private lateinit var requestVerificationViewModel: RequestSecurityNotificationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestVerificationViewModel = viewModel()
        ExpressSureCheckHandler.processSureCheck = object : ProcessSureCheck {
            override fun onSureCheckProcessed() {
                updateSecondaryCardMandates()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.secondary_card_access_title)
        populateViews()
        nextButton.setOnClickListener {
            tagSecondaryCardStateChange()
            updateSecondaryCardMandates()
        }
    }

    private fun updateSecondaryCardMandates() {
        val update = UpdateSecondaryCardMandateRequest().apply {
            primaryPlastic = secondaryViewModel.secondaryCardMandates.primaryPlastic
            secondaryPlastic = secondaryViewModel.secondaryCardMandates.secondaryPlastic
            secondaryTenantMandate = secondaryViewModel.secondaryCardMandates.secondaryTenantMandate
            secondaryCardUpdateMandateDetailsList = secondaryViewModel.buildSecondaryCardRequest()
            val sureCheckAdditionalParameters = HashMap<String, Any>()
            sureCheckAdditionalParameters["secondaryCardsUpdateList"] = secondaryViewModel.buildSureCheckSecondaryCardRequest()
            additionalSureCheckParameters = sureCheckAdditionalParameters
        }

        secondaryCardUpdateMandateViewModel.updateSecondaryCardMandates(update)
        secondaryCardUpdateMandateViewModel.updateSecondaryCardMandateLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigateToSuccessfulResultScreen()
        })

        secondaryCardUpdateMandateViewModel.failureLiveData.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            navigateToFailureResultScreen(it.toString())
        }
    }

    private fun tagSecondaryCardStateChange() {
        if (secondaryViewModel.buildSecondaryCardSummaryList().any { secondaryCards -> secondaryCards.additionalTenantMandate == SECONDARY_CARD_TENANT_MANDATE_DEACTIVATED }) {
            AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_EnableAccessScreen_MandateCheckBoxUnchecked")
        }

        if (secondaryViewModel.buildSecondaryCardSummaryList().any { secondaryCards -> secondaryCards.additionalTenantMandate == SECONDARY_CARD_TENANT_MANDATE_ACTIVE }) {
            AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_EnableAccessScreen_MandateCheckBoxChecked")
        }
    }

    private fun navigateToSuccessfulResultScreen() {
        AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_SuccessScreen_ScreenDisplayed")
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.secondary_card_success_title))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        navigate(actionSecondaryCardSummaryFragmentToGenericResultScreenFragment(resultScreenProperties, true))
    }

    private fun navigateToFailureResultScreen(failureMessage: String) {
        AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_ErrorScreen_ScreenDisplayed")
        hideToolBar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.secondary_card_updated_failed))
                .setDescription(failureMessage)
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        navigate(actionSecondaryCardSummaryFragmentToGenericResultScreenFragment(resultScreenProperties, true))
    }

    private fun populateViews() {
        primaryCardNumberContentAndLabelView.setContentText(secondaryViewModel.secondaryCardMandates.primaryPlastic.toFormattedAccountNumber())
        dateContentAndLabelView.setContentText(DateUtils.getTodaysDate(DATE_DISPLAY_PATTERN))
        timeContentAndLabelView.setContentText(DateUtils.getTodaysDate(TWENTY_FOUR_HOUR_TIME_PATTERN))
        secondaryCardRecyclerView.addItemDecoration(dividerItemDecoration())
        secondaryCardRecyclerView.adapter = SecondaryCardSummaryAdapter(secondaryViewModel.buildSecondaryCardSummaryList())
    }

    private fun dividerItemDecoration(): DividerItemDecoration {
        val itemArray = requireContext().obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        val divider = itemArray.getDrawable(0)
        val inset = resources.getDimensionPixelSize(R.dimen.medium_space)
        val insetDivider = InsetDrawable(divider, inset, 0, inset, 0)
        itemArray.recycle()

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        return itemDecoration
    }
}