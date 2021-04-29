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
package com.barclays.absa.banking.rewards.services.dto

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewardsCash
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM

class RedeemRewardsCashTransactionRequest<T>(redeemRewardsCash: RedeemRewardsCash,
                                             responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0912_REDEEM_REWARDS_CONFIRM)
                .put(Transaction.REDEMPTION_CODE, redeemRewardsCash.redemptionCode)
                .put(Transaction.REDEMPTION_ACCOUNT_NO, redeemRewardsCash.toAccountNumber)
                .put(Transaction.REDEMPTION_AMOUNT, redeemRewardsCash.amountToRedeem)
                .build()

        printRequest()
        mockResponseFile = "transfers/op0912_transfer_fund_rewards.json"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsRedeemConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}