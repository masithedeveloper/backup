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

package com.barclays.absa.banking.manage.profile.ui.otherPersonalDetails

import android.os.Bundle
import android.util.Base64
import android.util.Base64.DEFAULT
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.ssl.FileResponse
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils
import com.barclays.absa.banking.manage.profile.services.dto.BCMSCaseIdResponse
import com.barclays.absa.banking.manage.profile.services.dto.PersonalInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.PROOF_OF_IDENTIFICATION
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerFileToFetchDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileFileOptions
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileUploadedDocumentsWidget
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.manage_profile_personal_details_overview_fragment.*
import styleguide.utils.extensions.toTitleCase
import java.io.File

class ManageProfilePersonalDetailsOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_personal_details_overview_fragment), ManageProfileFileOptions {
    private lateinit var customerInformation: PersonalInformation
    private lateinit var manageProfileUploadedDocumentWidget: ManageProfileUploadedDocumentsWidget

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_personal_details_toolbar_title)

        initData()
        initOnClickListeners()
    }

    private fun initData() {
        customerInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.personalInformation!!
        if (manageProfileViewModel.personalInformation.value == null) {
            manageProfileViewModel.fetchLookupTableValuesForPersonalDetailsScreen()
        }

        manageProfileViewModel.personalInformation.observe(viewLifecycleOwner, { personalInformation ->
            initialsContentView.setContentText(customerInformation.initials)
            firstNameContentView.setContentText(customerInformation.firstName.toTitleCase())
            surnameContentView.setContentText(customerInformation.lastName.toTitleCase())
            if (customerInformation.identityType == "03") {
                identityNumberContentView.setLabelText(getString(R.string.manage_profile_passport_field_description))
            }
            identityNumberContentView.setContentText(customerInformation.identityNo)
            dateOfBirthContentView.setContentText(DateUtils.getDateWithMonthNameFromStringWithoutHyphen(customerInformation.dateOfBirth))

            titleContentView.setContentText(personalInformation?.title.toTitleCase())
            dependantsContentView.setContentText(customerInformation.dependents)
            homeLanguageContentView.setContentText(personalInformation?.homeLanguage.toTitleCase())
            nationalityContentView.setContentText(personalInformation?.nationality.toTitleCase())
            countryOfBirthContentView.setContentText(personalInformation?.countryOfBirth.toTitleCase())

            if (nationalityContentView.contentTextViewValue.isEmpty() || titleContentView.contentTextViewValue.isEmpty()) {
                otherPersonalDetailsActionView.setCustomActionOnclickListener { showGenericErrorAndExitFlow() }
            }

            dismissProgressDialog()
        })

        var bcmsCase = BCMSCaseIdResponse()
        manageProfileViewModel.bcmsCaseId.value?.let { bcmsCase = it }

        if (bcmsCase.isDocumentUploadComplete && bcmsCase.documentUploadStatus.isNotEmpty()) {
            identityInformationActionView.setActionTextGone()
            manageProfileUploadedDocumentWidget = ManageProfileUploadedDocumentsWidget(manageProfileActivity, bcmsCase.documentUploadStatus.first(), this)
            documentLinearLayout.addView(manageProfileUploadedDocumentWidget)
        } else {
            identityInformationActionView.setActionTextVisible()
        }
    }

    private fun initOnClickListeners() {
        otherPersonalDetailsActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_PersonalDetailsScreen_EditOtherPersonalDetailsButtonClicked")
            navigate(ManageProfilePersonalDetailsOverviewFragmentDirections.actionManageProfilePersonalDetailsOverviewFragmentToManageProfileEditOtherPersonalDetailsFragment())
        }
        identityInformationActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_PersonalDetailsScreen_EditIdentityInformationButtonClicked")
            navigate(ManageProfilePersonalDetailsOverviewFragmentDirections.actionManageProfilePersonalDetailsOverviewFragmentToManageProfileDocumentUploadIdentityInformationFragment())
        }
    }

    override fun openFile() {
        val bcmsCase = manageProfileViewModel.bcmsCaseId.value ?: BCMSCaseIdResponse()
        val indexOfFile = bcmsCase.documentUploadStatus.indexOfFirst { it.description == PROOF_OF_IDENTIFICATION }
        showProgressDialog()

        val filePath = "UserFiles/${ProfileManager.getInstance().activeUserProfile.userId.toString()}/${bcmsCase.documentUploadStatus[indexOfFile].name}"
        val file = File(baseActivity.cacheDir, filePath)
        if (!file.exists()) {
            ManageProfileDocHandlerFileToFetchDetails().apply {
                caseId = bcmsCase.caseID
                documentId = bcmsCase.documentUploadStatus[indexOfFile].documentId
                encodedPassword = Base64.encodeToString(bcmsCase.password.toByteArray(Charsets.UTF_8), DEFAULT)
                fileName = bcmsCase.documentUploadStatus[indexOfFile].name

                manageProfileViewModel.downloadFile(this).observe(viewLifecycleOwner, { fileResponse ->
                    if (fileResponse is FileResponse.SuccessResponse) {
                        ManageProfileFileUtils.saveFileToLocalStorage(baseActivity, ProfileManager.getInstance().activeUserProfile.userId.toString(), fileResponse.byteArray, bcmsCase.documentUploadStatus[indexOfFile].name)
                        openLocalFile()
                    } else {
                        navigate(ManageProfilePersonalDetailsOverviewFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this@ManageProfilePersonalDetailsOverviewFragment).updatedOtherPersonalInformationFailure(getString(R.string.manage_profile_could_not_download_selected_file))))
                    }
                    dismissProgressDialog()
                })
            }
        } else {
            dismissProgressDialog()
            openLocalFile()
        }
    }

    private fun openLocalFile() {
        val filePath = "UserFiles/${ProfileManager.getInstance().activeUserProfile.userId.toString()}/${manageProfileViewModel.bcmsCaseId.value?.documentUploadStatus?.first()?.name}"
        val file = File(baseActivity.cacheDir, filePath)
        manageProfileActivity.openFileOnDevice(file)
    }
}