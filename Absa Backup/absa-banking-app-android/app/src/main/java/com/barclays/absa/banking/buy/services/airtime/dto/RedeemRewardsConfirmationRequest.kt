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
package com.barclays.absa.banking.buy.services.airtime.dto

import android.os.Bundle

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction

import com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM

class RedeemRewardsConfirmationRequest<T> : ExtendedRequest<T> {

    constructor(bundle: Bundle, responseListener: ExtendedResponseListener<T>) : super(responseListener) {
        params = RequestParams.Builder()
                .put(OP0912_REDEEM_REWARDS_CONFIRM)
                .put(Transaction.REDEMPTION_CODE, bundle.getString("REDEEM_METHOD"))
                .put(Transaction.REDEMPTION_ID, bundle.getString("REDEMPTION_ID"))
                .put(Transaction.SERVICE_CELL_NUMBER, bundle.getString("PHONE_NUMBER"))
                .put(Transaction.REDEMPTION_AIRTIME_NETWORK_NAME, bundle.getString("NETWORK_OPERATOR"))
                .put(Transaction.REDEMPTION_AIRTIME_VOUCHER_NAME, bundle.getString("REDEMPTION_VOUCHER_NAME"))
                .put(Transaction.REDEMPTION_AIRTIME_FACEVALUE, bundle.getString("REDEMPTION_AIRTIME_FACEVALUE"))
                .put(Transaction.REDEMPTION_AIRTIME_ACTUALCOST, bundle.getString("REDEMPTION_AIRTIME_ACTUALCOST"))
                .build()

        setMockFileAndLogRequest()
    }

    constructor(rewardsRedeemConfirmation: RewardsRedeemConfirmation, responseListener: ExtendedResponseListener<T>) : super(responseListener) {
        params = RequestParams.Builder()
                .put(OP0912_REDEEM_REWARDS_CONFIRM)
                .put(Transaction.REDEMPTION_CODE, rewardsRedeemConfirmation.reedemptionCode)
                .put(Transaction.REDEMPTION_ID, rewardsRedeemConfirmation.redemptionId?.toString())
                .put(Transaction.REDEMPTION_AMOUNT, rewardsRedeemConfirmation.amount?.amountValue?.toString())
                .put(Transaction.CHARITY, rewardsRedeemConfirmation.charityName)
                .build()

        setMockFileAndLogRequest()
    }

    private fun setMockFileAndLogRequest() {
        mockResponseFile = "rewards/op0912_redeem_rewards_confirmation.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsRedeemConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}