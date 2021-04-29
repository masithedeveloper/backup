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
package com.barclays.absa.banking.debiCheck.services.dto

import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.debiCheck.services.DebitOrderMockFactory
import com.barclays.absa.banking.debiCheck.services.DebitOrderService
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class DebitOrderReversePaymentRequest<T>(debitOrderDataModel: DebitOrderDataModel, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(DebitOrderService.OP2114_REVERSE_DEBIT_ORDER)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(DebitOrderService.FROM_ACCOUNT_NUMBER, debitOrderDataModel.fromAccountNumber)
                .put(DebitOrderService.ACTION_DATE, debitOrderDataModel.actionDate)
                .put(DebitOrderService.USER_REFERANCE, debitOrderDataModel.userReference)
                .put(DebitOrderService.AMOUNT, debitOrderDataModel.amount)
                .put(DebitOrderService.REASON_CODE, debitOrderDataModel.reasonCode)
                .put(DebitOrderService.REASON_DESCRIPTION, debitOrderDataModel.reasonDescription)
                .put(DebitOrderService.DEBIT_TYPE, debitOrderDataModel.debitType)
                .put(DebitOrderService.STATUS, debitOrderDataModel.debitOrderStatus)
                .put(DebitOrderService.USER_CODE, debitOrderDataModel.userCode)
                .put(DebitOrderService.USER_SEQUENCE, debitOrderDataModel.userSequence)
                .put(DebitOrderService.TIEB_NUMBER, debitOrderDataModel.tiebNumber)
                .put(DebitOrderService.INSTRUCTION_NUMBER, debitOrderDataModel.instructionNumber)
                .build()
        mockResponseFile =  DebitOrderMockFactory.reverseDebitOrder()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SureCheckResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}