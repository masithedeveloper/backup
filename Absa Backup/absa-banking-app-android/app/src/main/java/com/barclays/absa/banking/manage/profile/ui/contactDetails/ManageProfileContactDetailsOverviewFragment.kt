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

package com.barclays.absa.banking.manage.profile.ui.contactDetails

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.manage.profile.services.dto.ContactInformation
import com.barclays.absa.banking.manage.profile.services.dto.PostalAddress
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_contact_details_overview_fragment.*
import styleguide.content.PrimaryContentAndLabelView
import styleguide.utils.extensions.toFormattedCellphoneNumber

class ManageProfileContactDetailsOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_contact_details_overview_fragment) {
    private var contactInformation = ContactInformation()
    private var postalAddressDetails = PostalAddress()
    private var preferredCommunicationLabel = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_contact_details_toolbar_title)
        manageProfileViewModel.fetchPreferredCommunicationMethod()
        initData()
        contactDetailsHeadingAndActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ContactDetailsSummaryScreen_EditButtonClicked")
            findNavController().navigate(R.id.action_manageProfileContactDetailsOverviewFragment_to_manageProfileEditContactDetailsFragment)
        }
    }

    private fun initData() {
        manageProfileViewModel.customerInformation.value?.customerInformation?.contactInformation?.let {
            contactInformation = it
        }

        manageProfileViewModel.customerInformation.value?.customerInformation?.postalAddress?.let {
            postalAddressDetails = it
        }

        manageProfileViewModel.contactInformationToUpdate.apply {
            cellNumber = contactInformation.cellNumber
            homeTelephoneCode = contactInformation.homeTelephoneCode
            homeNumber = contactInformation.homeNumber
            faxHomeCode = contactInformation.faxHomeCode
            homeFaxNumber = contactInformation.homeFaxNumber
            email = contactInformation.email
        }

        manageProfileViewModel.postalAddressDetailsToUpdate.apply {
            addressLineOne = postalAddressDetails.addressLine1
            addressLineTwo = postalAddressDetails.addressLine2
            addressSuburb = postalAddressDetails.suburbRsa
            addressCity = postalAddressDetails.town
            addressPostalCode = postalAddressDetails.postalCode
        }

        manageProfileViewModel.communicationMethodLookUpResult.observe(viewLifecycleOwner, {
            preferredCommunicationLabel = manageProfileViewModel.getLookupValue(it, contactInformation.preferredContactMethod)
            manageProfileViewModel.communicationMethodToUpdate = contactInformation.preferredContactMethod
            setContentViewTextAndVisibility(cellphoneNumberContentView, contactInformation.cellNumber.toFormattedCellphoneNumber())
            setContentViewTextAndVisibility(homePhoneNumberContentView, (contactInformation.homeTelephoneCode + contactInformation.homeNumber).toFormattedCellphoneNumber())
            setContentViewTextAndVisibility(emailAddressContentView, contactInformation.email.toLowerCase(BMBApplication.getApplicationLocale()))
            setContentViewTextAndVisibility(preferredCommunicationMethodContentView, preferredCommunicationLabel)
            dismissProgressDialog()
        })
    }

    private fun setContentViewTextAndVisibility(contentView: PrimaryContentAndLabelView, content: String) {
        if (content.isEmpty()) {
            contentView.visibility = View.GONE
        } else {
            contentView.setContentText(content)
            contentView.visibility = View.VISIBLE
        }
    }
}