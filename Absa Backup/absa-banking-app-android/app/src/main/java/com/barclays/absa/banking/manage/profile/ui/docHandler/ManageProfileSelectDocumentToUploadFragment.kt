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

package com.barclays.absa.banking.manage.profile.ui.docHandler

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileDocUploadType
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.manage_profile_select_document_to_upload_fragment.*

class ManageProfileSelectDocumentToUploadFragment : ManageProfileBaseFragment(R.layout.manage_profile_select_document_to_upload_fragment) {
    private lateinit var documentManagementDialog: DocumentManagementDialog
    private var retryCounter = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_select_document_upload_documents_title)

        initOnClickListeners()
        initData()
    }

    private fun initData() {
        if (manageProfileViewModel.docHandlerResponse.value?.requiredDocuments.isNullOrEmpty()) {
            manageProfileViewModel.fetchCase()
            showProgressDialog()
        }

        documentManagementDialog = DocumentManagementDialog(this, object : FileSelectionInterface {
            override fun openFile() {
                val file = if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT) {
                    manageProfileViewModel.proofOfIdentityFile?.let { byteArray ->
                        ManageProfileFileUtils.createLocalFileAndReturnFileObject(manageProfileActivity, ProfileManager.getInstance().activeUserProfile.userId.toString(),
                                byteArray, manageProfileViewModel.proofOfIdentityFileDetails.fileName)
                    }
                } else {
                    manageProfileViewModel.proofOfResidenceFile?.let { byteArray ->
                        ManageProfileFileUtils.createLocalFileAndReturnFileObject(manageProfileActivity, ProfileManager.getInstance().activeUserProfile.userId.toString(),
                                byteArray, manageProfileViewModel.proofOfResidenceFileDetails.fileName)
                    }
                }

                val mimeType = if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT) {
                    manageProfileViewModel.proofOfIdentityFileDetails.fileMime
                } else {
                    manageProfileViewModel.proofOfResidenceFileDetails.fileMime
                }

                Intent().apply {
                    action = Intent.ACTION_VIEW
                    val contentUri = file?.let { FileProvider.getUriForFile(manageProfileActivity, "${BuildConfig.APPLICATION_ID}.provider", it) }
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    setDataAndType(contentUri, mimeType)
                    manageProfileActivity.startActivity(this)
                }
            }

            override fun fileToBeRemoved() {
                if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT) {
                    manageProfileViewModel.proofOfIdentityFile = null

                    idDocumentUploadOptionActionButtonView.apply {
                        setIcon(R.drawable.ic_add_dark)
                        setCaptionText(getString(R.string.manage_profile_select_document_add_identity_document))
                        setOnClickListener {
                            manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.ID_DOCUMENT
                            navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionManageProfileSelectDocumentToUploadFragmentToManageProfileDocumentUploadSelectDocumentTypeFragment())
                        }
                    }
                } else if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.PROOF_OF_RESIDENCE) {
                    manageProfileViewModel.proofOfResidenceFile = null
                    proofOfResUploadOptionActionButtonView.apply {
                        setIcon(R.drawable.ic_add_dark)
                        setCaptionText(getString(R.string.manage_profile_select_document_add_identity_document))
                        setOnClickListener {
                            manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.PROOF_OF_RESIDENCE
                            navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionManageProfileSelectDocumentToUploadFragmentToManageProfileDocumentUploadSelectDocumentTypeFragment())
                        }
                    }
                }
            }
        })

        manageProfileViewModel.docHandlerResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })

        manageProfileViewModel.docHandlerUploadResponse.observe(viewLifecycleOwner, {
            if (it.error.isNullOrBlank()) {
                manageProfileActivity.dismissUploadIndicator()
                navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).uploadedDocumentSuccessScreen("ManageProfile_DocumentUploadUpdateDetailsSuccessScreen_DoneButtonClicked")))
            } else if (!it.error.isNullOrEmpty()) {
                navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).uploadedDocumentFailureScreen("ManageProfile_DocumentUploadUpdateDetailsFailureScreen_CallFICAHelplineButtonClicked")))
            }
        })

        manageProfileViewModel.docHandlerFetchCaseFailure.observe(viewLifecycleOwner, {
            if (retryCounter <= 2) {
                retryCounter += 1
                manageProfileViewModel.fetchCase()
            } else {
                navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(it, "ManageProfile_DocumentUploadUpdateDetailsErrorScreen_DoneButtonClicked")))
            }
        })

        if (manageProfileViewModel.proofOfResidenceFile != null) {
            proofOfResUploadOptionActionButtonView.apply {
                setIcon(R.drawable.ic_document_image)
                setCaptionText(manageProfileViewModel.proofOfResidenceFileDetails.fileName)
                setOnClickListener {
                    ManageProfileFileUtils.createUserDirectories(baseActivity)
                    manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.PROOF_OF_RESIDENCE
                    documentManagementDialog.fileOptionsDialog()
                }
            }
        } else {
            proofOfResUploadOptionActionButtonView.setOnClickListener {
                ManageProfileFileUtils.createUserDirectories(baseActivity)
                manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.PROOF_OF_RESIDENCE
                navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionManageProfileSelectDocumentToUploadFragmentToManageProfileDocumentUploadSelectDocumentTypeFragment())
            }
        }

        if (manageProfileViewModel.proofOfIdentityFile != null) {
            idDocumentUploadOptionActionButtonView.apply {
                setIcon(R.drawable.ic_document_image)
                setCaptionText(manageProfileViewModel.proofOfIdentityFileDetails.fileName)
                setOnClickListener {
                    ManageProfileFileUtils.createUserDirectories(baseActivity)
                    manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.ID_DOCUMENT
                    documentManagementDialog.fileOptionsDialog()
                }
            }
        } else {
            idDocumentUploadOptionActionButtonView.setOnClickListener {
                ManageProfileFileUtils.createUserDirectories(baseActivity)
                manageProfileViewModel.manageProfileDocUploadType = ManageProfileDocUploadType.ID_DOCUMENT
                navigate(ManageProfileSelectDocumentToUploadFragmentDirections.actionManageProfileSelectDocumentToUploadFragmentToManageProfileDocumentUploadSelectDocumentTypeFragment())
            }
        }
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_SelectDocumentScreen_ContinueButtonClicked")
            manageProfileActivity.showUploadIndicator()
            manageProfileViewModel.createDocuments()
        }
    }

    override fun onResume() {
        super.onResume()
        manageProfileViewModel.proofOfResidenceFile?.let {
            proofOfResUploadOptionActionButtonView.setCaptionText(manageProfileViewModel.proofOfResidenceFileDetails.fileName)
        }

        manageProfileViewModel.proofOfIdentityFile?.let {
            idDocumentUploadOptionActionButtonView.setCaptionText(manageProfileViewModel.proofOfIdentityFileDetails.fileName)
        }

        validateData()
    }

    private fun validateData() {
        if (manageProfileViewModel.proofOfIdentityFile != null && manageProfileViewModel.proofOfResidenceFile != null) {
            continueButton.isEnabled = true
        }
    }
}