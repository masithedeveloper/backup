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

import android.os.Bundle
import com.barclays.absa.banking.boundary.model.ATMAccessPINConfirmObject
import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.cashSend.services.CashSendService.OP0327_CASHSEND_UPDATE_ATM_PIN
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class UpdateCashSendPinRequest<T>(isCashSendPlus: Boolean, pinObject: PINObject, passParam: Bundle, updatePinResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(updatePinResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0327_CASHSEND_UPDATE_ATM_PIN)
                .put(Transaction.SERVICE_BENEFICIARY_ID, passParam.getString(BMBConstants.SERVICE_BENEFICIARY_ID))
                .put(Transaction.SERVICE_BENEFICIARY_ATM_PIN_CASHSEND, passParam.getString(BMBConstants.SERVICE_BENEFICIARY_ATM_PIN_CASHSEND))
                .put(Transaction.SERVICE_FROM_ACCT_NO, passParam.getString(BMBConstants.SERVICE_FROM_ACCOUNT))
                .put(Transaction.SERVICE_REF_NO, passParam.getString(BMBConstants.SERVICE_REF_NO))
                .put(Transaction.SERVICE_BENEFICIARY_BEN_CEL_NO_CASHSEND, passParam.getString(BMBConstants.SERVICE_BENEFICIARY_BEN_CEL_NO_CASHSEND))
                .put(Transaction.SERVICE_BENEFICIARY_NAME, passParam.getString(BMBConstants.SERVICE_BENEFICIARY_NAME))
                .put(Transaction.SERVICE_BENEFICIARY_SUR_NAME, passParam.getString(BMBConstants.SERVICE_BENEFICIARY_SUR_NAME))
                .put(Transaction.SERVICE_BEN_SHORT_NAME, passParam.getString(BMBConstants.SERVICE_BEN_SHORT_NAME))
                .put(Transaction.SERVICE_TXN_AMOUNT, passParam.getString(BMBConstants.SERVICE_BAL))
                .put(Transaction.SERVICE_MY_REFERENCE, passParam.getString(BMBConstants.SERVICE_MY_REFERENCE))
                .put(Transaction.SERVICE_UNIQUE_EFT, passParam.getString(BMBConstants.SERVICE_UNIQUE_EFT))
                .put(Transaction.SERVICE_TXN_DATE, passParam.getString(BMBConstants.SERVICE_DT))
                .put(Transaction.SERVICE_VIRTUAL_SERVER_ID, pinObject.mapId)
                .put(Transaction.SERVICE_SESSION_KEY_ID, pinObject.virtualSessionId)
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
                .build()

        mockResponseFile = "cash_send/op0327_update_cashsend_pin.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ATMAccessPINConfirmObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}