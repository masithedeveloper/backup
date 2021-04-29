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
 */

package com.barclays.absa.banking.manage.profile.ui.educationAndEmploymentDetails

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.manage.profile.ManageProfileEducationDetailsFlow
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ADDRESS_DETAILS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.TOOLBAR_TITLE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.OccupationStatus
import com.barclays.absa.banking.manage.profile.ui.addressDetails.GenericAddressDetails
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileGenericAddressFragmentDirections
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileUpdatedAddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressFlowType
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_education_and_employment_details_overview_fragment.*
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.io.Serializable

class ManageProfileEducationAndEmploymentDetailsOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_education_and_employment_details_overview_fragment) {
    private lateinit var genericAddressDetails: GenericAddressDetails

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_education_details_and_employment_details_toolbar)

        manageProfileViewModel.fetchLookupTableValuesForEducationAndEmploymentDetailsScreen()

        populateValues()
    }

    private fun populateValues() {
        manageProfileViewModel.educationDetails.observe(viewLifecycleOwner, { educationDetailsDisplayInformation ->
            if (educationDetailsDisplayInformation.occupationStatus.isNotEmpty()) {
                educationalDetailsActionView.setCustomActionOnclickListener {
                    AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EducationAndEmployementInformationScreen_EditEducationalDetailsButtonClicked")
                    manageProfileViewModel.manageProfileEducationDetailsFlow = ManageProfileEducationDetailsFlow.EDUCATION
                    navigate(ManageProfileEducationAndEmploymentDetailsOverviewFragmentDirections.actionManageProfileEducationAndEmploymentDetailsOverviewFragmentToManageProfileEditEducationDetailsFragment())
                }
            } else {
                educationalDetailsActionView.setCustomActionOnclickListener { showGenericErrorAndExitFlow() }
            }
            toggleVisibilityOfFieldsAndSetValues(highestQualificationContentView, educationDetailsDisplayInformation.highestQualification)
            postMatricContentView.setContentText(if (YES.equals(educationDetailsDisplayInformation.postMatric, true)) getString(R.string.yes) else getString(R.string.no))
            if (educationDetailsDisplayInformation.occupationStatus.isNotEmpty()) {
                employmentDetailsActionView.setCustomActionOnclickListener {
                    AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EducationAndEmploymentInformationScreen_EditEmploymentDetailsButtonClicked")
                    manageProfileViewModel.manageProfileEducationDetailsFlow = ManageProfileEducationDetailsFlow.EMPLOYMENT
                    navigate(ManageProfileEducationAndEmploymentDetailsOverviewFragmentDirections.actionManageProfileEducationAndEmploymentDetailsOverviewFragmentToManageProfileEditEmploymentDetailsFragment())
                }
            } else {
                employmentDetailsActionView.setCustomActionOnclickListener { showGenericErrorAndExitFlow() }
            }

            toggleVisibilityOfFieldsAndSetValues(occupationStatusContentView, educationDetailsDisplayInformation.occupationStatus)
            toggleVisibilityOfFieldsAndSetValues(occupationContentView, educationDetailsDisplayInformation.occupation)
            toggleVisibilityOfFieldsAndSetValues(occupationSectorContentView, educationDetailsDisplayInformation.occupationSector)
            toggleVisibilityOfFieldsAndSetValues(occupationLevelContentView, educationDetailsDisplayInformation.occupationLevel)

            if (isNotEmployed(educationDetailsDisplayInformation.occupationStatusCode)) {
                secondDividerView.visibility = View.GONE
                employerDetailsActionView.visibility = View.GONE
            } else {
                toggleVisibilityOfFieldsAndSetValues(employerNameContentView, educationDetailsDisplayInformation.employerName)
                toggleVisibilityOfFieldsAndSetValues(employerTelephoneNumberContentView, (educationDetailsDisplayInformation.employerTelephoneCode + educationDetailsDisplayInformation.employerTelephoneNumber).toFormattedCellphoneNumber())
                toggleVisibilityOfFieldsAndSetValues(employerAddressContentView, educationDetailsDisplayInformation.employerAddressLineTwo)
                toggleVisibilityOfFieldsAndSetValues(employerSuburbContentView, educationDetailsDisplayInformation.employerSuburb)
                toggleVisibilityOfFieldsAndSetValues(employerTownContentView, educationDetailsDisplayInformation.employerTown)
                toggleVisibilityOfFieldsAndSetValues(employerPostalCodeContentView, educationDetailsDisplayInformation.employerPostalCode)
            }

            genericAddressDetails = GenericAddressDetails().apply {
                flowType = AddressFlowType.EMPLOYER

                genericAddress = AddressDetails().apply {
                    addressLineOne = educationDetailsDisplayInformation.employerName
                    addressLineTwo = educationDetailsDisplayInformation.employerAddressLineTwo
                    suburb = educationDetailsDisplayInformation.employerSuburb
                    town = educationDetailsDisplayInformation.employerTown
                    postalCode = educationDetailsDisplayInformation.employerPostalCode
                    employerName = educationDetailsDisplayInformation.employerName
                    employerTelephoneNumber = educationDetailsDisplayInformation.employerTelephoneCode + educationDetailsDisplayInformation.employerTelephoneNumber
                }
            }

            employerDetailsActionView.setCustomActionOnclickListener {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EducationAndEmployementInformationScreen_EditEmployerDetailsButtonClicked")
                manageProfileViewModel.manageProfileEducationDetailsFlow = ManageProfileEducationDetailsFlow.EMPLOYER
                Bundle().apply {
                    putString(TOOLBAR_TITLE, getString(R.string.manage_profile_employer_address))
                    putParcelable(ADDRESS_DETAILS, genericAddressDetails)
                    putSerializable(ON_BUTTON_CLICK_EVENT, nextStep() as Serializable)
                    putString(ON_BUTTON_CLICK_EVENT_TAG, "ManageProfile_EmployerDetailsScreen_ContinueButtonClicked")
                    findNavController().navigate(R.id.manageProfileGenericAddressFragment, this)
                }
            }

            dismissProgressDialog()
            manageProfileViewModel.educationDetails.removeObservers(viewLifecycleOwner)
        })
    }

    private fun toggleVisibilityOfFieldsAndSetValues(contentView: BaseContentAndLabelView, value: String) =
            if (value.isEmpty()) {
                contentView.visibility = View.GONE
            } else {
                contentView.visibility = View.VISIBLE
                contentView.setContentText(value)
            }
}

fun isNotEmployed(occupationCode: String): Boolean = listOf(OccupationStatus.UNEMPLOYED, OccupationStatus.HOUSEWIFE, OccupationStatus.PENSIONER, OccupationStatus.STUDENT, OccupationStatus.SCHOLAR)
        .any { it.occupationCode == occupationCode }

private fun nextStep(): (baseFragment: BaseFragment, data: ManageProfileUpdatedAddressDetails) -> Unit {
    return { baseFragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails ->
        baseFragment.navigate(ManageProfileGenericAddressFragmentDirections.actionManageProfileGenericAddressFragmentToManageProfileEmployerDetailsConfirmationFragment(manageProfileUpdatedAddressDetails))
    }
}