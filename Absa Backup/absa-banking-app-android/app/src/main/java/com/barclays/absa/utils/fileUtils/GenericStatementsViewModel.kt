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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class GenericStatementsViewModel : ViewModel() {
    private var statementsRepository = StatementsRepository(FileDataSource())

    fun writePdfFileContent(uri: Uri, fileContentByteArray: ByteArray): LiveData<Uri> = liveData(Dispatchers.IO) {
        emit(statementsRepository.writePdfFileContent(uri, fileContentByteArray))
    }

    fun writeCsvFileContent(uri: Uri, csvFileContent: String): LiveData<Uri> = liveData(Dispatchers.IO) {
        emit(statementsRepository.writeCsvFileContent(uri, csvFileContent))
    }
}