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

import com.barclays.absa.banking.boundary.model.airtime.AirtimeOnceOffConfirmation
import com.barclays.absa.banking.buy.services.airtime.PrepaidService.OP0618_ONCE_OFF_AIRTIME_CONFIRM
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class OnceOffAirtimeConfirmRequest<T>(type: String, ownAmountSelected: String, selectedVoucherCode: String,
                                      airtimeOnceOffConfirmation: AirtimeOnceOffConfirmation,
                                      responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        airtimeOnceOffConfirmation.let {
            params = RequestParams.Builder()
                    .put(OP0618_ONCE_OFF_AIRTIME_CONFIRM)
                    .put(Transaction.SERVICE_FROM_ACCOUNT_AIRTIME, it.fromAccount)
                    .put(Transaction.SERVICE_FROM_ACCOUNT_TYPE, it.description)
                    .put(Transaction.SERVICE_CELL_NUMBER_AIRTIME, it.cellNumber)
                    .put(Transaction.SERVICE_RC_TYPE, type)
                    .put(Transaction.SERVICE_RC_AMOUNT, it.amount?.getAmount())
                    .put(Transaction.SERVICE_NETWORK_PROVIDER_NAME_BUY, it.voucherDescription)
                    .put(Transaction.SERVICE_OWN_AMOUNT_AIRTIME, ownAmountSelected)
                    .put(Transaction.SERVICE_NETWORK_PROVIDER, selectedVoucherCode)
                    .build()
        }

        mockResponseFile = "airtime/op0618_once_off_airtime_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AirtimeOnceOffConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}