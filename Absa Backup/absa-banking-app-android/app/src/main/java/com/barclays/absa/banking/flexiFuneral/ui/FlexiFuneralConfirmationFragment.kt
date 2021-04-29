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

package com.barclays.absa.banking.flexiFuneral.ui

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.ApplyForFlexiFuneralResponse
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DASHED_DATE_PATTERN
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.flexi_funeral_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toRandAmount
import styleguide.utils.extensions.toTitleCase

class FlexiFuneralConfirmationFragment : FlexiFuneralBaseFragment(R.layout.flexi_funeral_confirmation_fragment) {
    private lateinit var familyMemberAdapter: FlexiFuneralFamilyMemberRecyclerViewAdapter
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var applyForFlexiFuneralResponse = ApplyForFlexiFuneralResponse()

    companion object {
        const val ESTATE_LATE = "Estate_Late"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(activity) {
            override fun onSureCheckProcessed() {
                flexiFuneralViewModel.applyForFlexiFuneral(flexiFuneralViewModel.applyForFlexiFuneralData)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        attachObservers()
        initViews()
        setUpOnClickListeners()
        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_Confirmation_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        hostActivity.setToolBar(getString(R.string.flexi_funeral_confirmation))
        hostActivity.setStep(3)
    }

    private fun initViews() {
        setUpAdapter()
        totalMonthlyPremiumContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.totalPremium.toRandAmount())
        mainMemberContentAndLabelView.setLabelText(getString(R.string.flexi_funeral_main_member_cover_option, flexiFuneralViewModel.multipleDependentsDetails.coverAmount.toRandAmount(), flexiFuneralViewModel.multipleDependentsDetails.monthlyPremium.toRandAmount()))
        if (TRUE.equals(flexiFuneralViewModel.applyForFlexiFuneralData.isReplacement, true)) {
            terminatedFuneralPolicyContentAndLabelView.setContentText(getString(R.string.yes))
            nameOfInsurerContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.company)
            nameOfInsurerContentAndLabelView.visibility = View.VISIBLE
        } else {
            terminatedFuneralPolicyContentAndLabelView.setContentText(getString(R.string.no))
            nameOfInsurerContentAndLabelView.visibility = View.GONE
        }
        policyStartDateContentAndLabelView.setContentText(DateUtils.formatDate(flexiFuneralViewModel.addBeneficiaryStatus.value?.inceptionDate.toString(), DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
        debitDayContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.dayOfDebit)
        accountToDebitContentAndLabelView.setContentText(getString(R.string.flexi_funeral_account_description_and_account_number, flexiFuneralViewModel.applyForFlexiFuneralData.accountDescription, flexiFuneralViewModel.applyForFlexiFuneralData.accountNumber))
        employmentStatusContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.employmentStatus?.engCodeDescription)
        sourceOfFundsContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.sourceOfFund?.engCodeDescription)

        if (flexiFuneralViewModel.applyForFlexiFuneralData.occupation?.engCodeDescription.isNullOrEmpty()) {
            occupationStatusContentAndLabelView.visibility = View.GONE
        } else {
            occupationStatusContentAndLabelView.setContentText(flexiFuneralViewModel.applyForFlexiFuneralData.occupation?.engCodeDescription)
            occupationStatusContentAndLabelView.visibility = View.VISIBLE
        }

