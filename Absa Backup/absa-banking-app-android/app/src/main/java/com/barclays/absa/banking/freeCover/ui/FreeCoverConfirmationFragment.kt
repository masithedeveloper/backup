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

package com.barclays.absa.banking.freeCover.ui

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.freeCover.services.dto.ApplyForFreeCoverResponse
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.banking.freeCover.ui.FreeCoverBeneficiaryDetailsFragment.Companion.ESTATE_LATE_CODE
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.DateUtils.*
import com.barclays.absa.utils.PdfUtil
import kotlinx.android.synthetic.main.free_cover_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toRandAmount

class FreeCoverConfirmationFragment : FreeCoverBaseFragment(R.layout.free_cover_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var applyForFreeCoverResponse = ApplyForFreeCoverResponse()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                freeCoverViewModel.applyFreeCover(freeCoverViewModel.applyFreeCoverData)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        attachObservers()
        setUpFreeCoverData()
        initViews()
        setUpOnClickListener()

        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_ConfirmationScreen_ScreenDisplayed")
    }

    private fun setUpToolBar() {
        setToolBar(R.string.free_cover_confirmation)
        freeCoverInterface.setStep(4)
    }

    private fun attachObservers() {
        freeCoverViewModel.applyForFreeCoverStatusResponse = MutableLiveData()
        freeCoverViewModel.applyForFreeCoverStatusResponse.observe(viewLifecycleOwner, Observer {

            sureCheckDelegate.processSureCheck(baseActivity, it) {
                dismissProgressDialog()
                applyForFreeCoverResponse = it
                when {
                    it.transactionStatus.equals(BMBConstants.SUCCESS, true) -> {
                        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_ResultScreen_SuccessfulScreenDisplayed")
                        showSuccessScreen(it.freeInsuranceResponse.policyNumber, it.freeInsuranceResponse.commencementDate)
                    }
                    it.transactionStatus.equals(BMBConstants.FAILURE, true) -> {
                        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_ResultScreen_UnsuccessfulScreenDisplayed")
                        showFailureScreen()
                    }
                    else -> showErrorScreen()
                }
            }
        })
    }

    private fun initViews() {
        freeCoverViewModel.coverAmountApplyFreeCoverResponse.value?.coverAmount?.let {
            mainMemberSecondaryContentAndLabelView.setLabelText(getString(R.string.free_cover_cover_amount_and_monthly_premium).format(it.coverAmount.toRandAmount(), it.monthlyPremium.toRandAmount()))
        }
        employmentStatusSecondaryContentAndLabelView.setContentText(freeCoverViewModel.applyFreeCoverData.employmentStatus.displayValue)

        freeCoverViewModel.applyFreeCoverData.occupation.displayValue.let { occupation ->
            if (occupation.isNullOrEmpty()) {
                occupationSecondaryContentAndLabelView.visibility = View.GONE
            } else {
                with(occupationSecondaryContentAndLabelView) {
                    setContentText(occupation)
                    visibility = View.VISIBLE
                }
            }
        }

        if (freeCoverViewModel.isDeceasedEstate) {
            beneficiaryDetailsSecondaryContentAndLabelView.setContentText(getString(R.string.free_cover_estate_late).format(freeCoverViewModel.applyFreeCoverData.accountHolderName))
            addressSecondaryContentAndLabelView.visibility = View.GONE
            contactNumberSecondaryContentAndLabelView.visibility = View.GONE
            emailSecondaryContentAndLabelView.visibility = View.GONE
            beneficiaryDateOfBirthSecondaryAndLabelView.visibility = View.GONE
        } else {
            with(freeCoverViewModel.applyFreeCoverData) {
                val addressLineTwo = if (addressLineTwo.isEmpty()) "" else "$addressLineTwo, "
                val address = "$addressLineOne, $addressLineTwo$suburbRsa, $town, $postalCode"
                val relationship = InsuranceBeneficiaryHelper.findRelationship(relationshipCode)
                val title = InsuranceBeneficiaryHelper.findTitle(titleCode)
                beneficiaryDetailsSecondaryContentAndLabelView.setContentText("$title $firstName $surname ($relationship)")
                beneficiaryDateOfBirthSecondaryAndLabelView.setContentText(formatDate(dateOfBirth, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
                addressSecondaryContentAndLabelView.setContentText(address)

                if (cellphoneNumber.isNotBlank()) {
                    contactNumberSecondaryContentAndLabelView.setContentText(cellphoneNumber.toFormattedCellphoneNumber())
                } else {
                    contactNumberSecondaryContentAndLabelView.visibility = View.GONE
                }
                if (emailAddress.isNotBlank()) {
                    emailSecondaryContentAndLabelView.setContentText(emailAddress)
                } else {
                    emailSecondaryContentAndLabelView.visibility = View.GONE
                }
            }
        }
    }

    private fun setUpFreeCoverData() {
        freeCoverInterface.riskBasedApproachViewModel().personalInformationResponse.value?.customerInformation?.let { customerInformation ->
            freeCoverViewModel.applyFreeCoverData.apply {
                if (freeCoverViewModel.isDeceasedEstate) {
                    initials = customerInformation.initials ?: ""
                    titleCode = ESTATE_LATE_CODE
                    firstName = customerInformation.firstName ?: ""
                    surname = customerInformation.lastName ?: ""
                    dateOfBirth = formatDate(customerInformation.dateOfBirth.toString(), "yyyyMMdd", DASHED_DATE_PATTERN)
                    addressLineOne = customerInformation.residentialAddress?.addressLine1 ?: ""
                    suburbRsa = customerInformation.residentialAddress?.suburbRsa ?: ""
                    town = customerInformation.residentialAddress?.town ?: ""
                    postalCode = customerInformation.residentialAddress?.postalCode ?: ""
                    cellphoneNumber = customerInformation.cellNumber ?: ""
                    emailAddress = customerInformation.email ?: ""
                }
                idNumber = customerInformation.identityNo ?: ""
                idType = customerInformation.identityType ?: ""
                accountHolderName = "${customerInformation.initials} ${customerInformation.lastName}"
            }
        }
    }

    private fun setUpOnClickListener() {
        confirmButton.setOnClickListener {
            if (termsAndConditionsCheckBoxView.isChecked) {
                freeCoverViewModel.applyFreeCover(freeCoverViewModel.applyFreeCoverData)
            } else {
                termsAndConditionsCheckBoxView.setErrorMessage(getString(R.string.please_accept_terms_and_conditions))
            }
        }

        CommonUtils.makeTextClickable(context, R.string.free_cover_i_agree_to_terms,
                R.string.free_cover_terms_and_conditions, termsAndConditionsCheckBoxView.checkBoxTextView,
                R.color.graphite, object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_ConfirmationScreen_TermsAndConditionsButtonClicked")
                PdfUtil.showPDFInApp(hostActivity, freeCoverViewModel.freeCoverData.termsAndConditionsURL)
            }
        })
    }

    private fun showSuccessScreen(policyNumber: String, policyStartDate: String) {
        hideToolBar()
        freeCoverInterface.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.application_approved))
                .setDescription(getString(R.string.free_cover_application_approved).format(policyNumber, policyStartDate))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            appCacheService.setShouldUpdateExploreHub(true)
            loadAccountsAndGoHome()
        }

        navigate(FreeCoverConfirmationFragmentDirections.actionFreeCoverConfirmationFragmentToFreeCoverGenericResultScreenFragment2(resultScreenProperties))
    }

    private fun showFailureScreen() {
        hideToolBar()
        freeCoverInterface.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.content_description_unsuccess))
                .setDescription(getString(R.string.free_cover_contact_call_centre))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.free_cover_call_centre_number))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick { navigateToHomeScreenWithoutReloadingAccounts() }

        navigate(FreeCoverConfirmationFragmentDirections.actionFreeCoverConfirmationFragmentToFreeCoverGenericResultScreenFragment2(resultScreenProperties))
    }

    private fun showErrorScreen() {
        hideToolBar()
        freeCoverInterface.hideProgressIndicatorView()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.free_cover_something_went_wrong_error))
                .setDescription(getString(R.string.free_cover_something_went_wrong_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick { navigateToHomeScreenWithoutReloadingAccounts() }

        navigate(FreeCoverConfirmationFragmentDirections.actionFreeCoverConfirmationFragmentToFreeCoverGenericResultScreenFragment2(resultScreenProperties))
    }
}