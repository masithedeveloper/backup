/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.payments.services.dto

import com.barclays.absa.banking.boundary.model.BankDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0790_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_REBUILD
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class BankListRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0790_ONCE_OFF_PAYMENT_OTHERBANK_AUTOTEXT_REBUILD)
                .build()

        mockResponseFile = "op0790_bank_list.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BankDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
