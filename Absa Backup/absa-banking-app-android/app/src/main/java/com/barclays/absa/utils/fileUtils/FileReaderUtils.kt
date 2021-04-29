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

import android.content.Context
import android.os.Environment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.BMBLogger
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

object FileReaderUtils {
    @JvmStatic
    fun getFileContent(fileName: String): String? {
        try {
            val inputStream = BMBApplication.getInstance().assets.open(fileName)
            val size = inputStream.available()
            val readBuffer = ByteArray(size)
            inputStream.read(readBuffer)
            inputStream.close()
            return String(readBuffer, StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun createImageFile(context: Context): File {
        var image: File? = null

        val dirs = arrayOf(
                Environment.getStorageDirectory(),
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                context.filesDir)

        for (storageDir in dirs) try {
            val imageFileName = "image_" + System.currentTimeMillis()
            if (storageDir != null) {
                storageDir.mkdirs()
                image = File.createTempFile(imageFileName, ".jpg", storageDir)
                if (image != null) {
                    return image
                }
            }
        } catch (e: IOException) {
            BMBLogger.d(e.toString())
        }
        return image ?: File("")
    }
}