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

package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2032_VALIDATE_CUSTOMER_ADDRESS
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails
import com.barclays.absa.banking.newToBank.services.dto.ValidateCustomerAddressResponse

class NewToBankValidateCustomerAddressRequest<T>(userDetails: AddressDetails, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2032_VALIDATE_CUSTOMER_ADDRESS)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.ADDRESS_LINE_1, userDetails.addressLine1.toUpperCase())
                .put(Transaction.ADDRESS_LINE_2, userDetails.addressLine2.toUpperCase())
                .put(Transaction.SUBURB, userDetails.suburb.toUpperCase())
                .put(Transaction.TOWN, userDetails.town.toUpperCase())
                .put(Transaction.POSTAL_CODE, userDetails.postalCode)
                .put(NewToBankParams.ADDRESS_CHANGED.key, userDetails.addressChanged.toString().toUpperCase())
                .put(NewToBankParams.ADDRESS_TYPE.key, userDetails.addressType)
                .build()

        mockResponseFile = "new_to_bank/op2032_validate_address.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ValidateCustomerAddressResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
