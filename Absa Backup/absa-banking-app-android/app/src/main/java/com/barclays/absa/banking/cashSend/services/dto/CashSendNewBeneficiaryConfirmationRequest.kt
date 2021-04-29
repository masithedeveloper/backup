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

package com.barclays.absa.banking.cashSend.services.dto

import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject
import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class CashSendNewBeneficiaryConfirmationRequest<T>(addBeneficiaryCashSendConfirmation: AddBeneficiaryCashSendConfirmationObject,
                                                   cashSendBeneficiaryConfirmation: CashSendBeneficiaryConfirmation,
                                                   pinObject: PINObject, termsAccepted: Boolean, isCashSendPlus: Boolean,
                                                   responseListener: ExtendedResponseListener<T>)
    : CashSendConfirmationRequest<T>(cashSendBeneficiaryConfirmation, pinObject, termsAccepted, isCashSendPlus, responseListener) {

    init {
        params = super.buildRequestParams()
                .put(Transaction.SERVICE_BEN_NAME_CASHSEND, addBeneficiaryCashSendConfirmation.firstName)
                .put(Transaction.SERVICE_BEN_ID_CASHSEND, addBeneficiaryCashSendConfirmation.beneficiaryId)
                .put(Transaction.SERVICE_BEN_ACCOUNT_NUMBER_CASHSEND, addBeneficiaryCashSendConfirmation.cellNumber)
                .put(Transaction.SERVICE_CELL_NO_CASHSEND, addBeneficiaryCashSendConfirmation.cellNumber)
                .build()

        mockResponseFile = "cash_send/op0613_send_beneficiary_cashsend_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CashSendBeneficiaryConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}