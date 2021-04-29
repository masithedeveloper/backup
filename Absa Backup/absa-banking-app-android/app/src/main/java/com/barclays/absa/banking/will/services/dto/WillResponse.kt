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
 *
 */

package com.barclays.absa.banking.will.services.dto

import android.util.Base64
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.IOException

class WillResponse : TransactionResponse() {

    var willAccountNumber = ""

    @JsonProperty("willDocument")
    private val willBase64pdf = ""
    val willDocument: ByteArray?
        get() {
            try {
                if (willBase64pdf.isNotEmpty()) {
                    return Base64.decode(willBase64pdf, Base64.DEFAULT)
                }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
            return null
        }
    var txnStatus = ""
}