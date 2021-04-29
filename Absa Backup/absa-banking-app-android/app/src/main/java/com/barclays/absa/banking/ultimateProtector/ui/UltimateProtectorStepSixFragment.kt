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

package com.barclays.absa.banking.ultimateProtector.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepSixFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType
import com.barclays.absa.banking.ultimateProtector.services.dto.LifeCoverApplicationResult
import com.barclays.absa.banking.ultimateProtector.services.dto.LifeCoverInfo
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.ultimate_protector_step_six_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.removeSpaceAfterForwardSlash
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCaseSplit
import java.util.*

class UltimateProtectorStepSixFragment : AbsaBaseFragment<UltimateProtectorStepSixFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_six_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.ultimate_protector_confirmation))
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, 1)
        hostActivity.setStep(6)

        val ultimateProtectorInfo = ultimateProtectorViewModel.ultimateProtectorInfo
        val accountInfo = ultimateProtectorInfo.accountInfo
        val sourceOfFunds = ultimateProtectorInfo.sourceOfFund
        val employmentStatus = ultimateProtectorInfo.employmentStatus
        val occupation = ultimateProtectorInfo.occupation

        binding.apply {
            coverAmountSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRandNoDecimal(ultimateProtectorInfo.coverAmount))
            premiumSecondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(ultimateProtectorInfo.monthlyPremium))
            premiumSecondaryContentAndLabelView.setLabelText(getString(R.string.total_premium, TextFormatUtils.formatBasicAmountAsRand(ultimateProtectorInfo.serviceFee)))
            debitDaySecondaryContentAndLabelView.setContentText(ultimateProtectorInfo.dayOfDebit)
            frequencySecondaryContentAndLabelView.setContentText(getString(R.string.monthly))
            policyStartDateSecondaryContentAndLabelView.setContentText(DateUtils.format(calendar.time, DateUtils.DATE_DISPLAY_PATTERN))
            accountToDebitSecondaryContentAndLabelView.setContentText(accountInfo?.accountDescription + " - " + accountInfo?.accountNumber)
            sourceOfFundsSecondaryContentAndLabelView.setContentText(sourceOfFunds?.defaultLabel?.toLowerCase().toTitleCaseSplit().removeSpaceAfterForwardSlash())
            employmentStatusSecondaryContentAndLabelView.setContentText(employmentStatus?.defaultLabel)
            occupationSecondaryContentAndLabelView.setContentText(occupation?.defaultLabel)
        }
        occupationSecondaryContentAndLabelView.visibility = if (occupation?.defaultLabel.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (ultimateProtectorInfo.payTo.equals("myEstate", true)) {
            binding.beneficiaryDetailsSecondaryContentAndLabelView.setContentText(getString(R.string.my_estate))
        } else {
            val beneficiaryInfo = ultimateProtectorInfo.beneficiaryInfo
            beneficiaryInfo?.let {
                binding.beneficiaryDetailsSecondaryContentAndLabelView.setContentText(it.title?.description + " " + it.firstName + " " + it.surname)

                if (binding.relationshipSecondaryContentAndLabelView.visibility == View.GONE) {
                    binding.relationshipSecondaryContentAndLabelView.visibility = View.VISIBLE
                    binding.relationshipSecondaryContentAndLabelView.setContentText(it.relationship?.description)
                }

                if (binding.dateOfBirthSecondaryContentAndLabelView.visibility == View.GONE) {
                    binding.dateOfBirthSecondaryContentAndLabelView.visibility = View.VISIBLE
                    binding.dateOfBirthSecondaryContentAndLabelView.setContentText(DateUtils.formatDate(it.dateOfBirth, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN))
                }
            }
        }

        ultimateProtectorInfo.isStepSixDeclarationAccepted?.let {
            binding.declarationCheckBoxView.isChecked = it
        }

        ultimateProtectorInfo.isStepSixTermsAccepted?.let {
            binding.termsAndConditionsCheckBoxView.isChecked = it
        }
        attachViewCallbackHandlers(ultimateProtectorInfo)
        handleSurecheck()

        CommonUtils.makeTextClickable(hostActivity as Context, R.string.terms, getString(R.string.ultimate_protector_terms_and_conditions), object : ClickableSpan() {
            override fun onClick(view: View) {
                PdfUtil.showPDFInApp(hostActivity, ultimateProtectorInfo.termsAndConditionPdfUrl)
            }
        }, binding.termsAndConditionsCheckBoxView.checkBoxTextView)

        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step6")
    }

    private fun attachViewCallbackHandlers(lifeCoverInfo: LifeCoverInfo) {
        binding.declarationCheckBoxView.setOnCheckedListener {
            lifeCoverInfo.isStepSixDeclarationAccepted = it
        }

        binding.termsAndConditionsCheckBoxView.setOnCheckedListener {
            lifeCoverInfo.isStepSixTermsAccepted = it
        }

        binding.stepSixConfirmationButton.setOnClickListener {
            if (!binding.declarationCheckBoxView.isChecked) {
                binding.declarationCheckBoxView.setErrorMessage(getString(R.string.accept_declaration_error))
            } else if (!binding.termsAndConditionsCheckBoxView.isChecked) {
                binding.termsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.plz_accept_conditions))
            } else {
                ultimateProtectorViewModel.lifeCoverApplicationLiveData.observe(this, {
                    dismissProgressDialog()
                    it?.let { lifeCoverApplicationResult ->
                        sureCheckDelegate.processSureCheck(hostActivity, it) {
                            if (lifeCoverApplicationResult.transactionStatus.equals(BMBConstants.SUCCESS, true)) {
                                launchSuccessScreen(lifeCoverApplicationResult)
                            } else {
                                launchFailureScreen()
                            }
                        }
                    }
                })
                ultimateProtectorViewModel.applyForLifeCover(CallType.SURECHECKV2_REQUIRED)
            }
        }
    }

    private fun handleSurecheck() {
        val handler = Handler(Looper.getMainLooper())
        sureCheckDelegate = object : SureCheckDelegate(activity) {
            override fun onSureCheckProcessed() {
                handler.postDelayed({
                    ultimateProtectorViewModel.applyForLifeCover(CallType.SURECHECKV2_PASSED)
                }, 250)
            }

            override fun onSureCheckFailed() {
                launchFailureScreen()
            }
        }
    }

    private fun launchSuccessScreen(lifeCoverApplicationResult: LifeCoverApplicationResult) {
        hostActivity.hideProgressIndicator()
        hostActivity.hideToolbar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.application_approved).toSentenceCase())
                .setDescription(getString(R.string.ultimate_protector_success_message, lifeCoverApplicationResult.policyNumber, lifeCoverApplicationResult.policyStartDate))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.lifecover_contact))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }
        navigate(UltimateProtectorStepSixFragmentDirections.actionUltimateProtectorStepSixFragmentToGenericResultScreenFragment(resultScreenProperties))
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Success")
    }

    private fun launchFailureScreen() {
        hostActivity.hideProgressIndicator()
        hostActivity.hideToolbar()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.ultimate_protector_unsuccessful))
                .setDescription(getString(R.string.ultimate_protector_failure_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.lifecover_contact))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        navigate(UltimateProtectorStepSixFragmentDirections.actionUltimateProtectorStepSixFragmentToGenericResultScreenFragment(resultScreenProperties))
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Unsuccess")
    }

}