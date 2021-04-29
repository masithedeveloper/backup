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

import com.barclays.absa.banking.boundary.model.AddPrepaidElectricityBeneficiaryObject
import com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class AddPrepaidElectricityBeneficiaryRequest<T>(beneficiaryName: String, meterNumber: String,
                                                 utilityProvider: String, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0336_ADD_PREPAID_ELECTRICITY_BENEFICIARY)
                .put(Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryName)
                .put(Transaction.METER_NUMBER, meterNumber)
                .put(Transaction.SERVICE_UTILITY_PROVIDER, utilityProvider)
                .put(Transaction.HAS_IMAGE, "N")
                .build()

        mockResponseFile = "electricity/op0336_add_prepaid_electricity_beneficiary.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddPrepaidElectricityBeneficiaryObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}