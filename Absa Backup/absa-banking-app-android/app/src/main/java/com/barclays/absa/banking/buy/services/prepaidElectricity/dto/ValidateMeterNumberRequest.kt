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

import com.barclays.absa.banking.boundary.model.MeterNumberObject
import com.barclays.absa.banking.buy.services.prepaidElectricity.PrepaidElectricityService.OP0335_VALIDATE_METER_NUMBER
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class ValidateMeterNumberRequest<T>(meterNumber: String, meterNumberObjectExtendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(meterNumberObjectExtendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0335_VALIDATE_METER_NUMBER)
                .put(Transaction.METER_NUMBER, meterNumber)
                .build()

        mockResponseFile = MockFactory.validateMeterNumber()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = MeterNumberObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}