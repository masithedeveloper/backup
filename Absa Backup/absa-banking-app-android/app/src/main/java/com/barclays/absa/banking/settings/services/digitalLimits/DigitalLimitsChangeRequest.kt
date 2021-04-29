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
package com.barclays.absa.banking.settings.services.digitalLimits

import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeResult
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0631_CHANGE_LIMITS_CONFIRM
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class DigitalLimitsChangeRequest<T>(oldLimit: DigitalLimit, newLimit: DigitalLimit,
                                    responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0631_CHANGE_LIMITS_CONFIRM)
                .put(Transaction.OLD_VALUE_PAYMENT_CHANGE_LIMITS, oldLimit.dailyPaymentLimit.actualLimit.getAmount())
                .put(Transaction.OLD_VALUE_INTER_ACCOUNT_TRANSFER_CHANGE_LIMITS, oldLimit.dailyInterAccountTransferLimit.actualLimit.getAmount())
                .put(Transaction.OLD_VALUE_RECURRING_PAYMENT_CHANGE_LIMITS, oldLimit.recurringPaymentTransactionLimit.actualLimit.getAmount())
                .put(Transaction.OLD_VALUE_FUTURE_DATED_PAYMENTS_CHANGE_LIMITS, oldLimit.futureDatedPaymentTransactionLimit.actualLimit.getAmount())
                .put(Transaction.NEW_VALUE_PAYMENT_CHANGE_LIMITS, newLimit.dailyPaymentLimit.actualLimit.getAmount())
                .put(Transaction.NEW_VALUE_INTER_ACCOUNT_TRANSFER_CHANGE_LIMITS, newLimit.dailyInterAccountTransferLimit.actualLimit.getAmount())
                .put(Transaction.NEW_VALUE_RECURRING_PAYMENT_CHANGE_LIMITS, newLimit.recurringPaymentTransactionLimit.actualLimit.getAmount())
                .put(Transaction.NEW_VALUE_FUTURE_DATED_PAYMENTS_CHANGE_LIMITS, newLimit.futureDatedPaymentTransactionLimit.actualLimit.getAmount())
                .build()

        mockResponseFile = "profile/op0631_profile_limits_confirm.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = DigitalLimitsChangeResult::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
