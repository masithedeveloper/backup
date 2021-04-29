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
 */

package com.barclays.absa.banking.manage.profile.ui.educationAndEmploymentDetails

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ADDRESS_DETAILS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileUpdatedAddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.ConfirmScreenItem
import com.barclays.absa.banking.manage.profile.ui.widgets.ConfirmScreenItemWidget
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_edit_employer_details_fragment.continueButton
import kotlinx.android.synthetic.main.manage_profile_employer_details_confirmation_fragment.*
import styleguide.utils.extensions.removeSpaces

class ManageProfileEmployerDetailsConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_employer_details_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var confirmScreenItemList = ArrayList<ConfirmScreenItem>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                manageProfileViewModel.updateEmploymentInformation()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        var manageProfileUpdatedAddressDetails = ManageProfileUpdatedAddressDetails()
        arguments?.getParcelable<ManageProfileUpdatedAddressDetails>(ADDRESS_DETAILS)?.let { manageProfileUpdatedAddressDetails = it }

        manageProfileViewModel.manageProfileEducationDetailsModel.apply {
            employerAddressLineOne = manageProfileUpdatedAddressDetails.employerName
            employerTelephoneCode = if (manageProfileUpdatedAddressDetails.employerTelephoneNumber.isNotEmpty()) manageProfileUpdatedAddressDetails.employerTelephoneNumber.removeSpaces().substring(0, 3) else ""
            employerTelephoneNumber = if (manageProfileUpdatedAddressDetails.employerTelephoneNumber.isNotEmpty()) manageProfileUpdatedAddressDetails.employerTelephoneNumber.removeSpaces().substring(3, 10) else ""
            employerFaxCode = if (manageProfileUpdatedAddressDetails.employerFaxNumber.isNotEmpty()) manageProfileUpdatedAddressDetails.employerFaxNumber.removeSpaces().substring(0, 3) else manageProfileViewModel.customerInformation.value?.customerInformation?.employmentInformation?.faxWorkCode ?: ""
            employerFaxNumber = if (manageProfileUpdatedAddressDetails.employerFaxNumber.isNotEmpty()) manageProfileUpdatedAddressDetails.employerFaxNumber.removeSpaces().substring(3, 10) else manageProfileViewModel.customerInformation.value?.customerInformation?.employmentInformation?.workFaxNumber ?: ""
            employerAddressLineTwo = manageProfileUpdatedAddressDetails.addressLineOne
            employerSuburb = manageProfileUpdatedAddressDetails.suburb
            employerTown = manageProfileUpdatedAddressDetails.town
            employerPostalCode = manageProfileUpdatedAddressDetails.postalCode
        }

        manageProfileUpdatedAddressDetails.apply {
            addItemToConfirmationScreen(employerName, getString(R.string.manage_profile_address_widget_employer_name_label))
            addItemToConfirmationScreen(employerTelephoneNumber, getString(R.string.manage_profile_address_widget_telephone_number))
            addItemToConfirmationScreen(employerFaxNumber, getString(R.string.manage_profile_address_widget_fax_number_label))
            addItemToConfirmationScreen(addressLineOne, getString(R.string.manage_profile_address_widget_address_line_one_label))
            addItemToConfirmationScreen(suburb, getString(R.string.manage_profile_address_details_overview_suburb_label))
            addItemToConfirmationScreen(town, getString(R.string.manage_profile_address_details_overview_city_label))
            addItemToConfirmationScreen(postalCode, getString(R.string.manage_profile_address_details_overview_postal_code_label))
        }

        confirmScreenItemList.forEach {
            confirmationItemLinearLayout.addView(ConfirmScreenItemWidget(manageProfileActivity, it))
        }

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_ConfirmEmployerDetailsChangesScreen_SaveButtonClicked")
            manageProfileViewModel.updateEmploymentInformation()
        }
    }

    private fun addItemToConfirmationScreen(newValue: String, selectedLabel: String) {
        ConfirmScreenItem().apply {
            if (newValue.isNotEmpty()) {
                value = newValue
                label = selectedLabel
                confirmScreenItemList.add(this)
            }
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && customerInformation.transactionStatus.equals(SUCCESS, true) -> navigate(ManageProfileEmployerDetailsConfirmationFragmentDirections.actionManageProfileEmployerDetailsConfirmationFragmentToManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_ EmployerDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileEmployerDetailsConfirmationFragmentDirections.actionManageProfileEmployerDetailsConfirmationFragmentToManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_EmployerDetailsUpdateFailureScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(manageProfileActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateEmploymentInformation() }
            }
        })
    }
}