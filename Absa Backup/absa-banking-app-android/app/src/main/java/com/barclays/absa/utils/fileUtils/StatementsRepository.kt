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

import android.net.Uri

class StatementsRepository(private val fileDataSource: FileDataSource) {
    fun writePdfFileContent(uri: Uri, fileContentByteArray: ByteArray): Uri = fileDataSource.writePdfFileContent(uri, fileContentByteArray)
    fun writeCsvFileContent(uri: Uri, csvFileContent: String): Uri = fileDataSource.writeCsvFileContent(uri, csvFileContent)
}