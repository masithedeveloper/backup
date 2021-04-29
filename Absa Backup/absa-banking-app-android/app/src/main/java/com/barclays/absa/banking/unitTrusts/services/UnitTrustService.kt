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

package com.barclays.absa.banking.unitTrusts.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.services.dto.*
import com.barclays.absa.banking.unitTrusts.ui.buy.UnitTrustFundsAvailableToBuyExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.ui.buy.UnitTrustFundsExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.ui.view.BuyNewFundResponseListener

interface UnitTrustService {
    companion object {
        const val OP2058_RETRIEVE_AVAILABLE_UNIT_TRUST_FUNDS = "OP2058"
        const val OP2059_BUY_MORE_UNITS = "OP2059"
        const val OP2059_BUY_NEW_FUND = "OP2059"
        const val OP2060_RETRIEVE_BUY_MORE_UNITS_LINKED_ACCOUNTS = "OP2060"
        const val OP2061_RETRIEVE_DEBIT_DAYS = "OP2061"
        const val OP2064_RETRIEVE_UNIT_TRUST_FUND = "OP2064"
        const val OP2066_RETRIEVE_LINKED_ACCOUNTS = "OP2066"
        const val OP2067_CREATE_UNIT_TRUST_ACCOUNT = "OP2067"
        const val OP2068_VALIDATE_LUMP_SUM = "OP2068"
        const val OP2083_BUY_MORE_UNITS_CAPPED = "OP2083"
        const val OP2095_UNITS_REDEMPTION_ACCOUNT_STATUS = "OP2095"
        const val OP2096_REDEEM_FUND = "OP2096"
    }

    fun fetchUnitTrustFunds(extendedResponseListener: UnitTrustFundsExtendedResponseListener)
    fun fetchUnitTrustFundsAvailableToBuy(unitTrustAccountNumber: String, extendedResponseListener: UnitTrustFundsAvailableToBuyExtendedResponseListener)
    fun fetchLinkedAccounts(extendedResponseListener: ExtendedResponseListener<LinkedAccountsWrapper>)
    fun validateLumpSumAmount(lumpSumInfo: LumpSumInfo, extendedResponseListener: ExtendedResponseListener<ValidationStatus>)
    fun fetchDebitDays(extendedResponseListener: ExtendedResponseListener<DebitPeriod>)
    fun createUnitTrustAccount(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: ExtendedResponseListener<UnitTrustAccountCreationResult>)
    fun fetchBuyMoreUnitsCappedStatus(fundCode: String, extendedResponseListener: ExtendedResponseListener<BuyMoreUnitsCappedResponse>)
    fun fetchBuyMoreUnitsLinkedAccounts(extendedResponseListener: ExtendedResponseListener<BuyMoreUnitsLinkedAccountsResponse>)
    fun buyMoreUnitsValidateLumpSumAmount(lumpSumInfo: BuyMoreUnitsInfo, extendedResponseListener: ExtendedResponseListener<ValidationStatus>)
    fun fetchUnitTrustRedemptionAccountStatus(accountNumber: String, extendedResponseListener: ExtendedResponseListener<UnitsTrustRedemptionAccountResponse>)
    fun fetchBuyMoreUnitsStatus(buyMoreUnitsInfo: BuyMoreUnitsInfo, sureCheckAccepted: Boolean, extendedResponseListener: ExtendedResponseListener<BuyMoreFundsResponse>)
    fun fetchRedeemFundStatus(fundRedemptionInfo: FundRedemptionInfo, extendedResponseListener: ExtendedResponseListener<RedeemFundResponse>)
    fun buyNewFund(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: BuyNewFundResponseListener)
    fun fetchSwitchUnitTrustFunds(extendedResponseListener: ExtendedResponseListener<SwitchFundsListResponse>)
    fun switchUnitTrustFund(fundSwitchInfo: FundSwitchInfo, extendedResponseListener: ExtendedResponseListener<SwitchFundResponse>)

