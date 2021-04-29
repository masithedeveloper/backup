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
package com.barclays.absa.banking.lawForYou.ui

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.law_for_you_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toRandAmount
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class LawForYouConfirmationFragment : LawForYouBaseFragment(R.layout.law_for_you_confirmation_fragment) {

    private companion object {
        private const val CALL_CENTRE_NUMBER = "086 166 2529"
    }

    lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.claim_confirmation)

        lawForYouActivity.setProgressStep(4)

        continueButton.setOnClickListener {
            if (iAgreePolicyDetailsCheckBox.isChecked) {
                lawForYouViewModel.submitLawForYouApplication()
            } else {
                iAgreePolicyDetailsCheckBox.setErrorMessage(getString(R.string.please_accept_terms_and_conditions))
            }
        }

        CommonUtils.makeTextClickable(context, R.string.law_for_you_i_agree_to_terms,
                R.string.law_for_you_terms_and_conditions, iAgreePolicyDetailsCheckBox.checkBoxTextView,
                R.color.graphite, object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction("Law For You", "LawForYou_TermsAndConditionsPDF_ScreenDisplayed")
                PdfUtil.showPDFInApp(lawForYouActivity, lawForYouViewModel.applyLawForYou.termsAndConditionsPdfUrl)
            }
        })

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                lawForYouViewModel.submitLawForYouApplication()
            }
        }

        lawForYouViewModel.applyLawForYouResponseMutableLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            sureCheckDelegate.processSureCheck(lawForYouActivity, it) {
                if (it.transactionStatus.equals(BMBConstants.SUCCESS, true)) {
                    val coverOption = lawForYouViewModel.selectedCoverOption.cover.toTitleCase()
                    val coverStartDate = DateUtils.formatDate(lawForYouViewModel.lawForYouDetails.inceptionDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN)
                    navigate(LawForYouConfirmationFragmentDirections.actionLawForYouConfirmationFragmentToGenericResultScreenFragment(getSuccessResultScreenProperties(coverOption, it.productReferenceNumber, coverStartDate)))
                    AnalyticsUtil.trackAction("Law For You", "LawForYou_Success_ScreenDisplayed")
                } else {
                    lawForYouViewModel.notifyFailure(it)
                }
            }
        })

        lawForYouViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigate(LawForYouConfirmationFragmentDirections.actionLawForYouConfirmationFragmentToGenericResultScreenFragment(getFailedResultScreenProperties()))
            AnalyticsUtil.trackAction("Law For you", "LawForYou_Failure_ScreenDisplayed")
        })

        if (lawForYouViewModel.lawForYouDetails.addressLine1.isNotEmpty()) {
            setLabels()
        }

        AnalyticsUtil.trackAction("Law For You", "LawForYou_Confirmation_ScreenDisplayed")
    }

    private fun setLabels() {
        lawForYouViewModel.lawForYouDetails.apply {
            totalMonthlyPremiumPrimaryContentAndLabelView.setContentText(coverPremiumAmount.toRandAmount())
            val coverDetails = getString(R.string.law_for_you_cover_confirmation).format(lawForYouViewModel.selectedCoverOption.cover, coverAssuredAmount.toRandAmount())
            mainMemberCoverDetailsSecondaryContentAndLabelView.setLabelText(coverDetails)

            val addressLine2 = if (addressLine2.isEmpty()) "" else "$addressLine2, "
            val address = "$addressLine1, $addressLine2$suburb, $city, $postalCode"

            memberAddressSecondaryContentAndLabelView.setContentText(address)

            memberContactNumberSecondaryContentAndLabelView.setContentText(cellNumber.toFormattedCellphoneNumber())
            memberEmailSecondaryContentAndLabelView.setContentText(emailAddress)
            policyStartDateSecondaryContentAndLabelView.setContentText(DateUtils.formatDate(inceptionDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN))
            debitDaySecondaryContentAndLabelView.setContentText(dayOfDebit)
            sourceOfFundsSecondaryContentAndLabelView.setContentText(businessSourceIndicator.toTitleCase())
            lawForYouViewModel.selectedRetailAccount.apply { accountSecondaryContentAndLabelView.setContentText("${accountDescription.toSentenceCase()} (${accountNumber})") }
        }

        occupationStatusSecondaryContentAndLabelView.setContentText(lawForYouViewModel.selectedOccupationStatus?.defaultLabel.toTitleCase())

        val occupation = lawForYouViewModel.selectedOccupation?.defaultLabel ?: ""
        if (occupation.isNotEmpty()) {
            occupationSecondaryContentAndLabelView.visibility = View.VISIBLE
            occupationSecondaryContentAndLabelView.setContentText(lawForYouViewModel.selectedOccupation?.defaultLabel.toTitleCase())
        }
    }

    private fun getSuccessResultScreenProperties(coverOption: String, referenceNumber: String, startDate: String): GenericResultScreenProperties {
        hideToolBar()
        lawForYouActivity.setProgressIndicatorVisibility(View.GONE)
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick {
            AnalyticsUtil.trackAction("Law For You", "LawForYou_SuccessCallCentre_ComponentTapped")
            TelephoneUtil.call(lawForYouActivity, CALL_CENTRE_NUMBER)
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.application_approved))
                .setDescription(getString(R.string.law_for_you_application_approved).format(coverOption, referenceNumber, startDate))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setSecondaryButtonLabel(getString(R.string.call))
                .build(true)
    }

    private fun getFailedResultScreenProperties(): GenericResultScreenProperties {
        hideToolBar()
        lawForYouActivity.setProgressIndicatorVisibility(View.GONE)
        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        GenericResultScreenFragment.setSecondaryButtonOnClick {
            AnalyticsUtil.trackAction("Law For You", "LawForYou_FailureCallCentre_ComponentTapped")
            TelephoneUtil.call(lawForYouActivity, CALL_CENTRE_NUMBER)
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.content_description_unsuccess))
                .setDescription(getString(R.string.law_for_you_contact_call_centre))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setSecondaryButtonLabel(getString(R.string.call))
                .build(true)
    }

    override fun onDestroyView() {
        lawForYouViewModel.applyLawForYouResponseMutableLiveData.removeObservers(viewLifecycleOwner)
        lawForYouViewModel.failureResponse.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }
}