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
package com.barclays.absa.utils.imageHelpers

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showGenericErrorDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.fileUtils.FileReaderUtils.createImageFile
import java.io.ByteArrayOutputStream
import java.io.File

abstract class ImageHelper(val activity: Activity) {
    private val exceedsMarshmallow = Build.VERSION.SDK_INT > VERSION_CODES.M
    private var action = 0
    private var placeHolderImageId = 0
    private var imageUri: Uri? = null
    private var tempCroppedPhotoUri: Uri? = null

    @JvmField
    var bitmap: Bitmap? = null
    private var imageFile: File? = null

    @JvmField
    var imageView: ImageView? = null
    private var onImageActionListener: OnImageActionListener? = null
    private val packageManager: PackageManager = activity.packageManager
    var onClickListener: View.OnClickListener
    abstract val imageData: ByteArray

    companion object {
        fun onImageActionListener(function: () -> Unit) {
            function.invoke()
        }

        const val PROFILE_IMAGE_REQUEST = 11100
        const val PROFILE_IMAGE_REQUEST_AFTER_CROP = 11101
    }

    init {
        onClickListener = View.OnClickListener {
            BottomSheet.Builder(activity, R.style.BottomSheet_StyleDialog)
                    .title(activity.getString(R.string.profile_picture_chooser_title))
                    .sheet(if (hasDatabaseImage()) R.menu.menu_bottom_sheet else R.menu.menu_with_no_image).listener(OnSelectionListener())
                    .show()
        }
    }

    private inner class OnSelectionListener : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            action = which
            PermissionHelper.requestExternalStorageWritePermission(activity) { uploadImage() }
        }
    }

    constructor(activity: Activity, imageView: ImageView?) : this(activity) {
        setImageView(imageView)
    }

    private fun setImageView(imageView: ImageView?) {
        this.imageView = imageView
        this.imageView?.setOnClickListener(onClickListener)
    }

    protected fun setImageViewWithNoClickListener(imageView: ImageView?) {
        this.imageView = imageView
    }

    protected fun getDatabaseImage(imageName: String?): ByteArray {
        val addBeneficiaryDAO = AddBeneficiaryDAO(activity)
        val addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(imageName) ?: throw RuntimeException("Beneficiary Object is null")
        return addBeneficiaryObject.imageData ?: ByteArray(0)
    }

    interface OnImageActionListener {
        fun onProfileImageLoad()
    }

    fun setOnImageActionListener(onImageActionListener: OnImageActionListener?) {
        if (this.onImageActionListener == null) {
            this.onImageActionListener = onImageActionListener
        }
    }

    fun setDefaultPlaceHolderImageId(placeholderImageId: Int) {
        placeHolderImageId = placeholderImageId
    }

    protected open fun hasDatabaseImage(): Boolean {
        return try {
            val data = imageData
            data.isNotEmpty()
        } catch (e: RuntimeException) {
            false
        }
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    private fun uploadImage() {
        imageFile = createImageFile(activity)
        when (action) {
            R.id.camera -> PermissionHelper.requestCameraAccessPermission(activity) { captureProfileImage() }
            R.id.upload -> pickGalleryImage()
            R.id.delete -> deleteImage()
            else -> {
            }
        }
    }

    fun cropThumbnail(data: Intent?) {
        try {
            imageUri = if (data != null && data.data != null) {
                data.data
            } else {
                if (exceedsMarshmallow) FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile!!) else Uri.fromFile(imageFile)
            }
            val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val tempImageFile = File.createTempFile("temp", ".jpg", storageDir)
            tempCroppedPhotoUri = if (exceedsMarshmallow) FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", tempImageFile) else Uri.fromFile(tempImageFile)
            CommonUtils.performCrop(activity, imageUri, tempCroppedPhotoUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun captureProfileImage() {
        if (imageFile != null) {
            val photoURI = if (exceedsMarshmallow) FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile!!) else Uri.fromFile(imageFile)
            val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageCaptureIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            if (imageCaptureIntent.resolveActivity(packageManager) != null) {
                activity.startActivityForResult(imageCaptureIntent, PROFILE_IMAGE_REQUEST)
            } else if (activity is BaseActivity) {
                showGenericErrorDialog()
            }
        } else {
            if (activity is BaseActivity) {
                showGenericErrorDialog()
            }
        }
    }

    private fun pickGalleryImage() {
        if (imageFile == null) return
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
        if (galleryIntent.resolveActivity(packageManager) != null) {
            activity.startActivityForResult(Intent.createChooser(galleryIntent, "Complete action using"), PROFILE_IMAGE_REQUEST)
        }
    }

    private fun deleteImage() {
        if (hasDatabaseImage()) {
            showImageDeleteDialog()
        }
    }

    private fun showImageDeleteDialog() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .message(activity.getString(R.string.remove_photo))
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    imageView?.setImageResource(placeHolderImageId)
                    bitmap = null
                })
    }

    fun retrieveThumbnail(data: Intent?) {
        data?.let {
            val uri = if (data.data == null) tempCroppedPhotoUri else data.data
            bitmap = if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
                val imageDecoderSource = ImageDecoder.createSource(activity.contentResolver, uri ?: Uri.EMPTY)
                ImageDecoder.decodeBitmap(imageDecoderSource)
            } else {
                MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
            }

            imageView?.setImageBitmap(bitmap)
            onImageActionListener?.onProfileImageLoad()
        }
    }

    fun handlePermissionResults(requestCode: Int, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
                    uploadImage()
                } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
                    captureProfileImage()
                }
                PackageManager.PERMISSION_DENIED -> if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
                    PermissionHelper.requestExternalStorageWritePermission(activity) { uploadImage() }
                } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
                    PermissionHelper.requestCameraAccessPermission(activity) { captureProfileImage() }
                }
                else -> {
                }
            }
        }
    }

    fun getBitmapAsByteArray(): ByteArray {
        val bitmapOutputStream = ByteArrayOutputStream()
        getBitmap()?.compress(Bitmap.CompressFormat.PNG, 100, bitmapOutputStream)
        return bitmapOutputStream.toByteArray()
    }
}