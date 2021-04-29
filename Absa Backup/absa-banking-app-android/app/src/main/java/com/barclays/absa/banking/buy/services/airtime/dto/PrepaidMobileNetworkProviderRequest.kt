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

import com.barclays.absa.banking.boundary.model.airtime.AirtimeAddBeneficiary
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0331_PREPAID_NETWORK_PROVIDER_LIST
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class PrepaidMobileNetworkProviderRequest<T>(responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0331_PREPAID_NETWORK_PROVIDER_LIST)
                .put(Transaction.SERVICE_PAYMENT_TYPE, BMBConstants.PASS_PREPAID)
                .build()

        mockResponseFile = "beneficiaries/op0331_view_airtime_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AirtimeAddBeneficiary::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}