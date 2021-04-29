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

package com.barclays.absa.utils.fileUtils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.app.BMBApplication
import kotlinx.coroutines.Dispatchers
import java.io.File

class ScopedStorageFileUtils {
    companion object {
        fun determineIfFileCouldBeOpened(mimeType: String): Boolean {
            with(Intent(Intent.ACTION_VIEW)) {
                this.setDataAndType(null, mimeType)
                return this.resolveActivity(BMBApplication.getInstance().packageManager) != null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageToGalleryAsyncAndroidQ(context: Context, bitmap: Bitmap): LiveData<Uri?> = liveData(Dispatchers.IO) {
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val destinationDirectory = File(Environment.DIRECTORY_PICTURES, "")
        val currentTime = System.currentTimeMillis()

        val image = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$currentTime.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "$destinationDirectory${File.separator}")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val imageUri = context.contentResolver.insert(collection, image)

        imageUri?.let {
            context.contentResolver.openOutputStream(it, "w").use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        image.clear()
        image.put(MediaStore.Images.Media.IS_PENDING, 0)

        imageUri?.let { context.contentResolver.update(it, image, null, null) }
        emit(imageUri)
    }

    fun saveImageToGalleryAsyncOlderApi(context: Context, bitmap: Bitmap): LiveData<Uri?> = liveData(Dispatchers.IO) {
        val path = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_PICTURES}"
        val currentTime = System.currentTimeMillis()
        val destinationDirectory = File(path, "$currentTime.jpg")
        val imageUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", destinationDirectory)

        imageUri?.let {
            context.contentResolver.openOutputStream(it, "w").use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, destinationDirectory.absolutePath, destinationDirectory.name)
        emit(imageUri)
    }
}