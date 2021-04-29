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
package com.barclays.absa.banking.buy.services.prepaidElectricity.dto

import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity
import com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0339_PURCHASE_PREPAID_ELECTRICITY
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class PurchasePrepaidElectricityRequest<T>(purchasePrepaidElectricityResult: PurchasePrepaidElectricityResultObject,
                                           prepaidElectricityExtendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(prepaidElectricityExtendedResponseListener) {

    init {
        purchasePrepaidElectricityResult.let {
            params = RequestParams.Builder()
                    .put(OP0339_PURCHASE_PREPAID_ELECTRICITY)
                    .put(Transaction.SERVICE_BENEFICIARY_ID, it.benId)
                    .put(Transaction.SERVICE_BENEFICIARY_NAME_AIRTIME, it.benName)
                    .put(Transaction.SERVICE_FROM_ACCOUNT_NO, it.fromAccountNumber)
                    .put(Transaction.SMS_AMOUNT, it.amount)
                    .put(Transaction.ACCOUNT_TYPE, it.accountType)
                    .put(Transaction.METER_NUMBER, it.meterNumber)
                    .put(Transaction.SERVICE_PROVIDER, it.serviceProvider)
                    .put(Transaction.SERVICE_MY__NOTICE_METHOD, it.myNoticeMethod)
                    .put(Transaction.SERVICE_MY_CELL_NUMBER, it.myCellNumber)
                    .put(Transaction.SERVICE_MY_EMAIL, it.myEmail)
                    .put(Transaction.SERVICE_MY_FAX_CODE, it.myFaxCode)
                    .put(Transaction.SERVICE_MY_FAX_NUMBER, it.myFaxNumber)
                    .put(Transaction.SERVICE_BEN_NOTICE_TYPE, it.benNoticeTyp)
                    .put(Transaction.SERVICE_BENEFICIARY_CELL_NO, it.benCellNumber)
                    .put(Transaction.SERVICE_THEIR_EMAIL, it.benEmail)
                    .put(Transaction.SERVICE_THEIR_FAX_CODE, it.benFaxCode)
                    .put(Transaction.SERVICE_THEIR_FAX_NUMBER, it.benFaxNumber)
                    .build()
        }

        mockResponseFile = "electricity/op0339_purchase_prepaid_electricity.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = PrepaidElectricity::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
