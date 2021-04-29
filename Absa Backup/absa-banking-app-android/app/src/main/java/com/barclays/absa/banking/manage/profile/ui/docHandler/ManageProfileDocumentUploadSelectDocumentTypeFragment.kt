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
import com.barclays.absa.banking.manage.profile.ManageProfileDocUploadType
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerDocumentType
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import kotlinx.android.synthetic.main.manage_profile_document_upload_select_document_type_fragment.*
import styleguide.utils.AnimationHelper
import java.util.*

class ManageProfileDocumentUploadSelectDocumentTypeFragment : ManageProfileBaseFragment(R.layout.manage_profile_document_upload_select_document_type_fragment) {
    private lateinit var manageProfileAdapter: ManageProfileDocumentTypeRecyclerViewAdapter
    private lateinit var documentTypes: ArrayList<ManageProfileDocHandlerDocumentType>
    private var selectedItemPosition = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_document_upload_select_type_of_document_title)

        documentTypes = if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT) {
            manageProfileViewModel.retrieveProofOfIdentificationSourceList()
        } else {
            manageProfileViewModel.retrieveProofOfResidenceSourceList()
        }

        manageProfileAdapter = ManageProfileDocumentTypeRecyclerViewAdapter(documentTypes) {
            selectedItemPosition = it
            if (it != -1) {
                errorMessageTextView.visibility = View.GONE
            }
        }

        documentTypeRecyclerView.adapter = manageProfileAdapter

        continueButton.setOnClickListener {
            if (selectedItemPosition == -1) {
                AnimationHelper.shakeShakeAnimate(errorMessageTextView)
                errorMessageTextView.visibility = View.VISIBLE
            } else {
                if (manageProfileViewModel.manageProfileDocUploadType == ManageProfileDocUploadType.ID_DOCUMENT) {
                    manageProfileViewModel.idDocumentType = documentTypes[selectedItemPosition]
                } else {
                    manageProfileViewModel.proofOfResidenceType = documentTypes[selectedItemPosition]
                }

                BaseAlertDialog.showAlertDialog(
                        AlertDialogProperties.Builder()
                                .message(getString(R.string.manage_profile_document_3_months_notice))
                                .title(getString(R.string.manage_profile_document_upload_file_requirements))
                                .positiveButton(getString(R.string.ok))
                                .positiveDismissListener { _, _ -> ManageProfileFileSelectionUtil(this).selectFileDialog() }
                                .build()
                )
            }
        }
    }
}