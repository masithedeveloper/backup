/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.payments.services.dto

import android.os.Bundle
import com.barclays.absa.banking.boundary.model.ResendNoticeOfPayment
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.payments.ProofOfPaymentHistoryActivity
import com.barclays.absa.banking.payments.services.PaymentsService.OP0926_RESEND_NOTICE_OF_PAYMENT

class ResendNoticeOfPaymentRequest<T>(responseListener: ExtendedResponseListener<T>, viewTransactionDetails: ViewTransactionDetails,
                                      bundle: Bundle) : ExtendedRequest<T>(responseListener) {
    init {
        val notificationType = bundle.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_TYPE)
        val builder = RequestParams.Builder()
                .put(OP0926_RESEND_NOTICE_OF_PAYMENT)
                .put(Transaction.ID, viewTransactionDetails.referenceNumber)
                .put(Transaction.BENEFICIARY_ID, viewTransactionDetails.beneficiaryId)
                .put(Transaction.NOTIFICATION_TYPE, notificationType)
        notificationType?.let {
            when (it) {
                BMBConstants.NOTICE_TYPE_SMS_SHORT -> builder.put(Transaction.CELLPHONE_NUMBER, bundle.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_DETAILS))
                BMBConstants.NOTICE_TYPE_EMAIL_SHORT -> builder.put(Transaction.SERVICE_EMAIL_ADDRESS, bundle.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_DETAILS))
                BMBConstants.NOTICE_TYPE_FAX_SHORT -> {
                    builder.put(Transaction.FAX_CODE, bundle.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_DETAILS))
                    builder.put(Transaction.FAX_NUMBER, bundle.getString(ProofOfPaymentHistoryActivity.FAX_NUMBER))
                }
                else -> builder.put(Transaction.CELLPHONE_NUMBER, bundle.getString(ProofOfPaymentHistoryActivity.NOTIFICATION_DETAILS))
            }
        }
        params = builder.build()

        mockResponseFile = "op0926_resend_notice_of_payment.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ResendNoticeOfPayment::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}