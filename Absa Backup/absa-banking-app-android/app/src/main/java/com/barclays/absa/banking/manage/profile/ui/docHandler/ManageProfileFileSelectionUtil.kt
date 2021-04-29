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
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.manage.profile.ui.ManageProfileActivity
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.fileUtils.FileReaderUtils
import java.io.File
import java.io.IOException

const val IMAGE_REQUEST = 11100
const val FILE_REQUEST = 12567
const val TAKE_PHOTO_REQUEST = 1190
const val SELECT_TELEPHONE_NO_REQUEST_CODE = 3
const val SELECT_FAX_NO_REQUEST_CODE = 4

class ManageProfileFileSelectionUtil(val fragment: Fragment) {
    private var packageManager: PackageManager = BMBApplication.getInstance().packageManager
    private var action = 0
    private var file: File? = null

    fun selectFileDialog() {
        BottomSheet.Builder(fragment.context, R.style.BottomSheet_StyleDialog)
                .title(fragment.getString(R.string.profile_picture_chooser_title))
                .sheet(R.menu.menu_bottom_sheet_document_upload)
                .listener { _, which ->
                    action = which
                    PermissionHelper.requestExternalStorageWritePermission(fragment.activity) { this@ManageProfileFileSelectionUtil.uploadImage() }
                }
                .show()
    }

    private fun uploadImage() {
        try {
            file = FileReaderUtils.createImageFile(fragment.requireContext())
        } catch (e: IOException) {
            BMBLogger.e(e.toString())
        }

        when (action) {
            R.id.camera -> PermissionHelper.requestCameraAccessPermission(fragment.activity) { captureImage() }
            R.id.uploadPhoto -> PermissionHelper.requestFileSystemAccessPermission(fragment.activity) { pickGalleryImage() }
            R.id.uploadDocument -> PermissionHelper.requestFileSystemAccessPermission(fragment.activity) {
                pickFileFromPhone()
            }
        }
    }

    private fun captureImage() {
        (fragment.activity as ManageProfileActivity).captureImage()
    }

    private fun pickGalleryImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (file != null) {
            galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            if (galleryIntent.resolveActivity(packageManager) != null) {
                fragment.activity?.startActivityForResult(Intent.createChooser(galleryIntent, fragment.getString(R.string.manage_profile_complete_action_using)), IMAGE_REQUEST)
            }
        }
    }

    private fun pickFileFromPhone() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val mimeTypes = arrayOf("image/*", "application/pdf")
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            fragment.activity?.startActivityForResult(this, FILE_REQUEST)
        }
    }
}