    enum class UnitTrustParams(val key: String) {
        FUND_CODE("fundCode"),
        FUND_NAME("fundName"),
        FUND_NUMBER("fundNumber"),
        UNIT_TRUST_ACCOUNT_NAME("unitTrustAcctName"),
        LUMP_SUM_ACCOUNT_HOLDER("lumpSumAccHolder"),
        LUMP_SUM_ACCOUNT_NUMBER("lumpSumAccount"),
        LUMP_SUM_ACCOUNT_TYPE("lumpSumAccountType"),
        LUMP_SUM_AMOUNT("lumpSumAmount"),
        LUMP_SUM_AMNT("lumpSumAmnt"),
        LUMP_SUM_INDICATOR("lumpSumInd"),
        LUMP_SUM_AUTHORISED("lumpSumAuthorised"),
        DEBIT_ORDER_ACCOUNT_HOLDER("debitOrderAccHolder"),
        DEBIT_ORDER_ACCOUNT_NUMBER("debitOrderAcc"),
        DEBIT_ORDER_ACCOUNT_TYPE("debitOrderAccType"),
        DEBIT_ORDER_BANK("debitOrderBank"),
        DEBIT_ORDER_BANK_CODE("debitOrderBankCode"),
        DEBIT_ORDER_AMOUNT("debitOrderAmount"),
        DEBIT_ORDER_START_DATE("debitOrdStrtDate"),
        DEBIT_ORDER_AUTO_INCREASE("debitOrderAutoIncr"),
        DEBIT_ORDER_INDICATOR("debitOrderInd"),
        DEBIT_ORDER_ACCOUNT("debitOrdAcc"),
        DEBIT_ORDER_ACCOUNT_TYPE2("debitOrdAccType"),
        DEBIT_ORDER_ACCOUNT_NAME("debitOrdAccName"),
        DEBIT_ORDER_BANK2("debitOrdBank"),
        DEBIT_ORDER_BANK_CODE2("debitOrdBnkcd"),
        DEBIT_ORDER_AMOUNT2("debitOrdAmnt"),
        DEBIT_ORDER_START_DATE2("debitOrderStartDate"),
        DEBIT_ORDER_DAY("debitOrdDay"),
        DEBIT_ORDER_PERCENTAGE_INCREASE("percIncrease"),
        IS_REGISTERED_FOR_RSA_TAX("areYouRegistered"),
        RSA_TAX_NUMBER("saIncomeTaxNumber"),
        REASON_NOT_GIVEN_FOR_RSA_TAX("reasonNotGiven"),
        IS_REGISTERED_FOR_FOREIGN_TAX("areYouRegisteredForeignTax"),
        FOREIGN_TAX_NUMBER("foreignTaxNumber"),
        FOREIGN_TAX_COUNTRY("foreignTaxCountyr"),
        REASON_NOT_GIVEN_FOR_FOREIGN_TAX("reasonNotGivenForeign"),
        INCOME_DISTRIBUTION_ACCOUNT_HOLDER("incomeDistAccHolder"),
        INCOME_DISTRIBUTION_ACCOUNT_NUMBER("incomeDistAcc"),
        INCOME_DISTRIBUTION_TYPE("incomeDistType"),
        INCOME_DISTRIBUTION_BANK("incomeDistBank"),
        INCOME_DISTRIBUTION_BANK_CODE("incomeDistBankCode"),
        INCOME_DISTRIBUTION_ACCOUNT_NAME("incomeDistAccName"),
        INCOME_DISTRIBUTION_BANK_ACCOUNT("incomeDistBnkAcc"),
        INCOME_DISTRIBUTION_ACCOUNT_TYPE("incomeDistAccType"),
        INCOME_DISTRIBUTION_BANK_CODE2("incomeDistBnkcd"),
        INCOME_DISTRIBUTION_BANK_NAME("incomeDistBnknam"),
        REDEEM_ACCOUNT_HOLDER("redeemAccHolder"),
        REDEEM_ACCOUNT_NUMBER("redeemAcc"),
        REDEEM_ACCOUNT_TYPE("redeemAccType"),
        REDEEM_ACCOUNT_BANK("redeemAccBank"),
        REDEEM_ACCOUNT_BANK_CODE("redeemAccBankCode"),
        SOURCE_OF_FUNDS("sourceOfFunds"),
        INCOME_DISTRIBUTION_INDICATOR("incomeDistInd"),
        TRANSFER_IDENTIFIER_INCOME_DISTRIBUTION("transIdentifierIncomeDist"),
        TRANSFER_IDENTIFIER_BUY_UNIT_TRUST("transIdentifierBuyUnitTrust"),
        UNIT_TRUST_ACCOUNT_NUMBER("unitTrustAccountNumber"),
        UNIT_TRUST_ACCOUNT_HOLDER("unitTrustAccountHolder"),
        UNIT_TRUST_FUND_CODE("unitTrustFundCode"),
        AMOUNT_AVAILABLE_FOR_REDEMPTION("amountAvailableForRedemption"),
        UNITS_AVAILABLE_FOR_REDEMPTION("unitsAvailableForRedemption"),
        REDEMPTION_TYPE("redemptionType"),
        REDEMPTION_AMOUNT("redeemAmount"),
        FUND_UNITS("fundUnits"),
        ALL_UNITS("allUnits"),
        REDEEM_ALL("redeemAll"),
        REDEMPTION_ACCOUNT_NUMBER("redemptionAccountNumber"),
        REDEMPTION_ACCOUNT_HOLDER("redemptionAccountHolder"),
        ACCOUNT_TYPE("accountType"),
        REDEMPTION_ACCOUNT_BANK_NAME("redemptionAccountBankName"),
        REDEMPTION_ACCOUNT_BANK_CODE("redemptionAccountBankCode"),
        CANCEL_DEBIT_ORDER("cancelDebitOrder"),
        TRANSFER_IDENTIFIER_BUY_NEW_FUND("transIdentifierNewFund"),
        SWITCH_DEBIT_ORDER_OPERATION("debitOrderOperation"),
        SWITCH_DEBIT_ORDER_ACCOUNT("debitOrderAccount"),
        SWITCH_DEBIT_ORDER_ACCOUNT_TYPE("debitOrderAccountType"),
        SWITCH_DEBIT_ORDER_ACCOUNT_NAME("debitOrderAccountName"),
        SWITCH_DEBIT_ORDER_BANK("debitOrderBank"),
        SWITCH_DEBIT_ORDER_BANK_CODE("debitOrderBankCode"),
        SWITCH_DEBIT_ORDER_AMOUNT("debitOrderAmount"),
        SWITCH_DEBIT_ORDER_START_MONTH("debitOrderStartMonth"),
        SWITCH_INCOME_DISTRIBUTION_ACCOUNT_TYPE("incomeDistAccType"),
        SWITCH_DEBIT_ORDER_DAY("debitOrderDay"),
        SWITCH_DEBIT_ORDER_PERCENTAGE_INCREASE("debitOrderPercentageIncrease"),
        SWITCH_DEBIT_ORDER_INCREASE_EFFECTIVE_DATE("debitOrderIncreaseEffectiveDate"),
        SWITCH_INCOME_DISTRIBUTION_BANK_ACCOUNT("incomeDistBnkAcc"),
        SWITCH_INCOME_DISTRIBUTION_BANK_CODE("incomeDistBnkcd"),
        SWITCH_INCOME_DISTRIBUTION_BANK_NAME("incomeDistBnknam"),
        SWITCH_INCOME_DISTRIBUTION_ACCOUNT_NAME("incomeDistAccName"),
        SWITCH_UNIT_TRUST_ACCOUNT_NUMBER("unitTrustAccountNumber"),
        SWITCH_UNIT_TRUST_ACCOUNT_HOLDER_NAME("unitTrustAccountHolderName"),
        SWITCH_FROM_FUND_CODE("fromfundCode"),
        SWITCH_TO_FUND_CODE("tofundCode"),
        SWITCH_FROM_FUND_NAME("fromfundName"),
        SWITCH_TO_FUND_NAME("tofundName"),
        SWITCH_OPTION("switchOption"),
        UNITS_TO_SWITCH("unitsToSwitch"),
        SWITCH_INCOME_DISTRIBUTION_INDICATOR("incomeDistInd")
    }
}