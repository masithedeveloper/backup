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

package com.barclays.absa.banking.manage.profile.ui.widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.manage.profile.services.dto.DocumentUploadStatus
import kotlinx.android.synthetic.main.manage_profile_uploaded_documents_widget.view.*

class ManageProfileUploadedDocumentsWidget(context: Context, documentUploadStatus: DocumentUploadStatus, manageProfileFileOptions: ManageProfileFileOptions) : ConstraintLayout(context, null, 0) {
    init {
        View.inflate(context, R.layout.manage_profile_uploaded_documents_widget, this)

        documentEmailOptionActionButtonView.setOnClickListener {
            EmailUtil.email(context as BaseActivity, "FICAhelp@absa.co.za")
        }

        documentHelplineOptionActionButtonView.setOnClickListener {
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse(context.getString(R.string.manage_profile_fic_center_number))
                context.startActivity(this)
            }
        }

        documentUploadOptionActionButtonView.setCaptionText(documentUploadStatus.name)
        documentUploadOptionActionButtonView.setOnClickListener {
            manageProfileFileOptions.openFile()
        }
    }
}