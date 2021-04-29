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

import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOff
import com.barclays.absa.banking.buy.services.airtime.OnceOffAirtimeResponseParser
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0617_ONCE_OFF_AIRTIME
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.ACCOUNT_CACHING_NO

class OnceOffAirtimeVoucherRequest<T>(responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        val paramBuilder = RequestParams.Builder()
                .put(OP0617_ONCE_OFF_AIRTIME)
                .put(Transaction.SERVICE_BENEFICIARY_OPTION_TYPE, BMBConstants.PASS_PREPAID.toLowerCase(BMBApplication.getApplicationLocale()))
                .put(Transaction.IS_CUST_ACTS_REQ, ACCOUNT_CACHING_NO)
        params = paramBuilder.build()

        mockResponseFile = "airtime/op0617_with_mtn_social_bundles.json"
        responseParser = OnceOffAirtimeResponseParser()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AirtimeOnceOff::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}