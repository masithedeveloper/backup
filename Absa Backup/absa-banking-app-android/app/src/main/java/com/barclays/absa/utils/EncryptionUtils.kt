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
package com.barclays.absa.utils

import com.barclays.absa.banking.framework.data.ResponseHeader
import com.barclays.absa.banking.framework.parsers.EncryptedImageResponseParser
import com.barclays.absa.banking.framework.parsers.EncryptedResponseParser

object EncryptionUtils {
    /**
     * Perform decryption of server's response if required and return ResponseHeader
     *
     * @param response The response that needs to be decrypted
     * @return The response header
     */
    @JvmStatic
    fun getDecryptedResponseHeader(response: String): ResponseHeader {
        return when {
            response.contains("\"nVal\"") and response.contains("\"data\"") -> EncryptedResponseParser().getResponseHeader(response)
            response.contains("\"iVal\"") and response.contains("\"data\"") -> EncryptedImageResponseParser().getResponseHeader(response)
            else -> ResponseHeader().apply {
                code = ResponseHeader.CODE_SUCCESS
                message = ""
                body = response
            }
        }
    }
}