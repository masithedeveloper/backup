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

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.Companion.OP2059_BUY_NEW_FUND
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.UnitTrustParams.*

class BuyNewFundRequest<T>(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    companion object {
        const val NO = "N"
        const val YES = "Y"
        const val INVEST_INTO_ACCOUNT = "I"
    }

    init {
        val unitTrustFund = unitTrustAccountInfo.unitTrustFund
        val lumpSumInfo = unitTrustAccountInfo.lumpSumInfo
        val debitOrderInfo = unitTrustAccountInfo.debitOrderInfo
        val incomeDistributionAccountInfo = unitTrustAccountInfo.incomeDistributionAccountInfo
        val redemptionAccountInfo = unitTrustAccountInfo.redemptionAccountInfo

        val isPayIntoMyAccount = incomeDistributionAccountInfo.incomeType.equals(INVEST_INTO_ACCOUNT, ignoreCase = true)
        val isInvestmentMethodDebitOrder = lumpSumInfo == null

        params = RequestParams.Builder(OP2059_BUY_NEW_FUND).apply {
            put(UNIT_TRUST_ACCOUNT_NAME.key, incomeDistributionAccountInfo.incomeAccountsHolderName)
            put(FUND_NUMBER.key, unitTrustAccountInfo.unitTrustAccountNumber)
            put(FUND_CODE.key, unitTrustFund.fundCode)
            put(FUND_NAME.key, unitTrustFund.fundName)

            if (isPayIntoMyAccount) {
                put(INCOME_DISTRIBUTION_ACCOUNT_NAME.key, incomeDistributionAccountInfo.incomeAccountsHolderName)
                put(INCOME_DISTRIBUTION_BANK_ACCOUNT.key, incomeDistributionAccountInfo.incomeAccountsNumber)
                put(INCOME_DISTRIBUTION_ACCOUNT_TYPE.key, incomeDistributionAccountInfo.incomeAccountType)
                put(INCOME_DISTRIBUTION_BANK_CODE2.key, incomeDistributionAccountInfo.incomeBankCode)
                put(INCOME_DISTRIBUTION_BANK_NAME.key, redemptionAccountInfo.redeemBankName)
                put(TRANSFER_IDENTIFIER_INCOME_DISTRIBUTION.key, "pay into account")
                put(INCOME_DISTRIBUTION_INDICATOR.key, YES)
            } else {
                put(TRANSFER_IDENTIFIER_INCOME_DISTRIBUTION.key, "reinvest")
                put(INCOME_DISTRIBUTION_INDICATOR.key, NO)
            }

            if (isInvestmentMethodDebitOrder) {
                put(DEBIT_ORDER_ACCOUNT.key, debitOrderInfo?.accountInfo?.accountNumber)
                put(DEBIT_ORDER_ACCOUNT_TYPE2.key, debitOrderInfo?.accountInfo?.accountType)
                put(DEBIT_ORDER_ACCOUNT_NAME.key, debitOrderInfo?.accountInfo?.accountHolder)
                put(DEBIT_ORDER_BANK2.key, debitOrderInfo?.accountInfo?.bankName)
                put(DEBIT_ORDER_BANK_CODE2.key, debitOrderInfo?.accountInfo?.branchCode)
                put(DEBIT_ORDER_AMOUNT2.key, debitOrderInfo?.amount)
                put(DEBIT_ORDER_START_DATE2.key, debitOrderInfo?.debitDate)
                put(DEBIT_ORDER_DAY.key, debitOrderInfo?.debitDay)
                put(DEBIT_ORDER_PERCENTAGE_INCREASE.key, debitOrderInfo?.autoIncreasePercentage)
                put(TRANSFER_IDENTIFIER_BUY_UNIT_TRUST.key, "Do-New")
            } else {
                put(LUMP_SUM_ACCOUNT_HOLDER.key, lumpSumInfo?.accountInfo?.accountHolder)
                put(LUMP_SUM_ACCOUNT_NUMBER.key, lumpSumInfo?.accountInfo?.accountNumber)
                put(LUMP_SUM_ACCOUNT_TYPE.key, lumpSumInfo?.accountInfo?.accountType)
                put(LUMP_SUM_AMNT.key, lumpSumInfo?.amount)
                put(LUMP_SUM_AUTHORISED.key, YES)
                put(TRANSFER_IDENTIFIER_BUY_UNIT_TRUST.key, "Lump Sum")
            }

            put(TRANSFER_IDENTIFIER_BUY_NEW_FUND.key, "New Fund")
        }.build()

        mockResponseFile = "unit_trust/op2059_buy_new_fund_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = BuyMoreFundsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}