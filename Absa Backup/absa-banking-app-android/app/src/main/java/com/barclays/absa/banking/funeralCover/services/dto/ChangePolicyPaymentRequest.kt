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
package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyService.OP0848_CHANGE_PAYMENT_DETAILS_OPCODE
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails
import com.barclays.absa.banking.funeralCover.ui.ChangePolicyPaymentRequestParameters

class ChangePolicyPaymentRequest<T>(changePaymentDetails: ChangePaymentDetails, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0848_CHANGE_PAYMENT_DETAILS_OPCODE)
                .put(ChangePolicyPaymentRequestParameters.POLICY_NUMBER, changePaymentDetails.policyNumber)
                .put(ChangePolicyPaymentRequestParameters.ACCOUNT_HOLDER_NAME, changePaymentDetails.accountHolderName)
                .put(ChangePolicyPaymentRequestParameters.ACCOUNT_NUMBER, changePaymentDetails.accountNumber)
                .put(ChangePolicyPaymentRequestParameters.BANK_NAME, changePaymentDetails.bankName)
                .put(ChangePolicyPaymentRequestParameters.BRANCH_NAME, changePaymentDetails.branchName)
                .put(ChangePolicyPaymentRequestParameters.ACCOUNT_TYPE, changePaymentDetails.accountType)
                .put(ChangePolicyPaymentRequestParameters.DAY_OF_DEBIT, changePaymentDetails.dayOfDebit)
                .put(ChangePolicyPaymentRequestParameters.SOURCE_OF_FUNDS, String.format("%s-%s", changePaymentDetails.itemCode, changePaymentDetails.sourceOfFund))
                .build()

        mockResponseFile = "funeral_cover/op0848_change_policy_payment_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SureCheckResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}