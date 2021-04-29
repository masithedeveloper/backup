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

package com.barclays.absa.banking.cashSend.services.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP0310_BENEFICIARY_LIST
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class CashSendBeneficiaryListRequest<T>(extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0310_BENEFICIARY_LIST)
                .put(Transaction.SERVICE_PAYMENT_TYPE, BMBConstants.PASS_CASHSEND)
                .build()

        mockResponseFile = BeneficiariesMockFactory.beneficiaries()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BeneficiaryListObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}