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
package com.barclays.absa.banking.account.services.dto

import android.util.Base64
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class PdfStatementResponse : ResponseObject() {

    @JsonProperty("statementByteArray")
    var base64pdf: String = ""

    var transactionStatus = ""

    var transactionMessage = ""

    private var pdfArray: ByteArray = ByteArray(0)

    override fun toString() = "$opCode:$base64pdf"

    fun getPdfArray(): ByteArray? {
        try {
            return if (base64pdf.isNotBlank()) {
                Base64.decode(base64pdf, Base64.DEFAULT).also { pdfArray = it }
            } else {
                pdfArray
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}