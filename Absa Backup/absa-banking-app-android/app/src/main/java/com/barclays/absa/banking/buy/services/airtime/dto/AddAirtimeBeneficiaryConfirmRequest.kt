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
package com.barclays.absa.banking.buy.services.airtime.dto

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0329_ADD_AIRTIME_CONFIRM
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class AddAirtimeBeneficiaryConfirmRequest<T>(beneficiaryName: String, cellNumber: String,
                                             networkProviderName: String, networkProviderCode: String,
                                             responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0329_ADD_AIRTIME_CONFIRM)
                .put(Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryName)
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.PASS_PREPAID.toLowerCase())
                .put(Transaction.SERVICE_MY_REFERENCE, BMBConstants.PASS_PREPAID.toLowerCase())
                .put(Transaction.SERVICE_BENEFICAIRY_FAVORITE, BMBConstants.ALPHABET_N)
                .put(Transaction.SERVICE_CELL_NO_AIRTIME, cellNumber)
                .put(Transaction.SERVICE_NETWRK_PROVIDER_NAME, networkProviderName)
                .put(Transaction.SERVICE_NET_PROVIDER, networkProviderCode)
                .build()

        mockResponseFile = BeneficiariesMockFactory.addPaymentConfirmation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}