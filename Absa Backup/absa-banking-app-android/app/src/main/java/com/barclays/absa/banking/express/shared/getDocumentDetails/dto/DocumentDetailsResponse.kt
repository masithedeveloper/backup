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

package com.barclays.absa.banking.express.shared.getDocumentDetails.dto

import android.util.Base64
import com.fasterxml.jackson.annotation.JsonProperty
import za.co.absa.networking.dto.BaseResponse
import java.io.IOException

class DocumentDetailsResponse : BaseResponse() {
    var fileContent: String = ""
    val fileName: String = ""
    @JsonProperty("mimeType")
    var fileType: String = ""

    val pdfDocument: ByteArray
        get() {
            try {
                if (fileContent.isNotEmpty()) {
                    return Base64.decode(fileContent, Base64.DEFAULT)
                }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
            return ByteArray(0)
        }
}