/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.transfer.services

import com.barclays.absa.banking.boundary.model.rewards.RedeemRewardsCash
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemResult
import com.barclays.absa.banking.buy.services.airtime.dto.RedeemRewardsResultRequest
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.rewards.services.dto.RedeemRewardsCashTransactionRequest

class TransferFundsInteractor : AbstractInteractor(), TransferFundsService {
    override fun validateRewardsRedemption(redeemRewardsCash: RedeemRewardsCash, responseListener: ExtendedResponseListener<RewardsRedeemConfirmation>) {
        submitRequest(RedeemRewardsCashTransactionRequest(redeemRewardsCash, responseListener))
    }

    override fun sendTransferRequest(transactionReferenceId: String, responseListener: ExtendedResponseListener<RewardsRedeemResult>) {
        submitRequest(RedeemRewardsResultRequest(transactionReferenceId, responseListener))
    }
}