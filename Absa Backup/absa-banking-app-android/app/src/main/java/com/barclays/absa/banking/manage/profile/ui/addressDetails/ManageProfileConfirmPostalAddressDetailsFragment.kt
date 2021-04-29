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

package com.barclays.absa.banking.manage.profile.ui.addressDetails

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
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ADDRESS_DETAILS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_confirm_postal_address_details_fragment.*
import styleguide.content.BaseContentAndLabelView

class ManageProfileConfirmPostalAddressDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_confirm_postal_address_details_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updatePostalAddress() }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_address_details_confirm_toolbar_title)

        arguments?.getParcelable<ManageProfileUpdatedAddressDetails>(ADDRESS_DETAILS)?.let { populateViewModel(it) }

        initData()
        initOnClickListeners()
        initObservers()
    }

    private fun populateViewModel(manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails) {
        manageProfileViewModel.updatedPostalAddressDetails.apply {
            addressLineOne = manageProfileUpdatedAddressDetails.addressLineOne
            addressLineTwo = manageProfileUpdatedAddressDetails.addressLineTwo
            addressSuburb = manageProfileUpdatedAddressDetails.suburb
            addressCity = manageProfileUpdatedAddressDetails.town
            addressPostalCode = manageProfileUpdatedAddressDetails.postalCode
        }
    }

    private fun initData() {
        manageProfileViewModel.updatedPostalAddressDetails.apply {
            hideEmptyFields(addressLineOneContentView, addressLineOne)
            hideEmptyFields(addressLineTwoContentView, addressLineTwo)
            hideEmptyFields(suburbContentView, addressSuburb)
            hideEmptyFields(cityContentView, addressCity)
            hideEmptyFields(postalCodeContentView, addressPostalCode)
        }
    }

    private fun initOnClickListeners() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ConfirmDetailChangesScreen_SaveButtonClicked")
            manageProfileViewModel.updatePostalAddress()
        }
    }

    private fun hideEmptyFields(displayedField: BaseContentAndLabelView, content: String) {
        if (content.isBlank()) {
            displayedField.visibility = View.GONE
        } else {
            displayedField.setContentText(content)
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && customerInformation.transactionStatus.equals(SUCCESS, true) -> navigate(ManageProfileConfirmPostalAddressDetailsFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "ManageProfile_PostalAddressUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileConfirmPostalAddressDetailsFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_PostalAddressUpdateFailureScreen_DoneButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updatePostalAddress() }
            }
        })
    }
}