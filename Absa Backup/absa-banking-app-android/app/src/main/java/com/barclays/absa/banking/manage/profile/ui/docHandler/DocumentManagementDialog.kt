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

import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.fileUtils.FileReaderUtils
import java.io.File
import java.io.IOException

class DocumentManagementDialog(val fragment: Fragment, private var fileSelectionInterfaceCallback: FileSelectionInterface? = null) {
    private var action = 0
    private lateinit var file: File

    fun fileOptionsDialog() {
        BottomSheet.Builder(fragment.activity, R.style.BottomSheet_StyleDialog)
                .title(fragment.activity?.getString(R.string.profile_picture_chooser_title))
                .sheet(R.menu.menu_bottom_sheet_file_options)
                .listener { _, which ->
                    action = which
                    PermissionHelper.requestExternalStorageWritePermission(fragment.activity) { this@DocumentManagementDialog.uploadImage() }
                }
                .show()
    }

    private fun uploadImage() {
        try {
            file = FileReaderUtils.createImageFile(fragment.requireActivity())
        } catch (e: IOException) {
            BMBLogger.e(e.toString())
        }

        when (action) {
            R.id.previewFile -> PermissionHelper.requestCameraAccessPermission(fragment.activity) {
                fileSelectionInterfaceCallback?.openFile()
            }
            R.id.removeFile -> PermissionHelper.requestFileSystemAccessPermission(fragment.activity) {
                fileSelectionInterfaceCallback?.fileToBeRemoved()
            }
            R.id.chooseNewFile -> PermissionHelper.requestFileSystemAccessPermission(fragment.activity) {
                ManageProfileFileSelectionUtil(fragment).selectFileDialog()
            }
        }
    }
}