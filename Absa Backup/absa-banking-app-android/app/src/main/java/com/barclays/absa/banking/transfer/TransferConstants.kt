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

interface TransferConstants {
    companion object {
        const val FROM_ACCOUNT = "fromAccount"
        const val IS_FROM_REWARDS = "isFromRewards"
        const val ABSA_REWARDS = "absaReward"
        const val HOME_LOAN_ACCOUNT_OBJECT = "homeLoanAccountObject"
        const val AVAF_ACCOUNT_OBJECT = "AVAFAccountObject"
        const val MINIMUM_PAYABLE_AMOUNT = "MINIMUM_PAYABLE_AMOUNT"
        const val CREDIT_CARD = "CREDIT_CARD"
        const val REQUEST_CODE_TRANSFER = 100
        const val IS_TRANSFER_SUCCESSFUL = "isTransferSuccessful"
        const val IS_FUTURE_TRANSFER = "isFutureTransfer"
    }
}