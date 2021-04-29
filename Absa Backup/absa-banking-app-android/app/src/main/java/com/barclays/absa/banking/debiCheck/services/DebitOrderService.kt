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
package com.barclays.absa.banking.debiCheck.services

import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface DebitOrderService {
    companion object {
        const val OP2110_SEARCH_DEBIT_ORDER = "OP2110"
        const val OP2111_VIEW_STOPPED_DEBIT_ORDER = "OP2111"
        const val OP2113_STOP_DEBIT_ORDER = "OP2113"
        const val OP2114_REVERSE_DEBIT_ORDER = "OP2114"
        const val FROM_ACCOUNT_NUMBER = "fromAccountNumber"
        const val FROM_DATE = "fromDate"
        const val TO_DATE = "toDate"
        const val FILTER_BY_TYPE = "filterByType"
        const val ACTION_DATE = "actionDate"
        const val USER_REFERANCE = "userReferance"
        const val USER_REFERENCE = "userReference"
        const val AMOUNT = "amount"
        const val STOP_PAYMENT_TYPE = "stopPaymentType"
        const val STATUS = "status"
        const val USER_CODE = "userCode"
        const val USER_SEQUENCE = "userSeq"
        const val TIEB_NUMBER = "tiebNumber"
        const val INSTRUCTION_NUMBER = "instractionNo"
        const val REASON_CODE = "reasonCode"
        const val REASON_DESCRIPTION = "reasonDescription"
        const val DEBIT_TYPE = "debitType"
    }

    fun fetchDebitOrders(debitOrderDataModel: DebitOrderDataModel, debitOrderExtendedResponseListener: ExtendedResponseListener<DebitOrderList>)
    fun fetchStoppedDebitOrders(debitOrderDataModel: DebitOrderDataModel, debitOrderStoppedPaymentExtendedResponseListener: ExtendedResponseListener<DebitOrderList>)
    fun reverseDebitOrderPayment(debitOrderDataModel: DebitOrderDataModel, reverseDebitOrderPaymentExtendedResponseListener: ExtendedResponseListener<SureCheckResponse>)
    fun stopDebitOrderPayment(debitOrderDataModel: DebitOrderDataModel, stopDebitOrderExtendedResponseListener: ExtendedResponseListener<TransactionResponse>)
}