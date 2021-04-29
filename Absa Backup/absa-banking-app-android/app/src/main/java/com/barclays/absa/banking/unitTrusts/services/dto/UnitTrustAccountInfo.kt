/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.shared.services.dto.LookupItem

data class UnitTrustAccountInfo(var unitTrustFund: UnitTrustFund = UnitTrustFund(),
                                var lumpSumInfo: LumpSumInfo? = LumpSumInfo(),
                                var debitOrderInfo: DebitOrderInfo? = DebitOrderInfo(),
                                var taxInfo: TaxInfo = TaxInfo(),
                                var incomeDistributionAccountInfo: IncomeDistributionInfo = IncomeDistributionInfo(),
                                var redemptionAccountInfo: RedeemAccountInfo = RedeemAccountInfo(),
                                var sourceOfFunds: LookupItem? = LookupItem(),
                                var employmentStatus: String = "",
                                var occupation: String = "",
                                var incomeFromInvestment: String = "",
                                var unitTrustAccountNumber: String = ""
) {
    fun buildIncomeDistributionFormDebitOrder(debitOrderInfo: DebitOrderInfo, incomeDistributionChoice: String, sendAccountTypeNotAsIndicator: Boolean): IncomeDistributionInfo {
        val incomeDistribution = IncomeDistributionInfo()
        if (debitOrderInfo.accountInfo != null) {
            incomeDistribution.incomeAccountsNumber = debitOrderInfo.accountInfo?.accountNumber
            incomeDistribution.incomeAccountsHolderName = debitOrderInfo.accountInfo?.accountHolder
            incomeDistribution.description = debitOrderInfo.accountInfo?.description
            if (sendAccountTypeNotAsIndicator) {
                incomeDistribution.incomeAccountType = getAccountTypeIndicator(debitOrderInfo.accountInfo?.accountType!!)
            }
            incomeDistribution.incomeAccountType = debitOrderInfo.accountInfo?.accountType!!
        }
        incomeDistribution.incomeType = incomeDistributionChoice
        return incomeDistribution
    }

    fun getAccountTypeIndicator(accountType: String?): String {
        return if (CURRENT_ACCOUNT.equals(accountType, ignoreCase = true) || CHEQUE_ACCOUNT.equals(accountType, ignoreCase = true)) {
            CURRENT_CHEQUE_ACCOUNT_INDICATOR
        } else {
            SAVINGS_ACCOUNT_INDICATOR
        }
    }

    fun buildIncomeDistributionFormLumpSum(lumpSumInfo: LumpSumInfo, incomeDistributionChoice: String, sendAccountTypeNotAsIndicator: Boolean): IncomeDistributionInfo {
        val incomeDistribution = IncomeDistributionInfo()
        incomeDistribution.incomeAccountsNumber = lumpSumInfo.accountInfo?.accountNumber
        incomeDistribution.incomeAccountsHolderName = lumpSumInfo.accountInfo?.accountHolder
        incomeDistribution.incomeType = incomeDistributionChoice
        incomeDistribution.description = lumpSumInfo.accountInfo?.description
        incomeDistribution.incomeAccountType = getAccountTypeIndicator(lumpSumInfo.accountInfo?.accountType!!)
        if (sendAccountTypeNotAsIndicator) {
            incomeDistribution.incomeAccountType = lumpSumInfo.accountInfo?.accountType!!
        }
        return incomeDistribution
    }

    fun buildRedeemAccountInformation(): RedeemAccountInfo {
        val redeemAccountInfo = RedeemAccountInfo()
        if (debitOrderInfo?.indicator.equals(YES_INDICATOR, ignoreCase = true)) {
            redeemAccountInfo.redeemAccountsNumber = debitOrderInfo?.accountInfo?.accountNumber
            redeemAccountInfo.redeemAccountsHolderName = debitOrderInfo?.accountInfo?.accountHolder
            if (debitOrderInfo?.accountInfo?.accountType.equals(CURRENT_ACCOUNT, ignoreCase = true) || debitOrderInfo?.accountInfo?.accountType.equals(CHEQUE_ACCOUNT, ignoreCase = true)) {
                redeemAccountInfo.redeemAccountType = CURRENT_CHEQUE_ACCOUNT_INDICATOR
            } else {
                redeemAccountInfo.redeemAccountType = SAVINGS_ACCOUNT_INDICATOR
            }
        }

        if (lumpSumInfo?.indicator.equals(YES_INDICATOR, ignoreCase = true)) {
            if (!lumpSumInfo?.accountInfo?.accountType.equals(REWARDS_ACCOUNT, ignoreCase = true)) {
                redeemAccountInfo.redeemAccountsNumber = lumpSumInfo?.accountInfo?.accountNumber
                redeemAccountInfo.redeemAccountsHolderName = lumpSumInfo?.accountInfo?.accountHolder
                if (lumpSumInfo?.accountInfo?.accountType.equals(CURRENT_ACCOUNT, ignoreCase = true) || lumpSumInfo?.accountInfo?.accountType.equals(CHEQUE_ACCOUNT, ignoreCase = true)) {
                    redeemAccountInfo.redeemAccountType = CURRENT_CHEQUE_ACCOUNT_INDICATOR
                } else {
                    redeemAccountInfo.redeemAccountType = SAVINGS_ACCOUNT_INDICATOR
                }
            }
        }
        return redeemAccountInfo
    }

    companion object {
        const val CHEQUE_ACCOUNT = "chequeAccount"
        const val CURRENT_ACCOUNT = "currentAccount"
        const val REWARDS_ACCOUNT = "rewardsAccount"
        const val CURRENT_CHEQUE_ACCOUNT_INDICATOR = "C"
        const val SAVINGS_ACCOUNT_INDICATOR = "S"
        const val YES_INDICATOR = "Y"
    }
}