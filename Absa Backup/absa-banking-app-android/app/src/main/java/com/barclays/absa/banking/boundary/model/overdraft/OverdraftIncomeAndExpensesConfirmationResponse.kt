/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model.overdraft

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse

class OverdraftIncomeAndExpensesConfirmationResponse : TransactionResponse() {

    var respDTO: RespDTO? = null

    class RespDTO {
        var qmsDTO: QmsDTO? = null
    }

    class QmsDTO {
        var odAmount: String? = null
        var quoteRefNumber: String? = null
        var systemDecision: String? = null
        var recommendedOdDesc: String? = null
        var systemResult: String? = null
        var rejectReason: String? = null
        var quoteStatus: Boolean = false
        var quote: String? = null
        var applicationReferenceNumber: String? = null
        var productCode: String? = null
        var accountNumber: String? = null
        var accountName: String? = null
    }

    fun getOverdraftAmount(): String? {
        return respDTO?.qmsDTO?.odAmount
    }

    fun getQuoteReferenceNumber(): String? {
        return respDTO?.qmsDTO?.quoteRefNumber
    }

    fun getSystemDecision(): String? {
        return respDTO?.qmsDTO?.systemDecision
    }

    fun getSystemResult(): String? {
        return respDTO?.qmsDTO?.systemResult
    }

    fun getRejectReason(): String? {
        return respDTO?.qmsDTO?.rejectReason
    }

    fun getQuoteStatus(): Boolean {
        if (respDTO?.qmsDTO?.quoteStatus == null)
            return false
        return respDTO?.qmsDTO?.quoteStatus!!
    }
}