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
import com.barclays.absa.banking.fixedDeposit.FixedDepositReinvestInstructionsFragment.ReinvestInstruction
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.fixed_deposit_reinvest_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toTitleCase

class FixedDepositReinvestConfirmationFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_reinvest_confirmation_fragment) {

    companion object {
        private const val INTEREST_RATE = "interestRate"
        private const val CAP_FREQUENCY = "capFrequency"
    }

    private lateinit var analyticsTag: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_confirm_instruction)

        val interestRate = arguments?.getString(INTEREST_RATE, "") ?: ""
        val capFrequency = arguments?.getString(CAP_FREQUENCY, "") ?: ""

        interestRateSecondaryContentAndLabelView.setContentText("${interestRate}%")
        interestPaymentFrequencySecondaryContentAndLabelView.setContentText(capFrequency.toTitleCase())
        accountNamePrimaryContentAndLabelView.setContentText(fixedDepositViewModel.accountObject.description)

        analyticsTag = if (fixedDepositViewModel.reinvestInstruction == ReinvestInstruction.AUTO_REINVEST) {
            setupDataForAutoReinvest()
            "AutoReinvest"
        } else {
            setupDataForCustomReinvest()
            "CustomInstruction"
        }
        setupObservers()
    }

    private fun setupObservers() {
        val sureCheckDelegate = object : SureCheckDelegate(fixedDepositMaintenanceActivity) {
            override fun onSureCheckProcessed() {
                if (fixedDepositViewModel.reinvestInstruction == ReinvestInstruction.AUTO_REINVEST) {
                    fixedDepositViewModel.updateAccountDetails()
                } else {
                    fixedDepositViewModel.createRenewalInstruction()
                }
            }
        }
        fixedDepositViewModel.updateAccountDetailsResponse.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(fixedDepositMaintenanceActivity, it) {
                dismissProgressDialog()
                val resultScreenProperties = if (it.transactionMessage.equals(BMBConstants.SUCCESS, true)) {
                    buildSuccessResultScreenProperties(DateUtils.getTodaysDate(DATE_DISPLAY_PATTERN))
                } else {
                    buildFailureResultScreenProperties()
                }
                fixedDepositMaintenanceActivity.hideProgressIndicatorAndToolbar()
                navigate(FixedDepositReinvestConfirmationFragmentDirections.actionFixedDepositReinvestConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
            }
        })

        fixedDepositViewModel.createRenewalInstructionResponse.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(fixedDepositMaintenanceActivity, it) {
                dismissProgressDialog()
                if (it.transactionMessage.equals(BMBConstants.SUCCESS, true)) {
                    fixedDepositMaintenanceActivity.hideProgressIndicatorAndToolbar()
                    val successProperties = buildSuccessResultScreenProperties(DateUtils.formatDateMonth(fixedDepositViewModel.confirmRenewalInstructionResponse.value?.renewalInstruction?.startDate))
                    navigate(FixedDepositReinvestConfirmationFragmentDirections.actionFixedDepositReinvestConfirmationFragmentToGenericResultScreenFragment(successProperties))
                } else {
                    fixedDepositViewModel.notifyFailure(it)
                }
            }
        })

        fixedDepositViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            fixedDepositMaintenanceActivity.hideProgressIndicatorAndToolbar()
            navigate(FixedDepositReinvestConfirmationFragmentDirections.actionFixedDepositReinvestConfirmationFragmentToGenericResultScreenFragment(buildFailureResultScreenProperties()))
        })
    }

    private fun setupDataForAutoReinvest() {
        fixedDepositViewModel.accountDetailsResponse.value?.fixedDeposit?.let {
            capitalInvestmentPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(it.subAccountBalance))
            investmentTermSecondaryContentAndLabelView.setContentText("${it.term} ${getString(R.string.fixed_deposit_days)}")
            interestPaymentDaySecondaryContentAndLabelView.setContentText(it.nextCapDate.takeLast(2))
        }
        startDateSecondaryContentAndLabelView.visibility = View.GONE
        maturityDateSecondaryContentAndLabelView.visibility = View.GONE
        confirmButton.setOnClickListener { fixedDepositViewModel.updateAccountDetails() }
    }

    private fun setupDataForCustomReinvest() {
        fixedDepositMaintenanceActivity.setProgressStep(4)
        fixedDepositViewModel.confirmRenewalInstructionResponse.value?.let {
            capitalInvestmentPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(it.renewalInstruction.amount))
            investmentTermSecondaryContentAndLabelView.setContentText(fixedDepositViewModel.investmentTerm.value)
            interestPaymentDaySecondaryContentAndLabelView.visibility = if (it.renewalInstruction.interestCapFreq == "0") View.GONE else View.VISIBLE
            interestPaymentDaySecondaryContentAndLabelView.setContentText(it.renewalInstruction.capDay)
            maturityDateSecondaryContentAndLabelView.setContentText(DateUtils.formatDateMonth(it.renewalInstruction.endDate))
        }
        startDateSecondaryContentAndLabelView.setContentText(DateUtils.getTodaysDate(DATE_DISPLAY_PATTERN))
        confirmButton.setOnClickListener { fixedDepositViewModel.createRenewalInstruction() }
    }

    private fun buildSuccessResultScreenProperties(startDate: String): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "${analyticsTag}_SuccessScreen_DoneButtonClicked")
            loadAccountsAndGoHome()
        }
        val description = if (fixedDepositViewModel.reinvestInstruction == ReinvestInstruction.AUTO_REINVEST) {
            getString(R.string.fixed_deposit_auto_reinvestment_success_message)
        } else {
            getString(R.string.fixed_deposit_reinvestment_success_message, startDate)
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.fixed_deposit_reinvestment_confirmed))
                .setDescription(description)
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    private fun buildFailureResultScreenProperties(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "${analyticsTag}_FailureScreen_DoneButtonClicked")
            loadAccountsAndGoHome()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.error))
                .setDescription(getString(R.string.fixed_deposit_reinvest_error_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    override fun onDestroyView() {
        fixedDepositViewModel.apply {
            updateAccountDetailsResponse.removeObservers(viewLifecycleOwner)
            createRenewalInstructionResponse.removeObservers(viewLifecycleOwner)
            failureResponse.removeObservers(viewLifecycleOwner)
        }
        super.onDestroyView()
    }
}