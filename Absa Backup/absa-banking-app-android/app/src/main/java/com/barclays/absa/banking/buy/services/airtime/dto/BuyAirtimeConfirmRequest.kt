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

import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiaryConfirmation
import com.barclays.absa.banking.buy.services.airtime.PrepaidService
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class BuyAirtimeConfirmRequest<T>(type: String, ownAmountFlag: String, airtimeBuyBeneficiaryConfirmation: AirtimeBuyBeneficiaryConfirmation,
                                  responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        airtimeBuyBeneficiaryConfirmation.let {
            params = RequestParams.Builder()
                    .put(PrepaidService.OP0621_BUY_AIRTIME_CONFIRM)
                    .put(Transaction.SERVICE_FROM_ACCOUNT_AIRTIME, it.fromAccountNumber)
                    .put(Transaction.SERVICE_FROM_ACCOUNT_AIRTIME_TYPE, it.description)
                    .put(Transaction.SERVICE_CELL_NUMBER_AIRTIME, it.cellNumber)
                    .put(Transaction.SERVICE_RC_TYPE, type)
                    .put(Transaction.SERVICE_RC_AMOUNT, it.amount.getAmount())
                    .put(Transaction.SERVICE_BENEFICIARY_NAME_AIRTIME, it.beneficiaryName)
                    .put(Transaction.SERVICE_BENEFICIARY_ID_AIRTIME, it.beneficiaryID)
                    .put(Transaction.SERVICE_OWN_AMOUNT_AIRTIME, ownAmountFlag)
                    .put(Transaction.SERVICE_NETWORK_PROVIDER_NAME_BUY, it.voucherDescription)
                    .put(Transaction.SERVICE_NETWORK_PROVIDER, it.networkProvider)
                    .build()
        }

        mockResponseFile = "airtime/op0621_buy_airtime_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AirtimeBuyBeneficiaryConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}