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
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.Companion.OP2067_CREATE_UNIT_TRUST_ACCOUNT
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.UnitTrustParams

class UnitTrustAccountCreationRequest<T>(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val unitTrustFund = unitTrustAccountInfo.unitTrustFund
        val lumpSumInfo = unitTrustAccountInfo.lumpSumInfo
        val debitOrderInfo = unitTrustAccountInfo.debitOrderInfo
        val taxInfo = unitTrustAccountInfo.taxInfo
        val incomeDistributionAccountInfo = unitTrustAccountInfo.incomeDistributionAccountInfo
        val redemptionAccountInfo = unitTrustAccountInfo.redemptionAccountInfo
        val noValue = "N"

        var incomeDistBank = ""
        if (!incomeDistributionAccountInfo.incomeAccountType.isNullOrEmpty()) {
            incomeDistBank = incomeDistributionAccountInfo.incomeAccountType?.get(0)?.toUpperCase().toString()
        }

        params = RequestParams.Builder()
                .put(OP2067_CREATE_UNIT_TRUST_ACCOUNT)
                .put(UnitTrustParams.FUND_CODE.key, unitTrustFund.fundCode)
                .put(UnitTrustParams.FUND_NAME.key, unitTrustFund.fundName)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_HOLDER.key, lumpSumInfo?.accountInfo?.accountHolder)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_NUMBER.key, lumpSumInfo?.accountInfo?.accountNumber)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_TYPE.key, lumpSumInfo?.accountInfo?.accountType)
                .put(UnitTrustParams.LUMP_SUM_AMOUNT.key, lumpSumInfo?.amount)
                .put(UnitTrustParams.LUMP_SUM_INDICATOR.key, if (lumpSumInfo == null) noValue else lumpSumInfo.indicator)
                .put(UnitTrustParams.DEBIT_ORDER_ACCOUNT_HOLDER.key, debitOrderInfo?.accountInfo?.accountHolder)
                .put(UnitTrustParams.DEBIT_ORDER_ACCOUNT_NUMBER.key, debitOrderInfo?.accountInfo?.accountNumber)
                .put(UnitTrustParams.DEBIT_ORDER_ACCOUNT_TYPE.key, unitTrustAccountInfo.getAccountTypeIndicator(debitOrderInfo?.accountInfo?.accountType))
                .put(UnitTrustParams.DEBIT_ORDER_BANK.key, debitOrderInfo?.accountInfo?.bankName)
                .put(UnitTrustParams.DEBIT_ORDER_BANK_CODE.key, debitOrderInfo?.accountInfo?.branchCode)
                .put(UnitTrustParams.DEBIT_ORDER_AMOUNT.key, debitOrderInfo?.amount)
                .put(UnitTrustParams.DEBIT_ORDER_START_DATE.key, debitOrderInfo?.debitDate)
                .put(UnitTrustParams.DEBIT_ORDER_AUTO_INCREASE.key, debitOrderInfo?.autoIncreasePercentage)
                .put(UnitTrustParams.DEBIT_ORDER_INDICATOR.key, if (debitOrderInfo == null) noValue else debitOrderInfo.indicator)
                .put(UnitTrustParams.IS_REGISTERED_FOR_RSA_TAX.key, taxInfo.isRegisteredForSATax)
                .put(UnitTrustParams.RSA_TAX_NUMBER.key, taxInfo.saTaxNumber)
                .put(UnitTrustParams.REASON_NOT_GIVEN_FOR_RSA_TAX.key, taxInfo.reasonNotGivenForSATax)
                .put(UnitTrustParams.IS_REGISTERED_FOR_FOREIGN_TAX.key, taxInfo.isRegisteredForForeignTax)
                .put(UnitTrustParams.FOREIGN_TAX_NUMBER.key, taxInfo.foreignTaxNumber)
                .put(UnitTrustParams.FOREIGN_TAX_COUNTRY.key, taxInfo.foreignTaxCountry)
                .put(UnitTrustParams.REASON_NOT_GIVEN_FOR_FOREIGN_TAX.key, taxInfo.reasonNotGivenForForeignTax)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_ACCOUNT_HOLDER.key, incomeDistributionAccountInfo.incomeAccountsHolderName)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_ACCOUNT_NUMBER.key, incomeDistributionAccountInfo.incomeAccountsNumber)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_TYPE.key, incomeDistributionAccountInfo.incomeType)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_BANK.key, incomeDistBank)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_BANK_CODE.key, incomeDistributionAccountInfo.incomeBankCode)
                .put(UnitTrustParams.REDEEM_ACCOUNT_HOLDER.key, redemptionAccountInfo.redeemAccountsHolderName)
                .put(UnitTrustParams.REDEEM_ACCOUNT_NUMBER.key, redemptionAccountInfo.redeemAccountsNumber)
                .put(UnitTrustParams.REDEEM_ACCOUNT_TYPE.key, redemptionAccountInfo.redeemAccountType)
                .put(UnitTrustParams.REDEEM_ACCOUNT_BANK.key, redemptionAccountInfo.redeemBankName)
                .put(UnitTrustParams.REDEEM_ACCOUNT_BANK_CODE.key, redemptionAccountInfo.redeemBankCode)
                .put(UnitTrustParams.SOURCE_OF_FUNDS.key, unitTrustAccountInfo.sourceOfFunds?.itemCode)
                .build()
        mockResponseFile = "unit_trust/op2067_buy_unit_trust_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = UnitTrustAccountCreationResult::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}