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
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface TransferFundsService {

    companion object {
        const val OP2082_PERFORM_INTER_ACCOUNT_TRANSFER = "OP2082"
        const val FROM_ACCOUNT = "fromAccount"
        const val TO_ACCOUNT = "toAccount"
        const val AMOUNT = "amount"
        const val TRANSACTION_DATE = "transactionDate"
        const val TRANSACTION_TIME = "transactionTime"
        const val FROM_ACCOUNT_REFEERENCE = "fromAccountReference"
        const val TO_ACCOUNT_REFEERENCE = "toAccountReference"
    }

    fun validateRewardsRedemption(redeemRewardsCash: RedeemRewardsCash, responseListener: ExtendedResponseListener<RewardsRedeemConfirmation>)
    fun sendTransferRequest(transactionReferenceId: String, responseListener: ExtendedResponseListener<RewardsRedeemResult>)
}
