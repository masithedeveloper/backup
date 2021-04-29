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
 *
 */

package com.barclays.absa.banking.shared.viewer

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider.getUriForFile
import com.barclays.absa.utils.ProfileManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

private const val USER_FILES_DIRECTORY = "UserFiles"

fun Context.writeFile(fileName: String, fileContent: ByteArray): Uri? {
    val userId = ProfileManager.getInstance().activeUserProfile.userId ?: return null
    val userDirectory = File(cacheDir, "$USER_FILES_DIRECTORY/$userId")
    val userFile = with(userDirectory) {
        if (!exists()) {
            mkdirs()
        }
        File("${this}/${fileName}")
    }
    with(BufferedOutputStream(FileOutputStream(userFile))) {
        write(fileContent)
        close()
    }
    return uriFromFile(userFile)
}

private fun Context.uriFromFile(file: File): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        getUriForFile(this, "$packageName.provider", file)
    } else {
        Uri.fromFile(file)
    }
}