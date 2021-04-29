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

package com.barclays.absa.banking.bankConfirmationLetter.service

import com.barclays.absa.banking.bankConfirmationLetter.service.BankConfirmationLetterService.Companion.OP2178_FETCH_BANK_CONFIRMATION_LETTER
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class BankConfirmationLetterRequest<T>(accountNumber: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2178_FETCH_BANK_CONFIRMATION_LETTER)
                .put("accountNumber", accountNumber)
                .build()

        mockResponseFile = "bank_confirmation_letter/op2178_fetch_bank_confirmation_letter.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BankConfirmationLetterResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}