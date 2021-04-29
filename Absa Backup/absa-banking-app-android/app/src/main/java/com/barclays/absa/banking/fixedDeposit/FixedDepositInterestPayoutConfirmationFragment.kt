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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.fixed_deposit_interest_payout_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toTitleCase

class FixedDepositInterestPayoutConfirmationFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_interest_payout_confirmation_fragment) {

    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_confirm_payout_details)

        val payoutDetails = fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
            accountNumberPrimaryContentAndLabelView.setContentText(targetAccountNumber.toFormattedAccountNumber())
            bankSecondaryContentAndLabelView.setContentText(targetInstCode.toTitleCase())
            branchSecondaryContentAndLabelView.setContentText(targetBranchCode)
            accountTypeSecondaryContentAndLabelView.setContentText(targetAccountDescription.toTitleCase())
            paymentDaySecondaryContentAndLabelView.setContentText(termCapDay)
        }

        if (payoutDetails.targetAccountRef.isBlank()) {
            referenceSecondaryContentAndLabelView.visibility = View.GONE
        } else {
            referenceSecondaryContentAndLabelView.visibility = View.VISIBLE
            referenceSecondaryContentAndLabelView.setContentText(payoutDetails.targetAccountRef)
        }

        sureCheckDelegate = object : SureCheckDelegate(fixedDepositMaintenanceActivity) {
            override fun onSureCheckProcessed() {
                fixedDepositViewModel.updateAccountDetails()
            }
        }

        fixedDepositViewModel.updateAccountDetailsResponse.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(fixedDepositMaintenanceActivity, it) {
                dismissProgressDialog()
                if (it.transactionMessage.equals(BMBConstants.SUCCESS, true)) {
                    val successScreenProperties = buildSuccessResultScreenProperties(payoutDetails, it.product.interestPaymentInst?.trgAcc.toFormattedAccountNumber())
                    navigate(FixedDepositInterestPayoutConfirmationFragmentDirections.actionFixedDepositInterestPayoutConfirmationFragmentToGenericResultScreenFragment(successScreenProperties))
                    hideToolBar()
                } else {
                    fixedDepositViewModel.notifyFailure(it)
                }
            }
        })

        fixedDepositViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            GenericResultScreenFragment.setPrimaryButtonOnClick {
                AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "UpdatePayoutDetails_FailureScreen_DoneButtonClicked")
                navigateToHomeScreenWithoutReloadingAccounts()
            }
            val failureScreenProperties = if (it.transactionMessage.toString().contains("INVALID EXTERNAL ACCOUNT", true) || it.transactionMessage.toString().contains("NO CIF KEY FOR TARGET ACCOUNT", true)) {
                buildInvalidNumberFailureScreenProperties()
            } else {
                buildFailureResultScreenProperties()
            }
            navigate(FixedDepositInterestPayoutConfirmationFragmentDirections.actionFixedDepositInterestPayoutConfirmationFragmentToGenericResultScreenFragment(failureScreenProperties))
            hideToolBar()
        })

        confirmButton.setOnClickListener { fixedDepositViewModel.updateAccountDetails() }
    }

    private fun buildSuccessResultScreenProperties(payoutDetails: FixedDepositPayoutDetailsData, accountNumber: String): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "UpdatePayoutDetails_SuccessScreen_DoneButtonClicked")
            startActivity(IntentFactory.getFixedDepositHubActivity(fixedDepositMaintenanceActivity, fixedDepositViewModel.accountObject))
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.paymentSuccess)
                .setTitle(getString(R.string.fixed_deposit_update_successful))
                .setDescription(getString(R.string.fixed_deposit_payout_details_success_message, payoutDetails.targetAccountDescription.toTitleCase(), accountNumber))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    private fun buildFailureResultScreenProperties(): GenericResultScreenProperties {
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.error))
                .setDescription(getString(R.string.fixed_deposit_payout_details_error_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    private fun buildInvalidNumberFailureScreenProperties(): GenericResultScreenProperties {
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.fixed_deposit_invalid_account_number))
                .setDescription(getString(R.string.fixed_deposit_invalid_account_number_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    override fun onDestroyView() {
        fixedDepositViewModel.updateAccountDetailsResponse.removeObservers(viewLifecycleOwner)
        fixedDepositViewModel.failureResponse.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}