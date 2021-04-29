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

package com.barclays.absa.banking.manage.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import com.barclays.absa.utils.ProfileManager
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


object ManageProfileFileUtils {
    private const val USER_FILES_DIRECTORY = "UserFiles"
    private const val TEMP_DIRECTORY = "temp"

    fun saveFileToLocalStorage(context: Context, userId: String, byteArray: ByteArray, fileName: String) {
        val userDirectory = File(context.cacheDir, "$USER_FILES_DIRECTORY/$userId").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        BufferedOutputStream(FileOutputStream("${userDirectory}/${fileName}")).use {
            it.write(byteArray)
            it.close()
        }
    }

    fun createUserDirectories(context: Context) {
        val file = File(context.cacheDir, USER_FILES_DIRECTORY).apply {
            if (!exists()) {
                mkdir()
            }
        }
        createIndividualDirectoriesForEachUser(file)
    }

    private fun createIndividualDirectoriesForEachUser(parentDirectory: File) {
        ProfileManager.getInstance().userProfiles.forEach {
            File(parentDirectory, it.userId.toString()).apply {
                if (!exists()) {
                    mkdir()
                }
            }
        }
    }

    fun removeInternalFilesOlderThanSevenDays(context: Context) {
        File(context.cacheDir, USER_FILES_DIRECTORY).walkTopDown().forEach {
            if (it.isFile && (it.lastModified() < (it.lastModified() - TimeUnit.DAYS.toMillis(7)))) {
                it.deleteRecursively()
            }
        }
    }

    fun fetchFileName(context: Context, uri: Uri): String {
        var fileName = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null, null)

        cursor.use {
            if (it?.moveToFirst() == true) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }

        if (fileName.isEmpty()) {
            fileName = uri.path.toString().substringAfterLast("/")
        }

        return fileName
    }

    fun createLocalFileAndReturnFileObject(context: Context, userId: String, byteArray: ByteArray, fileName: String): File {
        createUserDirectories(context)
        val baseDirectory = File(context.cacheDir, "$USER_FILES_DIRECTORY/$userId")
        val tempDirectory = File(baseDirectory, TEMP_DIRECTORY).apply {
            if (!exists()) {
                mkdir()
            }
        }

        BufferedOutputStream(FileOutputStream("$tempDirectory/$fileName")).use {
            it.write(byteArray)
            it.close()
        }

        return File(tempDirectory, fileName)
    }

    fun removePreviewedFiles(context: Context, userId: String) {
        File(context.cacheDir, "$USER_FILES_DIRECTORY/$userId/$TEMP_DIRECTORY").walkTopDown().forEach {
            if (it.isFile) {
                it.deleteRecursively()
            }
        }
    }

    fun removeAllTempFilesForManageProfile(context: Context) {
        File(context.cacheDir, USER_FILES_DIRECTORY).walkTopDown().forEach {
            if (it.isDirectory && it.name.contains(TEMP_DIRECTORY)) {
                it.deleteRecursively()
            }
        }
    }

    fun compressImage(imageByteArray: ByteArray): ByteArray {
        ByteArrayOutputStream().use {
            val options = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size, options)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
            return it.toByteArray()
        }
    }
}