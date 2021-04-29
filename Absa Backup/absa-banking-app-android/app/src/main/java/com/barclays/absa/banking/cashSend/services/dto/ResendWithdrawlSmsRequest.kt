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

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.model.ResendWithdrawalSMSObject
import com.barclays.absa.banking.boundary.model.TransactionUnredeem
import com.barclays.absa.banking.cashSend.services.CashSendService
import com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat

class ResendWithdrawlSmsRequest<T>(isCashSendPlus: Boolean, fromAccount: String, transactionUnredeemObject: TransactionUnredeem, smsWithdrawlResponseListener: ExtendedResponseListener<T>, ) : ExtendedRequest<T>(smsWithdrawlResponseListener) {

    init {
        val paramBuilder = RequestParams.Builder()
                .put(CashSendService.OP0451_CASHSEND_RESEND_WITHDRAWAL_SMS)
                .put(Transaction.SERVICE_BEN_NAME_CASHSEND, transactionUnredeemObject.beneficiaryName)
                .put(Transaction.FROM_ACC_NO, fromAccount)
                .put(Transaction.SERVICE_CELL_NUMBER, transactionUnredeemObject.cellphoneNumber)
                .put(Transaction.SELF_REFRENCE, transactionUnredeemObject.statementTransactionDescription1)
                .put(Transaction.SMS_AMOUNT, transactionUnredeemObject.getAmountString())
                .put(Transaction.PAYMENT_NO, transactionUnredeemObject.transactionReferenceNumber)
                .put(Transaction.BENEFICIARY_PAY_REFERENCE, transactionUnredeemObject.uniqueEFT)
                .put(Transaction.BENEFICIARY_PAY_FLAG, java.lang.Boolean.FALSE.toString())
                .put(Transaction.CASH_PLUS_FLAG, java.lang.Boolean.FALSE.toString())
                .put(CASH_SEND_PLUS, isCashSendPlus.toString())
        try {
            val locale = BMBApplication.getApplicationLocale()
            val formattedDate = DateUtils.getFormattedDate(transactionUnredeemObject.transactionDateTime,
                    SimpleDateFormat("MM/dd/yyyy", locale),
                    SimpleDateFormat("yyyy-MM-dd", locale))
            paramBuilder.put(Transaction.TRANSACTION_DATE, formattedDate)
        } catch (e: ParseException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        params = paramBuilder.build()

        mockResponseFile = "cash_send/op0451_cashsend_resend_withdrawal_sms.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ResendWithdrawalSMSObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}