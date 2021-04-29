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

package com.barclays.absa.banking.manage.profile.ui.docHandler

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.services.dto.PostalAddress
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_document_upload_address_information_fragment.*
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toTitleCase

private const val COUNTRY = "country"

class ManageProfileDocumentUploadAddressInformationFragment : ManageProfileBaseFragment(R.layout.manage_profile_document_upload_address_information_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_address_details_overview_residential_address_label)
        val addressInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.residentialAddress ?: PostalAddress()

        hideEmptyFields(addressLineOneContentView, addressInformation.addressLine1.toTitleCase())
        hideEmptyFields(addressLineTwoContentView, addressInformation.addressLine2.toTitleCase())
        hideEmptyFields(suburbContentView, addressInformation.suburbRsa.toTitleCase())
        hideEmptyFields(cityContentView, addressInformation.town.toTitleCase())
        hideEmptyFields(postalCodeContentView, addressInformation.postalCode)
        hideEmptyFields(countryContentView, arguments?.getString(COUNTRY).toTitleCase())

        uploadButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ResidentialAddressScreen_UploadDocumentsButtonClicked")
            navigate(ManageProfileDocumentUploadAddressInformationFragmentDirections.actionManageProfileDocumentUploadAddressInformationFragmentToManageProfileSelectDocumentToUploadFragment())
        }
    }

    private fun hideEmptyFields(displayElement: BaseContentAndLabelView, content: String) {
        if (content.isBlank()) {
            displayElement.visibility = View.GONE
        } else {
            displayElement.setContentText(content)
        }
    }
}