        if (flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.title.equals(ESTATE_LATE, true)) {
            beneficiaryDetailsContentAndLabelView.setContentText(getString(R.string.flexi_funeral_my_estate))
        } else {
            val dependentsRelationship = InsuranceBeneficiaryHelper.buildFamilyMembersRelationshipMappings(hostActivity).filterValues { it == flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.relationship }.keys.first()
            beneficiaryDetailsContentAndLabelView.setContentText("${flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.title} ${flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.firstName} ${flexiFuneralViewModel.flexiFuneralBeneficiaryDetails.surname} (${dependentsRelationship.toTitleCase()})")
        }
    }

    private fun setUpAdapter() {
        familyMemberAdapter = FlexiFuneralFamilyMemberRecyclerViewAdapter(flexiFuneralViewModel.familyMemberList)
        familyMemberRecyclerView.adapter = familyMemberAdapter
        if (flexiFuneralViewModel.familyMemberList.size > 0) {
            familyMemberRecyclerView.visibility = View.VISIBLE
        } else {
            familyMemberRecyclerView.visibility = View.GONE
        }
    }

    private fun attachObservers() {
        flexiFuneralViewModel.applyForFlexiFuneral = MutableLiveData()
        flexiFuneralViewModel.applyForFlexiFuneral.observe(viewLifecycleOwner, {

            sureCheckDelegate.processSureCheck(baseActivity, it) {
                dismissProgressDialog()
                applyForFlexiFuneralResponse = it
                if (it.transactionStatus.equals("Success", ignoreCase = true) && it.flexiFuneralPlan.policyNumber.isNotEmpty() && it.flexiFuneralPlan.commencementDate.isNotEmpty()) {
                    AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_Success_ScreenDisplayed")
                    showSuccessScreen()
                } else if (it.transactionMessage.equals(getString(R.string.exergy_timeout_message))) {
                    showTimeOutScreen()
                } else if (it.transactionStatus.equals("Failure", ignoreCase = true)) {
                    AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_Failure_ScreenDisplayed")
                    showFailureScreen()
                } else {
                    AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_SomethingWentWrong_ScreenDisplayed")
                    showErrorScreen()
                }
            }
        })
    }

    private fun setUpOnClickListeners() {
        makeTermsAndConditionClickable()

        continueButton.setOnClickListener {
            if (!termsAndConditionsCheckBoxView.isChecked) {
                termsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.flexi_funeral_terms_and_conditions_error))
            } else {
                flexiFuneralViewModel.applyForFlexiFuneral(flexiFuneralViewModel.applyForFlexiFuneralData)
            }
        }
    }

    private fun makeTermsAndConditionClickable() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (hostActivity.flexiFuneralData.termsAndConditionsPdfUrl.isNotEmpty()) {
                    AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_TermsAndConditionsPDF_ScreenDisplayed")
                    PdfUtil.showPDFInApp(baseActivity, flexiFuneralViewModel.flexiFuneralData.termsAndConditionsPdfUrl)
                }
            }
        }
        CommonUtils.makeTextClickable(activity, R.string.insurance_terms_and_conditions, getString(R.string.insurance_terms_and_conditions_clickable), clickableSpan, termsAndConditionsCheckBoxView.checkBoxTextView, R.color.graphite)
    }

    private fun showSuccessScreen() {
        hostActivity.hideToolbar()
        hostActivity.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.flexi_funeral_success_screen_title))
                .setDescription(getString(R.string.flexi_funeral_success_screen_message, applyForFlexiFuneralResponse.flexiFuneralPlan.policyNumber, DateUtils.formatDate(applyForFlexiFuneralResponse.flexiFuneralPlan.commencementDate, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN)))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.flexi_funeral_call_center_number))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }

        navigate(FlexiFuneralConfirmationFragmentDirections.actionFlexiFuneralConfirmationFragmentToFlexiFuneralGenericResultScreenFragment(resultScreenProperties))
    }

    private fun showFailureScreen() {
        hostActivity.hideToolbar()
        hostActivity.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.flexi_funeral_failure_screen_title))
                .setDescription(getString(R.string.flexi_funeral_failure_screen_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.flexi_funeral_call_center_number))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }

        navigate(FlexiFuneralConfirmationFragmentDirections.actionFlexiFuneralConfirmationFragmentToFlexiFuneralGenericResultScreenFragment(resultScreenProperties))
    }

    private fun showErrorScreen() {
        hostActivity.hideToolbar()
        hostActivity.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.flexi_funeral_something_went_wrong_error))
                .setDescription(getString(R.string.flexi_funeral_something_went_wrong_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        navigate(FlexiFuneralConfirmationFragmentDirections.actionFlexiFuneralConfirmationFragmentToFlexiFuneralGenericResultScreenFragment(resultScreenProperties))
    }

    private fun showTimeOutScreen() {
        hostActivity.hideToolbar()
        hostActivity.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.flexi_funeral_timeout_screen_title))
                .setDescription(getString(R.string.flexi_funeral_timeout_screen_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.flexi_funeral_call_center_number))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }

        navigate(FlexiFuneralConfirmationFragmentDirections.actionFlexiFuneralConfirmationFragmentToFlexiFuneralGenericResultScreenFragment(resultScreenProperties))
    }
}