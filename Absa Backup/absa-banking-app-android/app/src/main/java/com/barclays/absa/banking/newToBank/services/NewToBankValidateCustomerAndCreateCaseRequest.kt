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
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails
import com.barclays.absa.banking.newToBank.services.dto.ValidateCustomerAndCreateCaseResponse

class NewToBankValidateCustomerAndCreateCaseRequest<T>(userDetails: CustomerDetails, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE)
                .put(Transaction.SERVICE_CHANNEL_IND, "S")
                .put(Transaction.ID_NUMBER, userDetails.idNumber)
                .put(Transaction.EMAIL, userDetails.email)
                .put(NewToBankParams.NTB_SURNAME.key, userDetails.surname)
                .put(NewToBankParams.NTB_CELLPHONE_NUMBER.key, userDetails.cellphoneNumber)
                .put(NewToBankParams.CLIENT_TYPE_GROUP.key, userDetails.clientTypeGroup)
                .build()

        mockResponseFile = "new_to_bank/op2034_validate_customer_and_create_case.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ValidateCustomerAndCreateCaseResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}