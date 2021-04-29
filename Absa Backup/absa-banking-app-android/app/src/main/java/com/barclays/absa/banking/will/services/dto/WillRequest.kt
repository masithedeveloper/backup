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

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.will.services.WillService.Companion.OP2115_FETCH_WILL

class WillRequest<T>(pdfRequired: Boolean, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val pdfRequiredString = if (pdfRequired) "Y" else "N"

        params = RequestParams.Builder(OP2115_FETCH_WILL)
                .put("pdfRequired", pdfRequiredString).build()

        mockResponseFile = when (pdfRequired) {
            true -> "will/op2115_will_with_pdf.json"
            else -> "will/op2115_will_without_pdf.json"
        }

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = WillResponse::class.java as Class<T>

    override fun isEncrypted() = true
}