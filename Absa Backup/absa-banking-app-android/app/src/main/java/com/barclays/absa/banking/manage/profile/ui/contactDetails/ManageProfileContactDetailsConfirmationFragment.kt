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

package com.barclays.absa.banking.manage.profile.ui.contactDetails

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.services.dto.LookupResult
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_contact_details_confirmation_fragment.*
import kotlinx.android.synthetic.main.manage_profile_contact_details_overview_fragment.cellphoneNumberContentView
import kotlinx.android.synthetic.main.manage_profile_contact_details_overview_fragment.emailAddressContentView
import kotlinx.android.synthetic.main.manage_profile_contact_details_overview_fragment.homePhoneNumberContentView
import styleguide.content.PrimaryContentAndLabelView
import styleguide.utils.extensions.toFormattedCellphoneNumber

class ManageProfileContactDetailsConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_contact_details_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateContactDetails() }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_contact_details_toolbar_title)
        initData()
        setUpOnClickListener()
        initObservers()
    }

    private fun initData() {
        val contactInformationToUpdate = manageProfileViewModel.contactInformationToUpdate
        val postalAddressDetailsToUpdate = manageProfileViewModel.postalAddressDetailsToUpdate

        setContentViewTextAndVisibility(cellphoneNumberContentView, contactInformationToUpdate.cellNumber)
        setContentViewTextAndVisibility(homePhoneNumberContentView, (contactInformationToUpdate.homeTelephoneCode + contactInformationToUpdate.homeNumber).toFormattedCellphoneNumber())
        setContentViewTextAndVisibility(emailAddressContentView, contactInformationToUpdate.email.toLowerCase(BMBApplication.getApplicationLocale()))

        var preferredCommunication = LookupResult()
        manageProfileViewModel.communicationMethodLookUpResult.value?.let {
            preferredCommunication = it
        }

        setContentViewTextAndVisibility(preferredCommunicationMethodContentView, manageProfileViewModel.getLookupValue(preferredCommunication, manageProfileViewModel.communicationMethodToUpdate))

        if (manageProfileViewModel.hasPostalAddressDetailsChanged) {
            setContentViewTextAndVisibility(addressLineOneContentView, postalAddressDetailsToUpdate.addressLineOne)
            setContentViewTextAndVisibility(addressLineTwoContentView, postalAddressDetailsToUpdate.addressLineTwo)
            setContentViewTextAndVisibility(suburbContentView, postalAddressDetailsToUpdate.addressSuburb)
            setContentViewTextAndVisibility(cityContentView, postalAddressDetailsToUpdate.addressCity)
            setContentViewTextAndVisibility(postalCodeContentView, postalAddressDetailsToUpdate.addressPostalCode)
        } else {
            postalAddressConstraintLayout.visibility = View.GONE
        }
    }

    private fun setContentViewTextAndVisibility(contentView: PrimaryContentAndLabelView, content: String) {
        if (content.isEmpty()) {
            contentView.visibility = View.GONE
        } else {
            contentView.setContentText(content)
            contentView.visibility = View.VISIBLE
        }
    }

    private fun setUpOnClickListener() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ConfirmContactDetailChangesScreen_SaveButtonClicked")
            manageProfileViewModel.updateContactDetails()
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && BMBConstants.SUCCESS.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileContactDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", "MangeProfile_ ContactDetailsUpdateSuccessScreen_DoneButtonClicked")))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(ManageProfileContactDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, "ManageProfile_ContactDetailsUpdateFailureScreen_OKButtonClicked")))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateContactDetails() }
            }
            dismissProgressDialog()
        })
    }
}