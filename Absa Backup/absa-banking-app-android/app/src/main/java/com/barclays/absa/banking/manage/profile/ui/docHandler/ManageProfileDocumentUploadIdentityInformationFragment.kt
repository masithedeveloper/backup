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
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.manage.profile.services.dto.PersonalInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.manage_profile_document_upload_identity_information_fragment.*
import styleguide.utils.extensions.toTitleCase

class ManageProfileDocumentUploadIdentityInformationFragment : ManageProfileBaseFragment(R.layout.manage_profile_document_upload_identity_information_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_document_upload_identity_information_title)

        val personalInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.personalInformation ?: PersonalInformation()

        initialsContentView.setContentText(personalInformation.initials.toUpperCase(BMBApplication.getApplicationLocale()))
        firstNameContentView.setContentText(personalInformation.firstName.toTitleCase())
        surnameContentView.setContentText(personalInformation.lastName.toTitleCase())
        identityNumberContentView.setContentText(personalInformation.identityNo)
        dateOfBirthContentView.setContentText(DateUtils.getDateWithMonthNameFromStringWithoutHyphen(personalInformation.dateOfBirth))

        uploadDocumentsButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_IdentityInformationScreen_UploadDocumentButtonClicked")
            navigate(ManageProfileDocumentUploadIdentityInformationFragmentDirections.actionManageProfileDocumentUploadIdentityInformationFragmentToManageProfileSelectDocumentToUploadFragment())
        }
    }
}