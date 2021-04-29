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
package com.barclays.absa.banking.transfer

import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.framework.BaseView
import styleguide.forms.SelectorList
import za.co.absa.networking.dto.ResultMessage

interface TransferFundsContract {

    interface TransferFundsView : BaseView {
        fun populateToAccountList(listOfToAccounts: SelectorList<AccountListItem>?)
        fun populateFromAccountList(listOfFromAccounts: SelectorList<AccountListItem>?)
        fun setFromAccount(fromAccount: String, availableBalance: String, index: Int)
        fun setToAccount(toAccount: String, availableBalance: String, index: Int)
        fun changeAmount(newAmount: String?)
        fun navigateToConfirmationScreenNormalTransfer()
        fun navigateToRewardsTransferConfirmationScreen(successResponse: RewardsRedeemConfirmation)
        fun setMinimumAmount(minimumAmount: String)
        fun showMinimumAmountError()
        fun showFromAccountNeededError()
        fun showToAccountNeededError()
        fun hideFutureDatedTransferTypeAndReferenceFields()
    }

    interface TransferFundsConfirmationView : BaseView {
        fun initialDataReturned(transferFundsConfirmationData: TransferConfirmationData)
        fun rewardsRedemptionSuccessResult()
        fun showSuccessfulInterAccountTransferResultScreen(transferConfirmationData: TransferConfirmationData, resultMessage: ResultMessage?)
        fun showAuthorizationRequiredScreen()
    }
}
