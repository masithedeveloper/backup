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

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0613_VALIDATE_CASHSEND_TO_SELF
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_N
import com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_Y

open class CashSendConfirmationRequest<T>(private val cashSendBeneficiaryConfirmation: CashSendBeneficiaryConfirmation,
                                          private val pinObject: PINObject, private val termsAccepted: Boolean, private val isCashSendPlus: Boolean,
                                          responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    fun buildRequestParams(): RequestParams.Builder {
        val amount = (cashSendBeneficiaryConfirmation.amount ?: Amount()).getAmount()
        return RequestParams.Builder()
                .put(OP0613_VALIDATE_CASHSEND_TO_SELF)
                .put(Transaction.SERVICE_CELL_NO_CASHSEND, cashSendBeneficiaryConfirmation.cellNumber)
                .put(Transaction.SERVICE_FROM_ACCOUNT_CASHSEND, cashSendBeneficiaryConfirmation.fromAccountNumber)
                .put(Transaction.SERVICE_ACCOUNT_TYPE_KEY, cashSendBeneficiaryConfirmation.accountType)
                .put(Transaction.SERVICE_AMOUNT_CASHSEND, amount)
                .put(Transaction.SERVICE_MY_REF_CASHSEND, cashSendBeneficiaryConfirmation.myReference)
                .put(Transaction.SERVICE_ACCEPT_TERMS_CASHSEND, if (termsAccepted) ALPHABET_Y else ALPHABET_N)
                .put(Transaction.SERVICE_ACCESSPIN_CASHSEND, pinObject.accessPin)
                .put(Transaction.SERVICE_MAPID_CASHSEND, pinObject.mapId)
                .put(Transaction.SERVICE_VIRTUALSESSID_CASHSEND, pinObject.virtualSessionId)
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CashSendBeneficiaryConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}