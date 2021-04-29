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

import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffConfirmation
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.cashSend.services.CashSendService.OP0610_ONCE_OFF_CASHSEND_CONFIRM
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_N
import com.barclays.absa.banking.framework.app.BMBConstants.ALPHABET_Y

class OnceOffCashSendConfirmationRequest<T>(onceOffCashSendConfirmationObject: CashSendOnceOffConfirmation,
                                            pinObject: PINObject, termsAccepted: Boolean, isCashSendPlus: Boolean,
                                            validateOnceOffCashSendResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(validateOnceOffCashSendResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0610_ONCE_OFF_CASHSEND_CONFIRM)
                .put(Transaction.SERVICE_FROM_ACCOUNT_CASHSEND, onceOffCashSendConfirmationObject.fromAccountNumber)
                .put(Transaction.SERVICE_ACCOUNT_TYPE_KEY, onceOffCashSendConfirmationObject.accountType)
                .put(Transaction.SERVICE_CELL_NO_CASHSEND, onceOffCashSendConfirmationObject.cellNumber)
                .put(Transaction.SERVICE_AMOUNT_CASHSEND, onceOffCashSendConfirmationObject.amount!!.getAmount())
                .put(Transaction.SERVICE_MY_REF_CASHSEND, onceOffCashSendConfirmationObject.myReference)
                .put(Transaction.SERVICE_FIRSTNAME_CASHSEND, onceOffCashSendConfirmationObject.firstName)
                .put(Transaction.SERVICE_SURNAME_CASHSEND, onceOffCashSendConfirmationObject.surname)
                .put(Transaction.SERVICE_NICKNAME_CASHSEND, onceOffCashSendConfirmationObject.firstName)
                .put(Transaction.SERVICE_ACCEPT_TERMS_CASHSEND, if (termsAccepted) ALPHABET_Y else ALPHABET_N)
                .put(Transaction.SERVICE_ACCESSPIN_CASHSEND, pinObject.accessPin)
                .put(Transaction.SERVICE_MAPID_CASHSEND, pinObject.mapId)
                .put(Transaction.SERVICE_VIRTUALSESSID_CASHSEND, pinObject.virtualSessionId)
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
                .build()

        mockResponseFile = "cash_send/op0610_onceoff_cashsend_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CashSendOnceOffConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}