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

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.payments.services.RewardsRedemptionService.OP0912_REDEEM_REWARDS_CONFIRM
import com.barclays.absa.banking.rewards.services.RewardsMockFactory
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionInfo

class RedeemVoucherRequest<T>(voucherRedemptionInfo: VoucherRedemptionInfo,
                              responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        params = RequestParams.Builder()
                .put(OP0912_REDEEM_REWARDS_CONFIRM)
                .put(Transaction.REDEMPTION_CODE, "RWTRET")
                .put(Transaction.REDEMPTION_ID, voucherRedemptionInfo.vendorId)
                .put(Transaction.REDEMPTION_AMOUNT, voucherRedemptionInfo.fixedAmount?.getAmount())
                .put(Transaction.SERVICE_CELL_NUMBER, voucherRedemptionInfo.cellNumber)
                .build()

        mockResponseFile = RewardsMockFactory.buyVoucherConfirmation()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RewardsRedeemConfirmation::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}