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
package com.barclays.absa.banking.rewards.services.dto

import com.barclays.absa.banking.boundary.model.rewards.RewardMembershipDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

class RewardsMembershipDetailsValidateRequest<T>(responseListener: ExtendedResponseListener<T>,
                                                 accountNumber: String, debitOrderFrequency: String,
                                                 debitDay: String) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder().put(OP0917_REDEEM_REWARDS_MEMBERSHIP_DETAILS_UPDATE)
                .put(Transaction.FROM_ACCOUNT, accountNumber)
                .put(Transaction.CHARGE_FREQUENCY_ID, debitOrderFrequency)
                .put(Transaction.ORDER_FREQUENCY_DATE, debitDay)
                .build()

        mockResponseFile = "op0917_membership_details_validate.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardMembershipDetails::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}